package kz.astanait.videofacade.subsystem;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Creates the {@link Codec} that matches a given format name. */
public class CodecFactory {

    private final Map<String, Codec> codecs = new LinkedHashMap<>();

    public CodecFactory() {
        register(new Mpeg4Codec());
        register(new OggCodec());
        register(new WebmCodec());
    }

    private void register(Codec codec) {
        codecs.put(codec.format(), codec);
    }

    public Codec forFormat(String format) throws UnsupportedFormatException {
        Codec codec = codecs.get(format.toLowerCase(Locale.ROOT));
        if (codec == null) {
            throw new UnsupportedFormatException(
                    "No codec for format '" + format + "'. Supported: " + codecs.keySet());
        }
        return codec;
    }

    public Set<String> supportedFormats() {
        return codecs.keySet();
    }
}
