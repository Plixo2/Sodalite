package io.github.plixo2.sodalite.file;

import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Objects;

public class FileIO {

    public static MemorySegment load(Arena arena, Path path) throws IOException {
        try (var fc = FileChannel.open(path)) {
            return fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size(), arena);
        }
    }
    public static MemorySegment load(Arena arena, InputStream inputStream) throws IOException {
        var bytes = Objects.requireNonNull(inputStream).readAllBytes();
        return arena.allocateFrom(ValueLayout.JAVA_BYTE, bytes);
    }


}
