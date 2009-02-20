package uk.co.pekim.ealing;

import java.util.HashMap;
import java.util.Map;

public class CommandProtocol {
    private final Map<CommandID, CommandImplementation> idToImplementation;
    private final Map<CommandImplementation, CommandID> implementationToId;
    
    CommandProtocol() {
        idToImplementation = new HashMap<CommandID, CommandImplementation>();
        implementationToId = new HashMap<CommandImplementation, CommandID>();
    }
    
    protected void addCommand(CommandID id, CommandImplementation implementation) {
        idToImplementation.put(id, implementation);
        implementationToId.put(implementation, id);
    }
    
    public CommandImplementation getCommandImplementation(CommandID commandId) {
        if (!idToImplementation.containsKey(commandId)) {
            throw new DeviceException("Unsupported command, " + commandId);
        }
        
        return idToImplementation.get(commandId);
    }
    
    public CommandID getCommandID(CommandImplementation commandImplementation) {
        if (!implementationToId.containsKey(commandImplementation)) {
            throw new DeviceException("Unsupported command, " + commandImplementation);
        }
        
        return implementationToId.get(commandImplementation);
    }
}
