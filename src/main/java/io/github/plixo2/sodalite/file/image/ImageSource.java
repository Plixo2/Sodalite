package io.github.plixo2.sodalite.file.image;

import io.github.plixo2.sodalite.file.FileIO;

import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.file.Path;

public sealed interface ImageSource {
    record FilePath(Path path) implements ImageSource {}
    record Memory(MemorySegment fileData) implements ImageSource {}
    record Stream(InputStream stream) implements ImageSource {}

    static ImageSource of(Path path) {
        return new FilePath(path);
    }

    static ImageSource of(InputStream stream) {
        return new Stream(stream);
    }

    static ImageSource of(MemorySegment fileData) {
        return new Memory(fileData);
    }

    default MemorySegment toSegment(Arena arena) throws IOException {
        return switch (this) {
            case ImageSource.FilePath(var path) -> {
                yield FileIO.load(arena, path);
            }
            case ImageSource.Stream(var stream) -> {
                yield FileIO.load(arena, stream);
            }
            case ImageSource.Memory(var data) -> data;
        };
    }

}
