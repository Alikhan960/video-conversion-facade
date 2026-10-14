package kz.astanait.videofacade.subsystem;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Shared behaviour of the demo codecs: a magic header identifying the format
 * followed by a payload scrambled with a codec-specific key.
 * (A stand-in for real compression so the subsystem has genuine, invertible logic.)
 */
abstract class AbstractHeaderCodec implements Codec {

    private final byte[] magic;
    private final byte key;

    AbstractHeaderCodec(String magic, byte key) {
        this.magic = magic.getBytes(StandardCharsets.US_ASCII);
        this.key = key;
    }

    @Override
    public byte[] encode(byte[] rawFrames) {
        byte[] out = new byte[magic.length + rawFrames.length];
        System.arraycopy(magic, 0, out, 0, magic.length);
        for (int i = 0; i < rawFrames.length; i++) {
            out[magic.length + i] = (byte) (rawFrames[i] ^ key);
        }
        return out;
    }

    @Override
    public byte[] decode(byte[] encoded) throws CorruptedMediaException {
        if (encoded.length < magic.length
                || !Arrays.equals(Arrays.copyOfRange(encoded, 0, magic.length), magic)) {
            throw new CorruptedMediaException(
                    "Data is not a valid ." + format() + " stream (bad header)");
        }
        byte[] raw = new byte[encoded.length - magic.length];
        for (int i = 0; i < raw.length; i++) {
            raw[i] = (byte) (encoded[magic.length + i] ^ key);
        }
        return raw;
    }
}
