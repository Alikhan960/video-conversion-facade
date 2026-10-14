package kz.astanait.videofacade.facade;

import kz.astanait.videofacade.subsystem.BitrateReader;
import kz.astanait.videofacade.subsystem.CodecFactory;
import kz.astanait.videofacade.subsystem.CorruptedMediaException;
import kz.astanait.videofacade.subsystem.UnsupportedFormatException;
import kz.astanait.videofacade.subsystem.VideoFileStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Second, separate Facade: read-only inspection of a video file. Kept apart
 * from {@link VideoConversionFacade} so that the converter's API is not
 * polluted with unrelated functionality.
 */
public class VideoInfoFacade {

    private final CodecFactory codecFactory = new CodecFactory();
    private final BitrateReader bitrateReader = new BitrateReader();
    private final VideoFileStorage storage = new VideoFileStorage();

    /** Returns a one-line description such as {@code "lecture.mp4: mp4, 2 KB raw, ~16 kbit/s"}. */
    public String describe(String fileName) throws ConversionException {
        Path file = Path.of(fileName);
        if (!Files.isRegularFile(file)) {
            throw new ConversionException("Source file not found: " + file);
        }
        try {
            String format = storage.extensionOf(file);
            byte[] raw = bitrateReader.readFrames(storage.load(file), codecFactory.forFormat(format));
            return file.getFileName() + ": " + format + ", " + raw.length + " bytes raw, ~"
                    + bitrateReader.bitrateKbps(raw) + " kbit/s";
        } catch (UnsupportedFormatException | CorruptedMediaException e) {
            throw new ConversionException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ConversionException("I/O failure while reading '" + file + "'", e);
        }
    }
}
