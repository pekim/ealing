package uk.co.pekim.ealing.packet;

import java.util.List;

import uk.co.pekim.ealing.Device;

public class ExtProductDataPacket extends Packet {
    private final List<String> additionalDescriptions;

    ExtProductDataPacket(Device device, byte[] packet) {
        super(device, packet);
        
        additionalDescriptions = getNullTerminatedStrings(0);
    }

    public List<String> getAdditionalDescriptions() {
        return additionalDescriptions;
    }

    @Override
    public void notifyDeviceOfReceivedPacket() {
    	getDevice().productData(this);
    }
}
