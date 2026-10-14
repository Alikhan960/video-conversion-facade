package kz.astanait.videofacade.facade;

import kz.astanait.videofacade.subsystem.AudioMixer;
import kz.astanait.videofacade.subsystem.BitrateReader;
import kz.astanait.videofacade.subsystem.Codec;
import kz.astanait.videofacade.subsystem.CodecFactory;
import kz.astanait.videofacade.subsystem.CorruptedMediaException;
import kz.astanait.videofacade.subsystem.UnsupportedFormatException;
import kz.astanait.videofacade.subsystem.VideoFileStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/**
 * Facade over the video conversion subsystem.
 *
 * <p>Converting a video needs five collaborators called in a strict order:
 * storage -> codec factory (source) -> bitrate reader (decode) -> audio mixer
 * -> codec factory (target) -> bitrate reader (encode) -> storage.
 * The Facade owns that order; clients only call {@link #convert}.
 */
public class VideoConversionFacade {

    private final CodecFactory codecFactory;
    private final BitrateReader bitrateReader;
    private final AudioMixer audioMixer;
    private final VideoFileStorage storage;

    public VideoConversionFacade() {
        this(new CodecFactory(), new BitrateReader(), new AudioMixer(), new VideoFileStorage());
    }

    /** Lets advanced users (and tests) supply their own subsystem objects. */
    public VideoConversionFacade(CodecFactory codecFactory, BitrateReader bitrateReader,
                                 AudioMixer audioMixer, VideoFileStorage storage) {
        this.codecFactory = codecFactory;
        this.bitrateReader = bitrateReader;
        this.audioMixer = audioMixer;
        this.storage = storage;
    }

    /**
     * Converts a video file to another format. The result is written next to
     * the source file, with the new extension.
     *
     * @param fileName     path of the source video; its extension selects the source codec
     * @param targetFormat desired format, e.g. "ogg" or "webm"
     * @return path of the converted file
     * @throws ConversionException if the conversion cannot be completed
     */
    public Path convert(String fileName, String targetFormat) throws ConversionException {
        Path source = Path.of(fileName);
        Path target = source.resolveSibling(
                storage.baseNameOf(source) + "." + targetFormat.toLowerCase());
        return convert(source, targetFormat, target);
    }

    /** Same as {@link #convert(String, String)} but writes to an explicit output path. */
    public Path convert(Path source, String targetFormat, Path target) throws ConversionException {
        requireExistingFile(source);
        try {
            Codec sourceCodec = codecFactory.forFormat(storage.extensionOf(source));
            Codec targetCodec = codecFactory.forFormat(targetFormat);

            byte[] encoded = storage.load(source);
            byte[] rawFrames = bitrateReader.readFrames(encoded, sourceCodec);
            byte[] mixed = audioMixer.normalize(rawFrames);
            byte[] converted = bitrateReader.writeFrames(mixed, targetCodec);

            return storage.save(target, converted);
        } catch (UnsupportedFormatException e) {
            throw new ConversionException(e.getMessage(), e);
        } catch (CorruptedMediaException e) {
            throw new ConversionException("Cannot convert '" + source + "': " + e.getMessage(), e);
        } catch (IOException e) {
            throw new ConversionException("I/O failure while converting '" + source + "'", e);
        }
    }

    /** Formats that can be passed as {@code targetFormat}. */
    public Set<String> supportedFormats() {
        return codecFactory.supportedFormats();
    }

    private void requireExistingFile(Path source) throws ConversionException {
        if (!Files.isRegularFile(source)) {
            throw new ConversionException("Source file not found: " + source);
        }
    }
}
