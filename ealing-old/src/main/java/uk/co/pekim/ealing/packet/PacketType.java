/**
 * 
 */
package uk.co.pekim.ealing.packet;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import uk.co.pekim.ealing.DeviceException;

public enum PacketType {
    USB_PROTOCOL(0, "USB protocol layer"),
    APPLICATION(20, "Application layer");

    private static final Map<Integer, PacketType> lookup = new HashMap<Integer, PacketType>();

    static {
        for(PacketType pt : EnumSet.allOf(PacketType.class))
             lookup.put(new Integer(pt.getType()), pt);
    }

    private final int type;
    private final String description;
    
    PacketType(int type, String description) {
        this.type = type;
        this.description = description;
    }

    int getType() {
        return type;
    }

    String getDescription() {
        return description;
    }

    public static PacketType get(int type) { 
        PacketType packetType = lookup.get(new Integer(type));
        if (packetType == null) {
            throw new DeviceException("Unknown packet type " + type);
        }
        return packetType;
    }

    @Override
    public String toString() {
        return description + " (" + type + ")";
    }
}