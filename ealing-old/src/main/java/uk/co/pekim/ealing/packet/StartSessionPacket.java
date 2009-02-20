package uk.co.pekim.ealing.packet;

import uk.co.pekim.ealing.Device;

public class StartSessionPacket extends USBProtocolPacket {
    public StartSessionPacket(Device device) {
        super(device, PacketID.START_SESSION, null);
    }

    public StartSessionPacket(Device device, byte[] packetBuffer) {
        super(device, packetBuffer);
    }
}
