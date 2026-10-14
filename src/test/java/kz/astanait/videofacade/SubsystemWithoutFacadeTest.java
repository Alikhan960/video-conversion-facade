package kz.astanait.videofacade;

import kz.astanait.videofacade.subsystem.AudioMixer;
import kz.astanait.videofacade.subsystem.Codec;
import kz.astanait.videofacade.subsystem.CorruptedMediaException;
import kz.astanait.videofacade.subsystem.Mpeg4Codec;
import kz.astanait.videofacade.subsystem.OggCodec;
import org.junit.Test;

import static kz.astanait.videofacade.VideoTestKit.bytes;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThrows;

/**
 * Proves the defining property of the Facade pattern: the subsystem is still
 * usable on its own, with no Facade anywhere in sight.
 */
public class SubsystemWithoutFacadeTest {

    @Test
    public void codecDecodesWhatItEncoded() throws Exception {
        Codec ogg = new OggCodec();

        byte[] roundTrip = ogg.decode(ogg.encode(bytes(9, 8, 7)));

        assertArrayEquals(bytes(9, 8, 7), roundTrip);
    }

    @Test
    public void codecRejectsDataEncodedByAnotherCodec() {
        byte[] mp4Data = new Mpeg4Codec().encode(bytes(1, 2, 3));

        assertThrows(CorruptedMediaException.class, () -> new OggCodec().decode(mp4Data));
    }

    @Test
    public void mixerLeavesQuietAudioUntouched() {
        byte[] quiet = bytes(9, 8, 7);

        assertArrayEquals(quiet, new AudioMixer().normalize(quiet));
    }
}
