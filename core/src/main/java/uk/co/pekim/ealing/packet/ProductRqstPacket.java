package uk.co.pekim.ealing.packet;

import uk.co.pekim.ealing.Device;

public class ProductRqstPacket extends ApplicationPacket {
    public ProductRqstPacket(Device device) {
        super(device, PacketID.PRODUCT_RQST, null);
    }

    public ProductRqstPacket(Device device, byte[] packetBuffer) {
        super(device, packetBuffer);
    }
}
