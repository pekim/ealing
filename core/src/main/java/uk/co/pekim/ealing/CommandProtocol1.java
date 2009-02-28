package uk.co.pekim.ealing;

/**
 * Command Protocol 1 - A010
 * 6.3.1
 * 
 * @author Mike D Pilsbury
 */
public class CommandProtocol1 extends CommandProtocol {
    CommandProtocol1() {
        super();
        
        addCommand(CommandID.CMND_TRANSFER_TIME, new CommandImplementation(5));
        addCommand(CommandID.CMND_TRANSFER_RUNS, new CommandImplementation(450));
    }
}
