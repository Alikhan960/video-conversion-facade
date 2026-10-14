package kz.astanait.videofacade;

import kz.astanait.videofacade.facade.ConversionException;
import kz.astanait.videofacade.facade.VideoConversionFacade;
import kz.astanait.videofacade.subsystem.AudioMixer;
import kz.astanait.videofacade.subsystem.Codec;
import kz.astanait.videofacade.subsystem.CodecFactory;
import kz.astanait.videofacade.subsystem.CorruptedMediaException;
import kz.astanait.videofacade.subsystem.Mpeg4Codec;
import kz.astanait.videofacade.subsystem.OggCodec;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/** Dependency-free tests: run with {@code java -cp out kz.astanait.videofacade.FacadeTests}. */
public class FacadeTests {

    private static int failures = 0;

    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("facade-tests");

        test("convert mp4 -> ogg produces a readable ogg file", () -> {
            byte[] raw = {1, 2, 3, 4, 5, 6};
            Path src = dir.resolve("a.mp4");
            Files.write(src, new Mpeg4Codec().encode(raw));

            Path out = new VideoConversionFacade().convert(src.toString(), "ogg");

            check(out.getFileName().toString().equals("a.ogg"), "output name");
            byte[] decoded = new OggCodec().decode(Files.readAllBytes(out));
            check(Arrays.equals(raw, decoded), "payload preserved");
        });

        test("audio samples are limited by the mixer step", () -> {
            byte[] raw = {10, 127, 20, -128};
            Path src = dir.resolve("b.mp4");
            Files.write(src, new Mpeg4Codec().encode(raw));

            Path out = new VideoConversionFacade().convert(src.toString(), "webm");

            byte[] decoded = new CodecFactory().forFormat("webm").decode(Files.readAllBytes(out));
            check(Arrays.equals(new byte[]{10, 100, 20, -100}, decoded), "clipped: " + Arrays.toString(decoded));
        });

        test("unsupported target format -> ConversionException", () -> {
            Path src = dir.resolve("c.mp4");
            Files.write(src, new Mpeg4Codec().encode(new byte[]{1}));
            expectConversionError(() -> new VideoConversionFacade().convert(src.toString(), "avi"));
        });

        test("missing file -> ConversionException", () ->
                expectConversionError(() -> new VideoConversionFacade().convert(dir.resolve("nope.mp4").toString(), "ogg")));

        test("corrupted file -> ConversionException", () -> {
            Path src = dir.resolve("d.mp4");
            Files.writeString(src, "not a video");
            expectConversionError(() -> new VideoConversionFacade().convert(src.toString(), "ogg"));
        });

        test("subsystem is usable directly, without the facade", () -> {
            byte[] raw = {9, 8, 7};
            Codec ogg = new OggCodec();
            check(Arrays.equals(raw, ogg.decode(ogg.encode(raw))), "round trip");
            check(Arrays.equals(raw, new AudioMixer().normalize(raw)), "mixer leaves quiet audio alone");
            try {
                ogg.decode(new Mpeg4Codec().encode(raw));
                check(false, "should reject foreign header");
            } catch (CorruptedMediaException expected) {
                // ok
            }
        });

        System.out.println(failures == 0 ? "ALL TESTS PASSED" : failures + " TEST(S) FAILED");
        System.exit(failures == 0 ? 0 : 1);
    }

    interface ThrowingRunnable { void run() throws Exception; }

    private static void test(String name, ThrowingRunnable body) {
        try {
            body.run();
            System.out.println("PASS  " + name);
        } catch (Throwable t) {
            failures++;
            System.out.println("FAIL  " + name + " -> " + t);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void expectConversionError(ThrowingRunnable r) throws Exception {
        try {
            r.run();
        } catch (ConversionException expected) {
            return;
        }
        throw new AssertionError("expected ConversionException");
    }
}
