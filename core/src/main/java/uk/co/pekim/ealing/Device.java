package uk.co.pekim.ealing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.pekim.ealing.datatype.DataType;
import uk.co.pekim.ealing.jna.linux.DeviceLinux;
import uk.co.pekim.ealing.jna.win32.DeviceWin32JNA;
import uk.co.pekim.ealing.packet.CommandPacket;
import uk.co.pekim.ealing.packet.DateTimeDataPacket;
import uk.co.pekim.ealing.packet.ExtProductDataPacket;
import uk.co.pekim.ealing.packet.Packet;
import uk.co.pekim.ealing.packet.ProductDataPacket;
import uk.co.pekim.ealing.packet.ProductRqstPacket;
import uk.co.pekim.ealing.packet.ProtocolArrayPacket;
import uk.co.pekim.ealing.packet.RecordsPacket;
import uk.co.pekim.ealing.packet.RunPacket;
import uk.co.pekim.ealing.packet.SessionStartedPacket;
import uk.co.pekim.ealing.packet.StartSessionPacket;
import uk.co.pekim.ealing.packet.TransferCompletePacket;
import uk.co.pekim.ealing.transport.DeviceNative;

import com.sun.jna.Platform;

/**
 * A USB attached Garmin device.
 * 
 * @author Mike D Pilsbury
 */
public class Device {
    private static final Logger LOGGER = LoggerFactory.getLogger(Device.class);

    private final AsyncReceiverThread receiverThread;
    private final ProcessReceivedDataThread processReceivedDataThread;
    private final BlockingQueue<byte[]> receivedDataQueue;
    private Map<String, Protocol> protocols;
    private CommandProtocol commandProtocol;
    
    private final List<DeviceListener> listeners;

    private final DeviceNative deviceNative;
    
    private long bytesSent;
    private long bytesReceived;

    public Device() {
        receivedDataQueue = new LinkedBlockingQueue<byte[]>();
        receiverThread = new AsyncReceiverThread(this);
        processReceivedDataThread = new ProcessReceivedDataThread(this);
        listeners = new ArrayList<DeviceListener>();

        if (Platform.isWindows()) {
            deviceNative= new DeviceWin32JNA();
        } else if (Platform.isLinux()) {
                deviceNative= new DeviceLinux();
        } else {
            throw new DeviceException("Unsupported platform");
        }
        
        bytesSent = 0;
        bytesReceived = 0;
    }
    
    /**
     * Initialise the device.
     * Assumes that it is the first, or only, Garmin USB device that is to be used.
     */
    public void initialise() {
        initialise(0);
    }
    
    /**
     * Initialise the device.
     * 
     * @param unit the (zero based) unit to initialise.
     */
    public void initialise(int unit) {
        LOGGER.info("Initialising Garmin USB device, unit " + unit);
        try {
            deviceNative.initialise(unit);
        } catch (NativeDeviceException nativeDeviceException) {
            throw new DeviceException(nativeDeviceException);
        }
        
        processReceivedDataThread.start();
        receiverThread.start();

        sendPacket(new StartSessionPacket(this));
    }

    void sendPacket(Packet packet) {
        packet.log("Send");
        
        byte[] nativeFormatPacket = packet.toNativeFormat();
        try {
            deviceNative.sendPacket(nativeFormatPacket);
        } catch (NativeDeviceException nativeDeviceException) {
            throw new DeviceException(nativeDeviceException);
        }
        
        bytesSent += nativeFormatPacket.length;
    }
    
    BlockingQueue<byte[]> getReceivedDataQueue() {
        return receivedDataQueue;
    }
    
    /**
     * Close the device.
     * 
     * @throws DeviceException if an error is encountered during the closing, or if
     *                          device not initialised.
     */
    public void close() {
        LOGGER.info("Close requested");
        
        receiverThread.requestTermination();
        try {
            receiverThread.join();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted waiting for " + receiverThread.getName() + " to terminate", e);
        }

        processReceivedDataThread.requestTermination();
        try {
            processReceivedDataThread.join();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted waiting for " + processReceivedDataThread.getName() + " to terminate", e);
        }

        try {
            deviceNative.close();
        } catch (NativeDeviceException nativeDeviceException) {
            throw new DeviceException(nativeDeviceException);
        }
        
        for (DeviceListener listener : listeners) {
        	listener.closed();
        }

        LOGGER.info("Close complete");
        LOGGER.info("bytes sent     : " + bytesSent);
        LOGGER.info("bytes received : " + bytesReceived);
    }
    
    /**
     * Called when the session has been started.
     */
    public void sessionStarted(SessionStartedPacket packet) {
        LOGGER.info("Session started, unit ID : " + packet.getUnitID());
        sendPacket(new ProductRqstPacket(this));
        
        for(DeviceListener listener : listeners) {
        	listener.sessionStarted(packet.getUnitID());
        }
    }
    
    /**
     * Called when product data received.
     */
    public void productData(ProductDataPacket packet) {
        LOGGER.info("Product data - product ID : " + packet.getProductID() + ", software version : " + packet.getSoftwareVersion());
        LOGGER.info("Product data - product description : " + packet.getProductDescription());

        if (LOGGER.isDebugEnabled()) {
            for (String description : packet.getAdditionalDescriptions()) {
                LOGGER.debug("Additional description : " + description);
            }
        }
    }
    
    /**
     * Called when product data received.
     */
    public void productData(ExtProductDataPacket packet) {
        if (LOGGER.isDebugEnabled()) {
            for (String description : packet.getAdditionalDescriptions()) {
                LOGGER.debug("Ext product data - Additional description : " + description);
            }
        }
    }

    /**
     * Called when date/time data received.
     */
    public void dateTimeData(DateTimeDataPacket packet) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        LOGGER.info("datetime : " + dateFormat.format(packet.getCalendar().getTime()));
        
        for(DeviceListener listener : listeners) {
            listener.initialized();
        }
    }
    
    /**
     * Called when the number of subsequent data packets (excluding a terminating packet) to expect. 
     */
    public void packetsToFollow(RecordsPacket packet) {
    	LOGGER.info("Packets to follow : " + packet.getPacketsToFollow());
    }

	public void transferComplete(TransferCompletePacket packet) {
		CommandID commandID = commandProtocol.getCommandID(new CommandImplementation(packet.getCommandId()));
		LOGGER.info("Transfer complete : " + commandID);
	}

	public void receivedRun(RunPacket runPacket) {
		// TODO Auto-generated method stub
		
	}

	/**
     * Called when protocol received.
     */
    public void protocolArray(ProtocolArrayPacket packet) {
        protocols = packet.getProtocols();
        
        for (Protocol protocol : protocols.values()) {
            StringBuffer message = new StringBuffer();
            message.append("Protocol " + protocol + " : ");
            for (DataType dataType : protocol.getDataTypes()) {
                message.append(dataType + " ");
            }
            LOGGER.info(message.toString());
        }
        
        if (getProtocol("A10") != null) {
            commandProtocol = new CommandProtocol1();
        } else if (getProtocol("A11") != null) {
            commandProtocol = new CommandProtocol2();
        } else {
            throw new DeviceException("No supported command protocol");
        }
        
        initialisationComplete();
    }
    
    private void initialisationComplete() {
        requestTime();
    }
    
    private void requestTime() {
        CommandImplementation implementation = commandProtocol.getCommandImplementation(CommandID.CMND_TRANSFER_TIME);
        Packet packet = new CommandPacket(this, implementation);
        sendPacket(packet);
    }

    public void requestRuns() {
        CommandImplementation implementation = commandProtocol.getCommandImplementation(CommandID.CMND_TRANSFER_RUNS);
        Packet packet = new CommandPacket(this, implementation);
        sendPacket(packet);
    }

    public Protocol getProtocol(String protocolName) {
    	Protocol protocol = protocols.get(protocolName);
    	if (protocol == null) {
    		throw new DeviceException("Protocol " + protocolName + " not supported");
    	}

    	return protocol;
    }

    public CommandProtocol getCommandProtocol() {
    	return commandProtocol;
    }
    
    public void addListener(DeviceListener listener) {
    	if (listeners.contains(listeners)) {
    		throw new IllegalArgumentException("Listener already registered");
    	}
    	listeners.add(listener);
    }
    
    public void removeListener(DeviceListener listener) {
    	if (!listeners.contains(listeners)) {
    		throw new IllegalArgumentException("Listener not registered");
    	}
    	listeners.remove(listener);
    }

    /**
     * Close the device, as the handle is an OS resource, and we should really give it
     * up if we can.
     */
    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    public static void main(String[] args) {
        new Device().initialise();
    }

    public byte[] receiveAsync() {
        try {
            byte[] data = deviceNative.receiveAsync();
            if (data != null) {
                bytesReceived += data.length;
            }
            
            return data;
        } catch (NativeDeviceException nativeDeviceException) {
            throw new DeviceException(nativeDeviceException);
        }
    }

    public void interruptAsyncIn() {
        try {
            deviceNative.interruptAsyncIn();
        } catch (NativeDeviceException nativeDeviceException) {
            throw new DeviceException(nativeDeviceException);
        }
    }

    public long getBytesSent() {
        return bytesSent;
    }

    public long getBytesReceived() {
        return bytesReceived;
    }
}
