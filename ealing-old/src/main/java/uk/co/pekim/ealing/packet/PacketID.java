/**
 * 
 */
package uk.co.pekim.ealing.packet;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import uk.co.pekim.ealing.DeviceException;

public enum PacketID {
    DATA_AVAILABLE(2, "Pid_Data_Available"),
    START_SESSION(5, "Pid_Start_Session"),
    SESSION_STARTED(6, "Pid_Session_Started"),
    COMMAND_DATA(10, "Pid_Command_Data"),
    TRANSFER_COMPLETE(12, "Pid_Xfer_Cmplt"),
    DATE_TIME_DATA(14, "Pid_Date_Time_Data"),
    RECORDS(27, "Pid_Records"),
    EXT_PRODUCT_DATA(248, "Pid_Ext_Product_Data"),
    PROTOCOL_ARRAY(253, "Pid_Protocol_Array"),
    PRODUCT_RQST(254, "Pid_Product_Rqst"),
    PRODUCT_DATA(255, "Pid_Product_Data"),
    RUN(990, "Pid_Run"),
    ;

    private static final Map<Integer, PacketID> lookup = new HashMap<Integer, PacketID>();

    static {
        for(PacketID id : EnumSet.allOf(PacketID.class))
             lookup.put(new Integer(id.getId()), id);
    }

    private final int id;
    private final String description;
    
    PacketID(int id, String description) {
        this.id = id;
        this.description = description;
    }

    int getId() {
        return id;
    }

    String getDescription() {
        return description;
    }
    
    public static PacketID get(int id) {
        PacketID packetID = lookup.get(new Integer(id));
        if (packetID == null) {
            throw new DeviceException("Unknown packet id " + id);
        }
        return packetID;
    }

    @Override
    public String toString() {
        return description + " (" + id + ")";
    }
}