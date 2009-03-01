package uk.co.pekim.ealing.packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import uk.co.pekim.ealing.packet.PacketID;
import uk.co.pekim.ealing.packet.PacketType;

public class PacketBuilder {
    ByteBuffer buffer;
    
    PacketBuilder(PacketType type, PacketID packetID) {
        buffer = ByteBuffer.allocate(100);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        addByte(type.getType());
        add000();
        addShort(packetID.getId());
        add00();
        
        // Reserve space for the "data size" value.
        // Will be set later, when getData() is called. 
        addInt(0);
    }

    void add0() {
        buffer.put((byte) 0);
    }

    void add00() {
        buffer.put((byte) 0);
        buffer.put((byte) 0);
    }
    
    void add000() {
        buffer.put((byte) 0);
        buffer.put((byte) 0);
        buffer.put((byte) 0);
    }

    void addByte(int b) {
        buffer.put((byte) b);
    }

    void addShort(int s) {
        buffer.putShort((short) s);
    }

    void addInt(int i) {
        buffer.putInt(i);
    }

    byte[] getData() {
        // Set the data size.
        buffer.putInt(8, buffer.position() - 12);

        return buffer.array();
    }
}
