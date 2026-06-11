package io.github.plixo2.sodalite.file;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public class FileIO {

    public static MemorySegment loadFile(Arena arena, Path path) throws IOException {
        try (var fc = FileChannel.open(path)) {
            return fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size(), arena);
        }
    }

}
