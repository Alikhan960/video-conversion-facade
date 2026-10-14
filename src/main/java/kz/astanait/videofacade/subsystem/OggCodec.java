package kz.astanait.videofacade.subsystem;

/** Codec for the OGG (Theora) format. */
public class OggCodec extends AbstractHeaderCodec {
    public OggCodec() {
        super("OggS", (byte) 0x3C);
    }

    @Override
    public String format() {
        return "ogg";
    }
}
