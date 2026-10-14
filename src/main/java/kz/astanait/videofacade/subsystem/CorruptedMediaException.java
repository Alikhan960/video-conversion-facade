package kz.astanait.videofacade.subsystem;

/** Thrown when media bytes do not match the codec that is asked to read them. */
public class CorruptedMediaException extends Exception {
    public CorruptedMediaException(String message) {
        super(message);
    }
}
