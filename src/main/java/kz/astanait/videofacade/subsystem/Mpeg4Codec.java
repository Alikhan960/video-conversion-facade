package kz.astanait.videofacade.subsystem;

/** Codec for the MP4 format. */
public class Mpeg4Codec extends AbstractHeaderCodec {
    public Mpeg4Codec() {
        super("MP4V", (byte) 0x5A);
    }

    @Override
    public String format() {
        return "mp4";
    }
}
