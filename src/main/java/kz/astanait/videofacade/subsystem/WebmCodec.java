package kz.astanait.videofacade.subsystem;

/** Codec for the WebM (VP9) format. */
public class WebmCodec extends AbstractHeaderCodec {
    public WebmCodec() {
        super("WEBM", (byte) 0x7E);
    }

    @Override
    public String format() {
        return "webm";
    }
}
