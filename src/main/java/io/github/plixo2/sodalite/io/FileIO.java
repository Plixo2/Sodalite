package io.github.plixo2.sodalite.io;

import io.github.plixo2.sodalite.resource.ResourceSet;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class FileIO {

    /// Maps the file at the given path directly into memory
    public static MemorySegment load(Arena arena, Path path) throws IOException {
        try (var fc = FileChannel.open(path)) {
            return fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size(), arena);
        }
    }

    public static MemorySegment load(Arena arena, InputStream inputStream) throws IOException {
        var bytes = Objects.requireNonNull(inputStream).readAllBytes();
        return arena.allocateFrom(ValueLayout.JAVA_BYTE, bytes);
    }

    public static void write(Path path, MemorySegment segment) throws IOException {
        try (var fc = FileChannel.open(
                path,
                StandardOpenOption.READ,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            var size = segment.byteSize();
            fc.truncate(size);

            if (size == 0) {
                return;
            }

            try (var arena = Arena.ofConfined()) {
                var dst = fc.map(FileChannel.MapMode.READ_WRITE, 0, size, arena);
                dst.copyFrom(segment);
            }
        }
    }

    public static IOWriteBuffer writeBuffer(Path path) {
        return writeBuffer(path, 256);
    }

    public static IOWriteBuffer writeBuffer(Path path, long initialSize) {
        var resources = ResourceSet.ofConfined();
        try {
            return new IOWriteBuffer(resources, path, initialSize);
        } catch(Exception e) {
            resources.close();
            throw e;
        }
    }

    public static IOReadBuffer readBuffer(Path path) throws IOException {
        var resources = ResourceSet.ofConfined();
        try {
            var segment = FileIO.load(resources.arena(), path);
            return new IOReadBuffer(resources, segment);
        } catch(Exception e) {
            resources.close();
            throw e;
        }
    }

}
