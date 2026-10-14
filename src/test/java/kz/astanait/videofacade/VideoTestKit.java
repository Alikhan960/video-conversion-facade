package kz.astanait.videofacade;

import kz.astanait.videofacade.facade.ConversionException;
import kz.astanait.videofacade.facade.VideoConversionFacade;
import kz.astanait.videofacade.subsystem.CodecFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Tiny testing language for the conversion tests (Clean Code, ch. 9:
 * "Domain-Specific Testing Language"). Tests say WHAT they set up and check;
 * codec lookups, byte juggling and file I/O live here.
 */
final class VideoTestKit {

    private final Path folder;
    private final VideoConversionFacade converter = new VideoConversionFacade();
    private final CodecFactory codecs = new CodecFactory();

    VideoTestKit(Path folder) {
        this.folder = folder;
    }

    /** Creates a valid video file; the extension of {@code name} selects its format. */
    Path makeVideo(String name, int... rawFrameBytes) {
        try {
            String format = name.substring(name.lastIndexOf('.') + 1);
            return Files.write(folder.resolve(name), codecs.forFormat(format).encode(bytes(rawFrameBytes)));
        } catch (Exception e) {
            throw new IllegalStateException("Test setup failed for " + name, e);
        }
    }

    /** Creates a file that has a video extension but garbage content. */
    Path makeBrokenVideo(String name) {
        try {
            return Files.writeString(folder.resolve(name), "this is not a video");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    Path convert(Path source, String format) throws ConversionException {
        return converter.convert(source.toString(), format);
    }

    /** Reads a video file back into raw frame bytes. */
    byte[] framesOf(Path video) throws Exception {
        String format = video.getFileName().toString().replaceAll(".*\\.", "");
        return codecs.forFormat(format).decode(Files.readAllBytes(video));
    }

    Path missingFile(String name) {
        return folder.resolve(name);
    }

    static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = (byte) values[i];
        }
        return result;
    }
}
