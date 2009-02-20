package uk.co.pekim.ealing.packet;

import uk.co.pekim.ealing.Device;

public class ApplicationPacket extends Packet {
    public ApplicationPacket(Device device, byte[] packetBuffer) {
        super(device, packetBuffer);
    }
    
    public ApplicationPacket(Device device, PacketID id, byte[] data) {
        super(device, PacketType.APPLICATION, id, data);
    }
}
