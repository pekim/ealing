package uk.co.pekim.ealing.packet;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.co.pekim.ealing.Device;
import uk.co.pekim.ealing.packet.Packet;
import uk.co.pekim.ealing.packet.PacketID;
import uk.co.pekim.ealing.packet.PacketType;
import uk.co.pekim.ealing.packet.SessionStartedPacket;

@SuppressWarnings("boxing")
public class TestPacket {
    private Device device;

    @Before
    public void setUp() throws Exception {
        device = new Device();
    }

    @Test
    public void createPacketTest() {
        PacketBuilder packetBuilder = new PacketBuilder(PacketType.USB_PROTOCOL, PacketID.SESSION_STARTED);
        packetBuilder.addInt(123);
        
        SessionStartedPacket packet = (SessionStartedPacket) Packet.createPacket(device, packetBuilder.getData());
        assertEquals("unit id", (long) 123, packet.getUnitID());
    }
}
