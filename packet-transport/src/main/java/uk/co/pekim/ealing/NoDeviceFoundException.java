package uk.co.pekim.ealing;

/**
 * No suitable device can be found.
 * 
 * @author Mike D Pilsbury
 *
 */
public class NoDeviceFoundException extends DeviceException {
    private static final long serialVersionUID = 8692356329216802523L;

    public NoDeviceFoundException(String message) {
        super(message);
    }
}
