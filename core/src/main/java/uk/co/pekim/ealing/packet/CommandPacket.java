package uk.co.pekim.ealing.packet;

import uk.co.pekim.ealing.CommandImplementation;
import uk.co.pekim.ealing.Device;

public class CommandPacket extends ApplicationPacket {
    private final int commandId;

    public CommandPacket(Device device, CommandImplementation commandImplementation) {
        super(device, PacketID.COMMAND_DATA, new byte[2]);
        this.commandId = commandImplementation.getCommandID();

        putU16(commandId, 0);
    }

    public CommandPacket(Device device, byte[] packetBuffer) {
        super(device, packetBuffer);
        
        commandId = getU16(0);
    }

    public int getCommandId() {
        return commandId;
    }

    @Override
    protected String getAdditionalDescription() {
        return "command id : " + commandId;
    }
}
