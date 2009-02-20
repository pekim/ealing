package uk.co.pekim.ealing.packet;

import uk.co.pekim.ealing.Device;

public class USBProtocolPacket extends Packet {
    public USBProtocolPacket(Device device, byte[] packetBuffer) {
        super(device, packetBuffer);
    }
    
    public USBProtocolPacket(Device device, PacketID id, byte[] data) {
        super(device, PacketType.USB_PROTOCOL, id, data);
    }
}
