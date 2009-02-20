package uk.co.pekim.ealing;

public class CommandImplementation {
    private final int commandID;

    CommandImplementation(int commandID) {
        this.commandID = commandID;
    }

    public int getCommandID() {
        return commandID;
    }

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof CommandImplementation)) {
			return false;
		}
		
		CommandImplementation other = (CommandImplementation) object;
		return other.commandID == commandID;
	}

	@Override
	public int hashCode() {
		return commandID;
	}
}
