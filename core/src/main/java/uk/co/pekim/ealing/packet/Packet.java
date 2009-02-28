package uk.co.pekim.ealing.packet;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.EndianUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.pekim.ealing.DataUtils;
import uk.co.pekim.ealing.Device;
import uk.co.pekim.ealing.DeviceException;

public class Packet {
    private static final Logger LOGGER = LoggerFactory.getLogger(Packet.class);

    private static final String NEWLINE = System.getProperty("line.separator");

    private static final int OFFSET_TYPE = 0;
    //private static final int OFFSET_RESERVED_1 = 1;
    private static final int OFFSET_ID = 4;
    //private static final int OFFSET_RESERVED_2 = 6;
    private static final int OFFSET_DATA_SIZE = 8;
    private static final int OFFSET_DATA = 12;
    
    /**
     * The number of bytes before the data.
     */
    private static final int NATIVE_FORMAT_HEADER_LENGTH = OFFSET_DATA;

    private static final int DEFAULT_DUMPBYTES = 16;
    
    private final Device device;
    
    private final PacketType type;
    private final PacketID id;
    protected final byte[] data;

    public Packet(Device device, PacketType type, PacketID id, byte[] data) {
        this.device = device;
		this.type = type;
        this.id = id;
        
        if (data != null) {
            this.data = data;
        } else {
            this.data = new byte[0];
        }
    }
    
    public Packet(Device device, byte[] packet) {
        this.device = device;
        this.type = PacketType.get(packet[OFFSET_TYPE]);
        this.id = PacketID.get(EndianUtils.readSwappedUnsignedShort(packet, OFFSET_ID));
        this.data = new byte[(int) EndianUtils.readSwappedUnsignedInteger(packet, OFFSET_DATA_SIZE)];
        System.arraycopy(packet, OFFSET_DATA, data, 0, data.length);
    }

    public static Packet createPacket(Device device, byte[] packetBuffer) {
        PacketID packetID = PacketID.get(EndianUtils.readSwappedUnsignedShort(packetBuffer, OFFSET_ID));
        switch (packetID) {
        case START_SESSION:
            return new StartSessionPacket(device, packetBuffer);
        case SESSION_STARTED:
            return new SessionStartedPacket(device, packetBuffer);
        case DATA_AVAILABLE:
            return new DataAvailablePacket(device, packetBuffer);
        case PRODUCT_RQST:
            return new ProductRqstPacket(device, packetBuffer);
        case PRODUCT_DATA:
            return new ProductDataPacket(device, packetBuffer);
        case EXT_PRODUCT_DATA:
            return new ExtProductDataPacket(device, packetBuffer);
        case PROTOCOL_ARRAY:
            return new ProtocolArrayPacket(device, packetBuffer);
        case COMMAND_DATA:
            return new CommandPacket(device, packetBuffer);
        case DATE_TIME_DATA:
            return new DateTimeDataPacket(device, packetBuffer);
        case RECORDS:
            return new RecordsPacket(device, packetBuffer);
        case TRANSFER_COMPLETE:
            return new TransferCompletePacket(device, packetBuffer);
        case RUN:
            return new RunPacket(device, packetBuffer);
        }
        throw new DeviceException("Unknown packet type (unreachable?)");
    }
    
    public byte[] toNativeFormat() {
        byte[] packet = new byte[NATIVE_FORMAT_HEADER_LENGTH + data.length];
        packet[OFFSET_TYPE] = (byte) type.getType();
        EndianUtils.writeSwappedShort(packet, OFFSET_ID, (short) id.getId());
        EndianUtils.writeSwappedInteger(packet, OFFSET_DATA_SIZE, data.length);
        System.arraycopy(data, 0, packet, OFFSET_DATA, data.length);
        
        return packet;
    }
    
    PacketType getType() {
        return type;
    }
    
    PacketID getId() {
        return id;
    }
    
    public byte[] getData() {
        return data.clone();
    }
    
    Device getDevice() {
    	return device;
    }
    
    protected short getU8(int offset) {
        return DataUtils.getU8(data, offset);
    }
    
    protected int getU16(int offset) {
        return DataUtils.getU16(data, offset);
    }
    
    protected short getS16(int offset) {
        return DataUtils.getS16(data, offset);
    }
    
    protected long getU32(int offset) {
        return DataUtils.getU32(data, offset);
    }
    
    protected char getChar(int offset) {
        return (char) data[offset];
    }
    
    protected String getNullTerminatedString(int offset) {
        return DataUtils.getNullTerminatedString(data, offset);
    }
    
    protected List<String> getNullTerminatedStrings(int offset) {
        List<String> strings = new ArrayList<String>();
     
        String string = getNullTerminatedString(offset);
        while (string != null) {
            strings.add(string);
            offset += string.length() + 1;
            string = getNullTerminatedString(offset);
        }
        
        return strings;
    }
    
    protected void putU16(int u16, int offset) {
        DataUtils.putU16(data, u16, offset);
    }
    
    @Override
    public String toString() {
        return
            "Packet" + NEWLINE +
            "  type        : " + type + NEWLINE +
            "  id          : " + id + NEWLINE +
            "  data length : " + data.length;
    }
    
    String dump() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Packet : ");
        
        byte[] nativeFormat = toNativeFormat();
        for (int b = 0; b < nativeFormat.length; b++) {
            buffer.append((nativeFormat[b] & 0xff));
            if (b < nativeFormat.length - 1) {
                buffer.append(',');
            }
        }
        
        return buffer.toString();
    }
    
    public void log(String message) {
        log(message, DEFAULT_DUMPBYTES);
    }
    
    void log(String message, int dumpBytes) {
        message = message == null ? "" : message;
        
        String additionalDescription = getAdditionalDescription();
        additionalDescription = additionalDescription == null ? "" : " - " + additionalDescription;
        
        LOGGER.info(message + " : " + id.getDescription() + additionalDescription);
        LOGGER.debug("  type        : " + type);
        LOGGER.debug("  id          : " + id);
        LOGGER.debug("  data length : " + data.length);

        dumpBytes = Math.min(dumpBytes, data.length);
        if (LOGGER.isDebugEnabled() && dumpBytes > 0) {
            StringBuffer dump = new StringBuffer();
            dump.append("  data        : ");
            
            for (int b = 0 ; b < dumpBytes; b++) {
                dump.append(data[b] & 0xff);
                if (b < dumpBytes - 1) {
                    dump.append(',');
                }
            }
            
            if (data.length > dumpBytes) {
                dump.append(",...");
            }
            
            LOGGER.debug(dump.toString());
        }
    }

    protected  String getAdditionalDescription() {
        return null;
    }

    /**
     * Notify device of a received packet.
     * 
     * <p>Not all packet types notify the device.
     * The default behaviour is to do nothing.
     */
    public void notifyDeviceOfReceivedPacket() {
        // Do nothing.
    }
}
