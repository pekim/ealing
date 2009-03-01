package uk.co.pekim.ealing;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.pekim.ealing.packet.Packet;


public class ProcessReceivedDataThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessReceivedDataThread.class);

    private final Device device;
    private final BlockingQueue<byte[]> queue;

    /**
     * When true, the run method's loop (and therefore the run method) will terminate
     * on it's next iteration.
     * 
     * Must be 'volatile', and relies on Java 5's clarified definition of volatile
     * (which shouldn't be a problem, as this library requires >= 1.5 to compile).
     */
    private volatile boolean terminate;

    ProcessReceivedDataThread(Device device) {
        this.device = device;
        this.terminate = false;
        queue = device.getReceivedDataQueue();
        
        setName("ReceivedDataThread");
    }

    @Override
    public void run() {
        LOGGER.info("Thread started");

        while (!terminate) {
            try {
                byte[] data = queue.take();
                if (data.length == 0) {
                    // Dummy data, added by requestTermination() method.
                    continue;
                }
                
                LOGGER.debug("Processing " + data.length + " bytes of async data");
                Packet packet = Packet.createPacket(device, data);
                packet.log("Received");
                packet.notifyDeviceOfReceivedPacket();
            } catch (DeviceException exception) {
                LOGGER.error("Failure processing received packet", exception);
            } catch (InterruptedException e) {
                LOGGER.warn("Interrupted while waiting for element");
            }
        }
        
        LOGGER.info("Thread terminated");
    }

    
    void requestTermination() {
        terminate = true;
        queue.add(new byte[0]);
        LOGGER.debug("Thread termination requested");
    }
}
