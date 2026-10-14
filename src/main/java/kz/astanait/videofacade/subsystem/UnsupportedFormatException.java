package kz.astanait.videofacade.subsystem;

/** Thrown when no codec is registered for the requested format. */
public class UnsupportedFormatException extends Exception {
    public UnsupportedFormatException(String message) {
        super(message);
    }
}
