package uk.co.pekim.ealing.jna.win32;

import static uk.co.pekim.ealing.jna.win32.SetupAPI.DIGCF_INTERFACEDEVICE;
import static uk.co.pekim.ealing.jna.win32.SetupAPI.DIGCF_PRESENT;
import static uk.co.pekim.ealing.jna.win32.W32API.ERROR_ACCESS_DENIED;
import static uk.co.pekim.ealing.jna.win32.W32API.ERROR_INSUFFICIENT_BUFFER;
import static uk.co.pekim.ealing.jna.win32.W32API.ERROR_IO_PENDING;
import static uk.co.pekim.ealing.jna.win32.W32API.ERROR_NO_MORE_ITEMS;
import static uk.co.pekim.ealing.jna.win32.W32API.FILE_ANY_ACCESS;
import static uk.co.pekim.ealing.jna.win32.W32API.FILE_ATTRIBUTE_NORMAL;
import static uk.co.pekim.ealing.jna.win32.W32API.FILE_DEVICE_UNKNOWN;
import static uk.co.pekim.ealing.jna.win32.W32API.FILE_FLAG_OVERLAPPED;
import static uk.co.pekim.ealing.jna.win32.W32API.GENERIC_READ;
import static uk.co.pekim.ealing.jna.win32.W32API.GENERIC_WRITE;
import static uk.co.pekim.ealing.jna.win32.W32API.INFINITE;
import static uk.co.pekim.ealing.jna.win32.W32API.INVALID_HANDLE_VALUE;
import static uk.co.pekim.ealing.jna.win32.W32API.METHOD_BUFFERED;
import static uk.co.pekim.ealing.jna.win32.W32API.OPEN_EXISTING;
import static uk.co.pekim.ealing.jna.win32.W32API.WAIT_OBJECT_0;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.pekim.ealing.AccessDeniedException;
import uk.co.pekim.ealing.DeviceException;
import uk.co.pekim.ealing.DeviceNative;
import uk.co.pekim.ealing.NoDeviceFoundException;
import uk.co.pekim.ealing.jna.win32.Kernel32.OVERLAPPED;
import uk.co.pekim.ealing.jna.win32.SetupAPI.SP_DEVICE_INTERFACE_DATA;
import uk.co.pekim.ealing.jna.win32.SetupAPI.SP_DEVICE_INTERFACE_DETAIL_DATA;
import uk.co.pekim.ealing.jna.win32.W32API.CTL_CODE;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

public class DeviceWin32JNA implements DeviceNative {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceWin32JNA.class);

    private static final SetupAPI SETUPAPI = SetupAPI.INSTANCE;
    private static final Kernel32 KERNEL32 = Kernel32.INSTANCE;

    private static final GUID GARMIN_GUID = new GUID(0x2c9c45c2L, 0x8e7d, 0x4c08, 0xa1, 0x2d, 0x81, 0x6b, 0xba, 0xe7,
            0x22, 0xc0);

    private static final CTL_CODE IOCTL_API_VERSION = new CTL_CODE(FILE_DEVICE_UNKNOWN, 0x800, METHOD_BUFFERED,
            FILE_ANY_ACCESS);
    private static final CTL_CODE IOCTL_ASYNC_IN = new CTL_CODE(FILE_DEVICE_UNKNOWN, 0x850, METHOD_BUFFERED,
            FILE_ANY_ACCESS);
    private static final CTL_CODE IOCTL_USB_PACKET_SIZE = new CTL_CODE(FILE_DEVICE_UNKNOWN, 0x851, METHOD_BUFFERED,
            FILE_ANY_ACCESS);

    private static final int MAX_BUFFER_SIZE = 4096;
    private static final int ASYNC_DATA_SIZE = 64;

    private int deviceHandle = INVALID_HANDLE_VALUE;
    private int usbPacketSize;
    private int apiVersion;
    private int interruptAsyncInEvent;
    private String devicePath;

    public DeviceWin32JNA() {
        //
    }
    
    /* (non-Javadoc)
     * @see uk.co.pekim.garmin.DeviceNative#initialise(int)
     */
    public void initialise(int unit) {
        if (deviceHandle != INVALID_HANDLE_VALUE) {
            throw new DeviceException("Already initialised");
        }

        int hDevInfo = getDeviceInfo();
        SP_DEVICE_INTERFACE_DATA interfaceData = getDeviceInterface(hDevInfo, unit);
        getDevicePath(hDevInfo, interfaceData);
        createDeviceHandle();
        discoverUSBPacketSize();
        discoverAPIVersion();
        createInterruptAsyncInEvent();

        // processReceivedDataThread.start();
        // receiverThread.start();
        //
        // sendPacket(new StartSessionPacket(this));
    }

    private void createInterruptAsyncInEvent() {
        interruptAsyncInEvent = KERNEL32.CreateEventA(null, true, false, null);
        if (interruptAsyncInEvent == 0) {
            throw new DeviceException("CreateEvent failed, error " + getLastError());
        }
    }

    /**
     * Get the USB packet size. Needed when sending data to the device.
     */
    private void discoverUSBPacketSize() {
        IntByReference size = new IntByReference();
        IntByReference packetSize = new IntByReference();
        packetSize.getPointer();
        if (!KERNEL32.DeviceIoControl(deviceHandle, IOCTL_USB_PACKET_SIZE.value(), null, 0, packetSize.getPointer(),
                Pointer.SIZE, size, null)) {
            throw new DeviceException("DeviceIoControl failed when getting USB packet size, error " + getLastError());
        }

        LOGGER.debug("USB packet size : " + packetSize.getValue());
        this.usbPacketSize = packetSize.getValue();
    }

    /**
     * Get the API version.
     */
    private void discoverAPIVersion() {
        IntByReference size = new IntByReference();
        IntByReference apiVersion = new IntByReference();
        apiVersion.getPointer();
        if (!KERNEL32.DeviceIoControl(deviceHandle, IOCTL_API_VERSION.value(), null, 0, apiVersion.getPointer(),
                Pointer.SIZE, size, null)) {
            throw new DeviceException("DeviceIoControl failed when getting API version, error " + getLastError());
        }

        LOGGER.debug("API version : " + apiVersion.getValue());
        this.apiVersion = apiVersion.getValue();
    }

    /**
     * Get the nth device interface from the set.
     */
    private SP_DEVICE_INTERFACE_DATA getDeviceInterface(int hDevInfo, int unit) {
        SP_DEVICE_INTERFACE_DATA deviceInterfaceData = new SP_DEVICE_INTERFACE_DATA();

        if (!SETUPAPI.SetupDiEnumDeviceInterfaces(hDevInfo, null, GARMIN_GUID, unit, deviceInterfaceData)) {
            if (getLastError() == ERROR_NO_MORE_ITEMS) {
                throw new NoDeviceFoundException("Garmin USB device (unit " + unit
                        + ") not present, or driver not installed");
            } else {
                throw new DeviceException("SetupDiEnumDeviceInterfaces failed, error " + getLastError());
            }
        }

        return deviceInterfaceData;
    }

    /**
     * Get the device information set for present Garmin USB devices.
     */
    private int getDeviceInfo() {
        int flags = DIGCF_PRESENT | DIGCF_INTERFACEDEVICE;
        int hDevInfo = SETUPAPI.SetupDiGetClassDevsA(GARMIN_GUID, null, 0, flags);
        if (hDevInfo == INVALID_HANDLE_VALUE) {
            throw new NoDeviceFoundException("SetupDiGetClassDevs failed, error " + getLastError());
        }

        return hDevInfo;
    }

    /**
     * Get the device's path from the interface's detail.
     */
    private String getDevicePath(int hDeviceInfo, SP_DEVICE_INTERFACE_DATA interfaceData) {
        // Find out the size of the device detail data.
        IntByReference sizeofDetailData = new IntByReference();
        if (!SETUPAPI.SetupDiGetDeviceInterfaceDetailA(hDeviceInfo, interfaceData, null, 0, sizeofDetailData, null)) {
            if (getLastError() != ERROR_INSUFFICIENT_BUFFER) {
                throw new DeviceException("SetupDiGetDeviceInterfaceDetail (finding size) failed, error "
                        + getLastError());
            }
        }

        // Get the device detail data.
        SP_DEVICE_INTERFACE_DETAIL_DATA deviceDetailData = new SP_DEVICE_INTERFACE_DETAIL_DATA(sizeofDetailData
                .getValue());
        if (!SETUPAPI.SetupDiGetDeviceInterfaceDetailA(hDeviceInfo, interfaceData, deviceDetailData, sizeofDetailData
                .getValue(), null, null)) {
            throw new DeviceException("SetupDiGetDeviceInterfaceDetail failed, error " + getLastError());
        }
        devicePath = Native.toString(deviceDetailData.devicePath);

        LOGGER.info("Device path : " + devicePath);
        return devicePath;
    }

    /**
     * Create a handle for the device.
     */
    private void createDeviceHandle() {
        int access = GENERIC_READ | GENERIC_WRITE;
        int shareMode = 0;
        int creationDisposition = OPEN_EXISTING;
        int flagsAndAttributes = FILE_ATTRIBUTE_NORMAL | FILE_FLAG_OVERLAPPED;

        deviceHandle = KERNEL32.CreateFileA(devicePath, access, shareMode, null, creationDisposition,
                flagsAndAttributes, null);
        if (deviceHandle == INVALID_HANDLE_VALUE) {
            if (getLastError() == ERROR_ACCESS_DENIED) {
                throw new AccessDeniedException(
                        "exclusive access is denied; it's likely that something else already has control of the device");
            } else {
                throw new DeviceException("CreateFile failed, error " + getLastError());
            }
        }
    }

    /* (non-Javadoc)
     * @see uk.co.pekim.garmin.DeviceNative#close()
     */
    public void close() {
        if (!KERNEL32.CloseHandle(deviceHandle)) {
            throw new DeviceException("Failed to close device, error " + getLastError());
        }
    }

    public class Data extends Structure {
        Data(byte[] data) {
            this.data = data;
        }
        public byte[] data;
    }
    
    /* (non-Javadoc)
     * @see uk.co.pekim.garmin.DeviceNative#sendPacket(byte[])
     */
    public void sendPacket(byte[] packet) {
        LOGGER.debug("Send " + packet.length + " bytes");

        // Write MAX_BUFFER_SIZE bytes at a time, until all bytes written.
        int bytesLeft = packet.length;
        int position = 0;
        while (bytesLeft > 0) {
            int bytesToWrite = Math.min(bytesLeft, MAX_BUFFER_SIZE);
            int bytesWritten = writeFile(packet, position, bytesToWrite);

            position += bytesWritten;
            bytesLeft -= bytesWritten;
        }
        LOGGER.debug("Sent a total of " + packet.length + " bytes");

        // If the packet size was an exact multiple of the USB packet
        // size, we must make a final write call with no data
        if (packet.length % usbPacketSize == 0) {
            writeFile(new byte[0], 0, 0);
            LOGGER.debug("Sent empty final buffer");
        }
    }

    private void closeHandle(int event) {
        if (!KERNEL32.CloseHandle(event)) {
            throw new DeviceException("CloseHandle failed, error " + getLastError());
        }
    }

    private OVERLAPPED newOverlappedWithEvent() {
        int event = KERNEL32.CreateEventA(null, true, false, null);
        if (event == 0) {
            throw new DeviceException("CreateEvent failed, error " + getLastError());
        }
        return new OVERLAPPED(event);
    }

    private int writeFile(byte[] data, int index, int length) {
        IntByReference bytesWritten = new IntByReference();
        OVERLAPPED overlapped = newOverlappedWithEvent();

        try {
            // The memory that will stick around until the write operation has completed.
            // Must be at least 1 byte in length, as can't allocate 0 (zero) bytes, and
            // we sometimes get called (validly) to send 1 byte.
            Memory buffer = new Memory(length + 1);
            buffer.write(0, data, index, length);
            
            LOGGER.debug("Writing " + length + " bytes");
            if (!KERNEL32.WriteFile(deviceHandle, buffer, length, null, overlapped)) {
                if (getLastError() != ERROR_IO_PENDING) {
                    throw new DeviceException("WriteFile failed for " + length + " bytes, error "
                            + getLastError());
                } else {
                    // Operation has been queued and will complete in the future.
                    LOGGER.debug("Queued write");
                    int waitResult = KERNEL32.WaitForSingleObject(overlapped.event, INFINITE);
                    if (waitResult != WAIT_OBJECT_0) {
                        throw new DeviceException("Wait for WriteFile to complete returned " + waitResult + ", error "
                                + getLastError());
                    }
                    if (!KERNEL32.GetOverlappedResult(deviceHandle, overlapped, bytesWritten, true)) {
                        throw new DeviceException("GetOverlappedResult failed, error " + getLastError());
                    }
                }
            }
        } finally {
            closeHandle(overlapped.event);
        }

        LOGGER.debug("Wrote " + length + " bytes");
        return bytesWritten.getValue();
    }
    
    /* (non-Javadoc)
     * @see uk.co.pekim.garmin.DeviceNative#receiveAsync()
     */
    public byte[] receiveAsync() {
        LOGGER.debug("Async receive");

        byte[] buffer = new byte[0];
        IntByReference bytesReturned = new IntByReference(ASYNC_DATA_SIZE);

        // Read async data until the driver returns less than the
        // max async data size, which signifies the end of a packet.
        while (bytesReturned.getValue() == ASYNC_DATA_SIZE) {
            Memory tempBuffer = new Memory(ASYNC_DATA_SIZE);
            OVERLAPPED overlapped = newOverlappedWithEvent();

            try {
                if (!KERNEL32.DeviceIoControl(deviceHandle, IOCTL_ASYNC_IN.value(), Pointer.NULL, 0, tempBuffer,
                        ASYNC_DATA_SIZE, bytesReturned, overlapped)) {
                    if (getLastError() != ERROR_IO_PENDING) {
                        throw new DeviceException("DeviceIoControl async in failed, error " + getLastError());
                    } else {
                        // Operation has been queued and will complete in the
                        // future.
                        if (!KERNEL32.ResetEvent(interruptAsyncInEvent)) {
                            throw new DeviceException("ResetEvent failed, error " + getLastError());
                        }
                        int events[] = new int[] { overlapped.event, interruptAsyncInEvent };
                        LOGGER.debug("No data immediately available, waiting");
                        int waitResult = KERNEL32.WaitForMultipleObjects(events.length, events, false, INFINITE);
                        if (waitResult == WAIT_OBJECT_0 + 1) {
                            LOGGER.debug("Wait for async receive interrupted");
                            if (!KERNEL32.CancelIo(deviceHandle)) {
                                throw new DeviceException("CancelIo failed, error " + getLastError());
                            }
                            KERNEL32.WaitForSingleObject(overlapped.event, INFINITE);
                            return null;
                        }

                        if (!KERNEL32.GetOverlappedResult(deviceHandle, overlapped, bytesReturned, true)) {
                            throw new DeviceException("GetOverlappedResult failed, error " + getLastError());
                        }
                    }
                }
            } finally {
                closeHandle(overlapped.event);
            }
            LOGGER.debug("Receive complete, " + bytesReturned.getValue() + " bytes returned");

            byte[] tempBufferArray = tempBuffer.getByteArray(0, bytesReturned.getValue());
            byte[] newBuffer = new byte[buffer.length + bytesReturned.getValue()];
            System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
            System.arraycopy(tempBufferArray, 0, newBuffer, buffer.length, bytesReturned.getValue());
            buffer = newBuffer;
        }

        LOGGER.debug("Received total of " + buffer.length + " bytes");
        return buffer;
    }

    private int getLastError() {
        return Native.getLastError();
    }

    @Override
    public void interruptAsyncIn() {
        if (!KERNEL32.SetEvent(interruptAsyncInEvent)) {
            throw new DeviceException("SetEvent failed, error " + getLastError());
        }
   }
}