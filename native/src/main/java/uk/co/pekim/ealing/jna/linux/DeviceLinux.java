package uk.co.pekim.ealing.jna.linux;

import org.apache.commons.io.EndianUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.pekim.ealing.NativeDeviceException;
import uk.co.pekim.ealing.NoDeviceFoundException;
import uk.co.pekim.ealing.jna.linux.LibUsb.USBBus;
import uk.co.pekim.ealing.jna.linux.LibUsb.USBDevice;
import uk.co.pekim.ealing.jna.linux.LibUsb.USBEndpointDescriptor;
import uk.co.pekim.ealing.jna.linux.LibUsb.USBInterfaceDescriptor;
import uk.co.pekim.ealing.transport.DeviceNative;

import com.sun.jna.Pointer;

public class DeviceLinux implements DeviceNative {
    private static final int ETIMEOUT = -110;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(DeviceLinux.class);

    private static final LibUsb LIBUSB = LibUsb.INSTANCE;

    private static final int MILLI_SECOND = 1;
    private static final int SECOND = 1000 * MILLI_SECOND;

    private static final int TIMEOUT_BULK = 3 * SECOND;
    /**
     * The timeout for interrupt reads. Termination could take up to this long.
     * So we don't want it to be too long. If it's too short the polling loops
     * too often and data is missed.
     */
    private static final int TIMEOUT_INTERRUPT = 1 * SECOND;

    private static final short VENDOR_GARMIN = 0x091e;
    private static final short PRODUCT_GARMIN_GPS = 0x0003;

    private Pointer deviceHandle = Pointer.NULL;
    private int interruptIn;
    private int bulkIn;
    private int bulkOut;

    private int usbPacketSize;

    private volatile boolean terminateAsyncIn;

    public DeviceLinux() {
        terminateAsyncIn = false;
    }

    /*
     * (non-Javadoc)
     *
     * @see uk.co.pekim.garmin.DeviceNative#initialise(int)
     */
    @Override
    public void initialise(int unit) {
        LIBUSB.usb_init();
        LIBUSB.usb_find_busses();
        LIBUSB.usb_find_devices();

        USBDevice garminDevice = null;
        String devicePath = null;

        int foundDevices = 0;
        USBBus firstBus = LIBUSB.usb_get_busses();
        for (USBBus bus = firstBus; bus != null; bus = bus.next) {
            for (USBDevice device = bus.firstDevice; device != null; device = device.next) {
                if (device.descriptor.idVendor == VENDOR_GARMIN
                        && device.descriptor.idProduct == PRODUCT_GARMIN_GPS) {
                    if (foundDevices == unit) {
                        garminDevice = device;
                        devicePath = bus.getDirName() + "/"
                                + garminDevice.getFileName();
                        break;
                    } else {
                        foundDevices++;
                    }
                }
            }
        }

        if (garminDevice == null) {
            throw new NoDeviceFoundException("Failed to find Garmin USB device");
        }

        LOGGER.debug("Found Garmin USB device " + devicePath);

        deviceHandle = LIBUSB.usb_open(garminDevice);
        if (deviceHandle.equals(Pointer.NULL)) {
            throw new NativeDeviceException("usb_open failed, error "
                    + LIBUSB.usb_strerror());
        }

        if (LIBUSB.usb_set_configuration(deviceHandle, 1) < 0) {
            throw new NativeDeviceException(
                    "usb_set_configuration failed , error "
                            + LIBUSB.usb_strerror()
                            + " - Likely to be either a permission problem, or the device is in use");
        }

        if (LIBUSB.usb_claim_interface(deviceHandle, 0) < 0) {
            throw new NativeDeviceException("usb_claim_interface failed, error "
                    + LIBUSB.usb_strerror());
        }

        USBInterfaceDescriptor.ByReference altSetting = garminDevice.config.usbInterface.altSetting;
        USBEndpointDescriptor[] endpoints = (USBEndpointDescriptor[]) altSetting.endpoints
                .toArray(altSetting.numberOfEndpoints);
        for (USBEndpointDescriptor endpoint : endpoints) {
            switch (endpoint.attributes & USBEndpointDescriptor.TYPE_MASK) {
            case USBEndpointDescriptor.TYPE_BULK:
                if ((endpoint.endpointAddress & USBEndpointDescriptor.DIR_MASK) != 0) {
                    bulkIn = endpoint.endpointAddress
                            & USBEndpointDescriptor.ADDRESS_MASK;
                    LOGGER.debug("Endpoint : bulk in      : " + bulkIn);
                } else {
                    bulkOut = endpoint.endpointAddress
                            & USBEndpointDescriptor.ADDRESS_MASK;
                    LOGGER.debug("Endpoint : bulk out     : " + bulkOut);
                }
                break;
            case USBEndpointDescriptor.TYPE_INTERRUPT:
                if ((endpoint.endpointAddress & USBEndpointDescriptor.DIR_MASK) != 0) {
                    interruptIn = endpoint.endpointAddress
                            & USBEndpointDescriptor.ADDRESS_MASK;
                    LOGGER.debug("Endpoint : interrupt in : " + interruptIn);
                }
                break;
            default:
                // Not interested.
            }
        }

        if (bulkIn == 0) {
            throw new NativeDeviceException("Failed to get endpoint : bulk in");
        }
        if (bulkOut == 0) {
            throw new NativeDeviceException("Failed to get endpoint : bulk out");
        }
        if (interruptIn == 0) {
            throw new NativeDeviceException("Failed to get endpoint : interrupt in");
        }

        // The USB packet size. Needed when sending data to the device.
        usbPacketSize = garminDevice.descriptor.maxPacketSize0;
        LOGGER.debug("USB packet size : " + usbPacketSize);
    }

    /*
     * (non-Javadoc)
     *
     * @see uk.co.pekim.garmin.DeviceNative#close()
     */
    @Override
    public void close() {
        if (deviceHandle == Pointer.NULL) {
            LOGGER.warn("Aleady closed");
            return;
        }

        try {
            LIBUSB.usb_release_interface(deviceHandle, bulkIn);
            LIBUSB.usb_release_interface(deviceHandle, bulkOut);
            LIBUSB.usb_release_interface(deviceHandle, interruptIn);
            LIBUSB.usb_close(deviceHandle);
        } finally {
            deviceHandle = Pointer.NULL;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (deviceHandle != Pointer.NULL) {
            LOGGER.warn("Closing device in finalize");
            close();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see uk.co.pekim.garmin.DeviceNative#sendPacket(byte[])
     */
    @Override
    public void sendPacket(byte[] packet) {
        int bytesSent = LIBUSB.usb_bulk_write(deviceHandle, bulkOut, packet,
                packet.length, TIMEOUT_BULK);
        LOGGER.debug("Sent " + bytesSent + "/" + packet.length);
    }

    /*
     * (non-Javadoc)
     *
     * @see uk.co.pekim.garmin.DeviceNative#receiveAsync()
     */
    @Override
    public byte[] receiveAsync() {
        byte[] packet = new byte[0];
        byte[] receiveBuffer = new byte[1024];
        int expectedPacketLength = Integer.MAX_VALUE;

        int bytesReceived = ETIMEOUT;
        while (bytesReceived == ETIMEOUT) {
            if (terminateAsyncIn) {
                return new byte[0];
            }

            bytesReceived = LIBUSB.usb_interrupt_read(deviceHandle,
                    interruptIn, receiveBuffer, receiveBuffer.length,
                    TIMEOUT_INTERRUPT);
            if (bytesReceived == ETIMEOUT) {
                LOGGER.debug("Timeout");
            }
        }

        while (bytesReceived > 0 && packet.length < expectedPacketLength) {
            LOGGER.debug("Received " + bytesReceived);

            // Concatenate received data.
            byte[] newPacket = new byte[packet.length + bytesReceived];
            System.arraycopy(packet, 0, newPacket, 0, packet.length);
            System.arraycopy(receiveBuffer, 0, newPacket, packet.length,
                    bytesReceived);
            packet = newPacket;

            if (packet.length >= 12) {
                long dataLength = EndianUtils.readSwappedUnsignedInteger(packet, 8);
                expectedPacketLength = (int) (12 + dataLength);
            }

            if (packet.length < expectedPacketLength) {
                bytesReceived = LIBUSB.usb_interrupt_read(deviceHandle,
                        interruptIn, receiveBuffer, receiveBuffer.length,
                        TIMEOUT_INTERRUPT);
            }
        }

        return packet;

        // while (bytesReceived > 0) {
        // LOGGER.debug("Received " + bytesReceived);
        //
        // byte[] newPacket = new byte[packet.length + bytesReceived];
        // System.arraycopy(packet, 0, newPacket, 0, packet.length);
        // System.arraycopy(receiveBuffer, 0, newPacket, packet.length,
        // bytesReceived);
        // packet = newPacket;
        //
        // bytesReceived = LIBUSB.usb_interrupt_read(deviceHandle, interruptIn,
        // receiveBuffer, receiveBuffer.length, TIMEOUT_INTERRUPT);
        // }
        //
        // return packet;

        // return receiveBuffer;
    }

    @Override
    public void interruptAsyncIn() {
        LOGGER.debug("interruptAsyncIn requested");
        terminateAsyncIn = true;
    }
}