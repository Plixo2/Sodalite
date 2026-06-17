package io.github.plixo2.sodalite.category.gpu;

import io.github.plixo2.sodalite.file.FileIO;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.file.Path;

public sealed interface ShaderSource<T extends Exception> {

    @ShaderFormat int shaderFormat();
    MemorySegment load(Arena arena) throws T;

    static ShaderSource<IOException> of(@ShaderFormat int shaderFormat, Path path) {
        return new File(shaderFormat, path);
    }
    static ShaderSource<IOException> of(@ShaderFormat int shaderFormat, java.io.InputStream inputStream) {
        return new InputStream(shaderFormat, inputStream);
    }
    static ShaderSource<RuntimeException> of(@ShaderFormat int shaderFormat, byte[] bytes) {
        return new ByteArray(shaderFormat, bytes);
    }
    static ShaderSource<RuntimeException> of(@ShaderFormat int shaderFormat, MemorySegment segment) {
        return new Memory(shaderFormat, segment);
    }


    record File(@ShaderFormat int shaderFormat, Path path) implements ShaderSource<IOException> {
        @Override
        public MemorySegment load(Arena arena) throws IOException {
            return FileIO.loadFile(arena, this.path);
        }
    }

    record InputStream(@ShaderFormat int shaderFormat, java.io.InputStream inputStream) implements ShaderSource<IOException> {
        @Override
        public MemorySegment load(Arena arena) throws IOException {
            var bytes = this.inputStream.readAllBytes();
            return arena.allocateFrom(ValueLayout.JAVA_BYTE, bytes);
        }
    }


    record ByteArray(@ShaderFormat int shaderFormat, byte[] bytes) implements ShaderSource<RuntimeException> {
        @Override
        public MemorySegment load(Arena arena) {
            return arena.allocateFrom(ValueLayout.JAVA_BYTE, this.bytes);
        }
    }

    record Memory(@ShaderFormat int shaderFormat, MemorySegment segment) implements ShaderSource<RuntimeException> {
        @Override
        public MemorySegment load(Arena arena) {
            return this.segment;
        }
    }
}
