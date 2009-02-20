package uk.co.pekim.ealing.transport;


public interface DeviceNative {
    /**
     * Find and initialise the device.
     * 
     * @param unit
     *            the (zero based) unit to initialise.
     *            
     * @throws NoDeviceFoundException if the device can't be found or any necessary
     *          driver is not installed.
     * @throws DeviceException if initialisation fails.
     */
    public void initialise(int unit);

    /**
     * Close the device.
     * 
     * @throws DeviceException if an error is encountered during the closing.
     */
    public void close();

    /**
     * Send a packet to the device.
     * 
     * @param packet the packet to send.
     */
    public void sendPacket(byte[] packet);

    /**
     * Request any available async data.
     * 
     * @return received data, or <code>null</code> if no data available (when the IO
     *              has been interrupted).
     */
    public byte[] receiveAsync();

    /**
     * Interrupt any outstanding async receive.
     * This may cause null to be returned from an in progress <code>receiveAsync()</code>
     * call.
     */
    public void interruptAsyncIn();
}