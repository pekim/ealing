package uk.co.pekim.ealing.packet;

import uk.co.pekim.ealing.Device;

public class SessionStartedPacket extends Packet {
    private final long unitID;

    SessionStartedPacket(Device device, byte[] packet) {
        super(device, packet);
        
        unitID = getU32(0);
    }

    public long getUnitID() {
        return unitID;
    }

    @Override
    public void notifyDeviceOfReceivedPacket() {
    	getDevice().sessionStarted(this);
    }
}
