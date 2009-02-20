package uk.co.pekim.ealing.packet;

import uk.co.pekim.ealing.Device;
import uk.co.pekim.ealing.datatype.BaseDataType;

public class RunPacket extends Packet {
    private static int D_1 = 1;
    private final BaseDataType dataType;

    RunPacket(Device device, byte[] packet) {
        super(device, packet);

        dataType = device.getProtocol("A1000").createDataType(D_1, this);
    }

    @Override
    public void notifyDeviceOfReceivedPacket() {
    	getDevice().receivedRun(this);
    }

    
    
    @Override
    public void log(String message) {
        super.log(message);
        dataType.log("  ");
    }
}
