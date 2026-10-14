package kz.astanait.videofacade;

import kz.astanait.videofacade.facade.ConversionException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Files;
import java.nio.file.Path;

import static kz.astanait.videofacade.VideoTestKit.bytes;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Behaviour of VideoConversionFacade, written as BUILD-OPERATE-CHECK tests.
 * F.I.R.S.T.: every test gets a fresh temporary folder (Independent, Repeatable),
 * runs in milliseconds with no network (Fast) and ends in an assertion (Self-validating).
 */
public class VideoConversionFacadeTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private VideoTestKit kit;

    @Before
    public void setUp() {
        kit = new VideoTestKit(folder.getRoot().toPath());
    }

    @Test
    public void convertingMp4ToOggCreatesOggFileNextToSource() throws Exception {
        Path source = kit.makeVideo("clip.mp4", 1, 2, 3);

        Path result = kit.convert(source, "ogg");

        assertEquals(source.resolveSibling("clip.ogg"), result);
    }

    @Test
    public void convertedVideoKeepsTheFrames() throws Exception {
        Path source = kit.makeVideo("clip.mp4", 1, 2, 3, 4);

        Path result = kit.convert(source, "ogg");

        assertArrayEquals(bytes(1, 2, 3, 4), kit.framesOf(result));
    }

    @Test
    public void convertingLimitsTooLoudAudioSamples() throws Exception {
        Path source = kit.makeVideo("clip.mp4", 10, 127, 20, -128);

        Path result = kit.convert(source, "webm");

        assertArrayEquals(bytes(10, 100, 20, -100), kit.framesOf(result));
    }

    @Test
    public void formatNameIsCaseInsensitive() throws Exception {
        Path source = kit.makeVideo("clip.mp4", 1, 2);

        Path result = kit.convert(source, "OGG");

        assertTrue(Files.isRegularFile(result));
    }

    @Test
    public void unsupportedTargetFormatIsRejectedWithSupportedFormatsInMessage() {
        Path source = kit.makeVideo("clip.mp4", 1);

        ConversionException e = assertThrows(ConversionException.class, () -> kit.convert(source, "avi"));

        assertTrue(e.getMessage().contains("avi") && e.getMessage().contains("mp4"));
    }

    @Test
    public void missingSourceFileIsRejectedWithItsPathInMessage() {
        Path missing = kit.missingFile("nope.mp4");

        ConversionException e = assertThrows(ConversionException.class, () -> kit.convert(missing, "ogg"));

        assertTrue(e.getMessage().contains("nope.mp4"));
    }

    @Test
    public void corruptedSourceFileIsRejectedWithItsCauseAttached() {
        Path broken = kit.makeBrokenVideo("broken.mp4");

        ConversionException e = assertThrows(ConversionException.class, () -> kit.convert(broken, "ogg"));

        assertNotNull(e.getCause());
    }

    @Test
    public void failedConversionLeavesNoOutputFileBehind() {
        Path broken = kit.makeBrokenVideo("broken.mp4");

        assertThrows(ConversionException.class, () -> kit.convert(broken, "ogg"));

        assertFalse(Files.exists(broken.resolveSibling("broken.ogg")));
    }
}
