package uk.co.pekim.ealing;

public class DeviceException extends RuntimeException {
    private static final long serialVersionUID = 3282779208800489991L;

    public DeviceException(String message) {
        super(message);
    }

    public DeviceException(Throwable cause) {
        super(cause);
    }
}
