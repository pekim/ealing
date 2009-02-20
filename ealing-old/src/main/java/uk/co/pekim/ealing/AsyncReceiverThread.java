package uk.co.pekim.ealing;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AsyncReceiverThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncReceiverThread.class);

    private final Device device;
    private final BlockingQueue<byte[]> receivedDataQueue;
    
    /**
     * When true, the run method's loop (and therefore the run method) will terminate
     * on it's next iteration.
     * 
     * Must be 'volatile', and relies on Java 5's clarified definition of volatile
     * (which shouldn't be a problem, as this library requires >= 1.5 to compile).
     */
    private volatile boolean terminate;

    
    public AsyncReceiverThread(Device device) {
        this.device = device;
        this.terminate = false;
        receivedDataQueue = device.getReceivedDataQueue();
        
        setName("ReceiverThread");
    }

    @Override
    public void run() {
        LOGGER.info("Thread started");
        
        while (!terminate) {
            LOGGER.debug("Requesting async data");
            byte[] buffer = device.receiveAsync();
            if (buffer != null) {
                LOGGER.debug("Received " + buffer.length + " bytes of async data");
                receivedDataQueue.add(buffer);
            } else {
                // No data available, probably because the async io was interrupted, and
                // we're about to terminated.
                LOGGER.debug("No data");
            }
        }

        LOGGER.info("Thread terminated");
    }
    
    void requestTermination() {
        terminate = true;
        device.interruptAsyncIn();
        LOGGER.debug("Thread termination requested");
    }
}
