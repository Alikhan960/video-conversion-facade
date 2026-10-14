package kz.astanait.videofacade.subsystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Low-level reading and writing of media files on disk. */
public class VideoFileStorage {

    public byte[] load(Path source) throws IOException {
        return Files.readAllBytes(source);
    }

    public Path save(Path target, byte[] data) throws IOException {
        Path parent = target.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        return Files.write(target, data);
    }

    /** Returns the file extension in lower case, or an empty string if there is none. */
    public String extensionOf(Path file) {
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot < 0 ? "" : name.substring(dot + 1).toLowerCase();
    }

    /** Returns the file name without its extension. */
    public String baseNameOf(Path file) {
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot < 0 ? name : name.substring(0, dot);
    }
}
