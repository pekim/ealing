package uk.co.pekim.ealing.packet;

import java.util.HashMap;
import java.util.Map;

import uk.co.pekim.ealing.Device;
import uk.co.pekim.ealing.DeviceException;
import uk.co.pekim.ealing.Protocol;

public class ProtocolArrayPacket extends Packet {
    private final Map<String, Protocol> protocols;
    
    ProtocolArrayPacket(Device device, byte[] packet) {
        super(device, packet);
        
        protocols = new HashMap<String, Protocol>();
        Protocol protocol = null;
        
        int offset = 0;
        while (offset < data.length) {
            char tag = getChar(offset);
            int tagData = getU16(offset + 1);
            
            if (tag == 'D') {
                if (protocol == null) {
                    throw new DeviceException("Datatype D" + tagData + " before any protocol");
                }
                protocol.addDataType(tagData);
            } else {
                protocol = new Protocol(tag, tagData);
                protocols.put(protocol.toString(), protocol);
            }
            
            offset += 3;
        }
    }

    public Map<String, Protocol> getProtocols() {
        return protocols;
    }

    @Override
    public void notifyDeviceOfReceivedPacket() {
    	getDevice().protocolArray(this);
    }
}
