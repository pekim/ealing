package uk.co.pekim.ealing.packet;

import uk.co.pekim.ealing.Device;
import uk.co.pekim.ealing.DeviceException;

public class DataAvailablePacket extends Packet {

    DataAvailablePacket(Device device, byte[] packet) {
        super(device, packet);
    }

    @Override
    public void notifyDeviceOfReceivedPacket() {
        throw new DeviceException("can't handle Data Available packet yet");
    }
}
