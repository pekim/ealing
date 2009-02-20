package uk.co.pekim.ealing;

public class AccessDeniedException extends DeviceException {
    private static final long serialVersionUID = 8692356329216802523L;

    public AccessDeniedException(String message) {
        super(message);
    }
}
