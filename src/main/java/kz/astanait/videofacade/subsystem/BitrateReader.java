package kz.astanait.videofacade.subsystem;

/**
 * Reads a compressed stream into raw frames with a given codec and writes raw
 * frames back out with another one. Also reports the stream's bitrate.
 */
public class BitrateReader {

    private static final int BYTES_PER_FRAME = 1024;
    private static final int FRAMES_PER_SECOND = 24;

    public byte[] readFrames(byte[] encoded, Codec sourceCodec) throws CorruptedMediaException {
        return sourceCodec.decode(encoded);
    }

    public byte[] writeFrames(byte[] rawFrames, Codec targetCodec) {
        return targetCodec.encode(rawFrames);
    }

    /** Approximate bitrate in kbit/s, assuming the demo frame size and frame rate. */
    public int bitrateKbps(byte[] rawFrames) {
        int frames = Math.max(1, rawFrames.length / BYTES_PER_FRAME);
        double seconds = Math.max(1.0, (double) frames / FRAMES_PER_SECOND);
        return (int) Math.round(rawFrames.length * 8 / 1000.0 / seconds);
    }
}
