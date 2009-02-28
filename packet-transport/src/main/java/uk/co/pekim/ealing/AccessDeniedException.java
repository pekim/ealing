package uk.co.pekim.ealing;

/**
 * Access to the device is not permitted by the operating system.
 * 
 * @author Mike D Pilsbury
 *
 */
public class AccessDeniedException extends DeviceException {
    private static final long serialVersionUID = 8692356329216802523L;

    public AccessDeniedException(String message) {
        super(message);
    }
}
