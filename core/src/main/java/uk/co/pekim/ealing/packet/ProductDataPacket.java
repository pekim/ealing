package uk.co.pekim.ealing.packet;

import java.util.List;

import uk.co.pekim.ealing.Device;

public class ProductDataPacket extends Packet {
    private final int productID;
    private final int softwareVersion;
    private final String productDescription;
    private final List<String> additionalDescriptions;

    ProductDataPacket(Device device, byte[] packet) {
        super(device, packet);
        
        productID = getU16(0);
        softwareVersion = getS16(2);
        productDescription = getNullTerminatedString(4);
        additionalDescriptions = getNullTerminatedStrings(4 + productDescription.length() + 1);
    }

    public int getProductID() {
        return productID;
    }

    public int getSoftwareVersion() {
        return softwareVersion;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public List<String> getAdditionalDescriptions() {
        return additionalDescriptions;
    }

    @Override
    public void notifyDeviceOfReceivedPacket() {
    	getDevice().productData(this);
    }
}
