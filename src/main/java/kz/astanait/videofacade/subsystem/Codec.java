package kz.astanait.videofacade.subsystem;

/**
 * A video codec: knows how to turn a raw frame stream into its own
 * compressed representation and back again.
 */
public interface Codec {

    /** Container/format name, e.g. "mp4". Also the file extension. */
    String format();

    /** Compresses raw frames into this codec's on-disk representation. */
    byte[] encode(byte[] rawFrames);

    /**
     * Decompresses data previously produced by {@link #encode(byte[])}.
     *
     * @throws CorruptedMediaException if the data does not belong to this codec
     */
    byte[] decode(byte[] encoded) throws CorruptedMediaException;
}
