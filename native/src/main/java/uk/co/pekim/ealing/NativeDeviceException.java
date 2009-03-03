package uk.co.pekim.ealing;

/**
 * A problem with the native device.
 *
 * @author mike
 *
 */
public class NativeDeviceException extends RuntimeException {
    private static final long serialVersionUID = 2331892222312201071L;

    public NativeDeviceException(String message) {
        super(message);
    }
}
