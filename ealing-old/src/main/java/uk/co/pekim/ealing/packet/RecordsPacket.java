package uk.co.pekim.ealing.packet;

import uk.co.pekim.ealing.Device;

public class RecordsPacket extends Packet {

    private final int packetsToFollow;

    RecordsPacket(Device device, byte[] packet) {
        super(device, packet);

        // 5.4 Standard Beginning and Ending Packets
        packetsToFollow = getU16(0);
    }

    @Override
    public void notifyDeviceOfReceivedPacket() {
    	getDevice().packetsToFollow(this);
    }

    public int getPacketsToFollow() {
        return packetsToFollow;
    }
}
