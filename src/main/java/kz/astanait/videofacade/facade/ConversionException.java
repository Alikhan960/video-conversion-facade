package kz.astanait.videofacade.facade;

/**
 * The only checked exception the Facade exposes. Subsystem failures
 * (unsupported format, corrupted data, I/O) are translated into this one,
 * so clients never depend on subsystem exception types.
 */
public class ConversionException extends Exception {
    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
