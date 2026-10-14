package kz.astanait.videofacade.client;

import kz.astanait.videofacade.facade.ConversionException;
import kz.astanait.videofacade.facade.VideoConversionFacade;
import kz.astanait.videofacade.facade.VideoInfoFacade;

import java.nio.file.Path;

/**
 * Demo client. It imports only the facade package - no subsystem class is
 * instantiated or called here.
 */
public class Main {

    public static void main(String[] args) {
        VideoConversionFacade converter = new VideoConversionFacade();
        VideoInfoFacade info = new VideoInfoFacade();

        String source = args.length > 0 ? args[0] : "samples/lecture.mp4";

        try {
            System.out.println("Before: " + info.describe(source));

            Path result = converter.convert(source, "ogg");
            System.out.println("Converted to: " + result);
            System.out.println("After:  " + info.describe(result.toString()));

            System.out.println("Also supported: " + converter.supportedFormats());
        } catch (ConversionException e) {
            System.err.println("Conversion failed: " + e.getMessage());
        }

        System.out.println();
        System.out.println("--- error handling demo ---");
        tryConvert(converter, source, "avi");
        tryConvert(converter, "samples/missing.mp4", "ogg");
        tryConvert(converter, "samples/broken.mp4", "ogg");
    }

    private static void tryConvert(VideoConversionFacade converter, String file, String format) {
        try {
            converter.convert(file, format);
        } catch (ConversionException e) {
            System.out.println("Expected failure -> " + e.getMessage());
        }
    }
}
