package kz.astanait.videofacade.subsystem;

/**
 * Post-processes the audio of a decoded stream. The demo treats every second
 * byte as an audio sample and limits (clips) it to a safe amplitude range.
 */
public class AudioMixer {

    private static final int MAX_AMPLITUDE = 100;

    /** Returns a copy of the frames in which audio samples are limited to ±MAX_AMPLITUDE. */
    public byte[] normalize(byte[] rawFrames) {
        byte[] out = rawFrames.clone();
        for (int i = 1; i < out.length; i += 2) {
            if (out[i] > MAX_AMPLITUDE) {
                out[i] = MAX_AMPLITUDE;
            } else if (out[i] < -MAX_AMPLITUDE) {
                out[i] = -MAX_AMPLITUDE;
            }
        }
        return out;
    }
}
