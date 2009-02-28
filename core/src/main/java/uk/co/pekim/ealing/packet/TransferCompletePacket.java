package uk.co.pekim.ealing.packet;

import uk.co.pekim.ealing.Device;

public class TransferCompletePacket extends Packet {

    private final int commandId;

    TransferCompletePacket(Device device, byte[] packet) {
        super(device, packet);

        // 5.4 Standard Beginning and Ending Packets
        commandId = getU16(0);
    }

    @Override
    public void notifyDeviceOfReceivedPacket() {
    	getDevice().transferComplete(this);
    }

    public int getCommandId() {
        return commandId;
    }
}
