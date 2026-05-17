package io.github.plixo2.sodalite.category.gpu;

import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

/// @apiNote SDL_GPUShader
public class Shader extends ResourceObject {

    private final MemorySegment segment;

    Shader(
            ResourceSet resources,
            Device device,
            MemorySegment segment
    ) {
        resources.register(this, () -> GPU.releaseGPUShader(device, segment));
        this.segment = segment;
    }

    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }

    public record ShaderCreator<T extends Throwable>(
        Shader.Source<T> source,
        Shader.Parameter parameter
    ) {
        public static ShaderCreator<IOException> of(
                @ShaderFormat int shaderFormat,
                Path path,
                Shader.Parameter parameter
        ) {
            return new ShaderCreator<>(Source.fromFile(shaderFormat, path), parameter);
        }
        public static ShaderCreator<IOException> of(
                @ShaderFormat int shaderFormat,
                java.io.InputStream inputStream,
                Shader.Parameter parameter
        ) {
            return new ShaderCreator<>(Source.fromInputStream(shaderFormat, inputStream), parameter);
        }
        public static ShaderCreator<RuntimeException> of(
                @ShaderFormat int shaderFormat,
                byte[] bytes,
                Shader.Parameter parameter
        ) {
            return new ShaderCreator<>(Source.fromByteArray(shaderFormat, bytes), parameter);
        }
        public static ShaderCreator<RuntimeException> of(
                @ShaderFormat int shaderFormat,
                MemorySegment code,
                Shader.Parameter parameter
        ) {
            return new ShaderCreator<>(Source.fromMemory(shaderFormat, code), parameter);
        }
        public static <T extends Throwable> ShaderCreator<T> of(
                Shader.Source<T> source,
                Shader.Parameter parameter
        ) {
            return new ShaderCreator<>(source, parameter);
        }
    }

    public record Parameter(
            String entryPoint,
            int num_samplers,
            int num_storage_textures,
            int num_storage_buffers,
            int num_uniform_buffers
    ) {
        public Parameter(
                int num_samplers,
                int num_storage_textures,
                int num_storage_buffers,
                int num_uniform_buffers
        ) {
            this(
                    "main",
                    num_samplers,
                    num_storage_textures,
                    num_storage_buffers,
                    num_uniform_buffers
            );
        }

        public Parameter {
            if (num_samplers < 0) throw new IllegalArgumentException("num_samplers must be non-negative");
            if (num_storage_textures < 0) throw new IllegalArgumentException("num_storage_textures must be non-negative");
            if (num_storage_buffers < 0) throw new IllegalArgumentException("num_storage_buffers must be non-negative");
            if (num_uniform_buffers < 0) throw new IllegalArgumentException("num_uniform_buffers must be non-negative");
            if (entryPoint.isEmpty()) throw new IllegalArgumentException("entryPoint must be non-empty");
        }

        public static Parameter of(
                String entryPoint,
                int num_samplers,
                int num_storage_textures,
                int num_storage_buffers,
                int num_uniform_buffers
        ) {
            return new Parameter(
                    entryPoint,
                    num_samplers,
                    num_storage_textures,
                    num_storage_buffers,
                    num_uniform_buffers
            );
        }

        public static Parameter of(
                int num_samplers,
                int num_storage_textures,
                int num_storage_buffers,
                int num_uniform_buffers
        ) {
            return new Parameter(
                    num_samplers,
                    num_storage_textures,
                    num_storage_buffers,
                    num_uniform_buffers
            );
        }

    }

    public sealed interface Source<T extends Throwable> {

        @ShaderFormat int shaderFormat();
        MemorySegment load(Arena arena) throws T;


        static Source<IOException> fromFile(@ShaderFormat int shaderFormat, Path path) {
            return new Shader.File(shaderFormat, path);
        }
        static Source<IOException> fromInputStream(@ShaderFormat int shaderFormat, java.io.InputStream inputStream) {
            return new Shader.InputStream(shaderFormat, inputStream);
        }
        static Source<RuntimeException> fromByteArray(@ShaderFormat int shaderFormat, byte[] bytes) {
            return new Shader.ByteArray(shaderFormat, bytes);
        }
        static Source<RuntimeException> fromMemory(@ShaderFormat int shaderFormat, MemorySegment segment) {
            return new Shader.Memory(shaderFormat, segment);
        }


    }

    private record File(@ShaderFormat int shaderFormat, Path path) implements Source<IOException> {
        @Override
        public MemorySegment load(Arena arena) throws IOException {
            try (var fc = FileChannel.open(this.path)) {
                return fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size(), arena);
            }
        }
    }

    private record InputStream(@ShaderFormat int shaderFormat, java.io.InputStream inputStream) implements Source<IOException> {
        @Override
        public MemorySegment load(Arena arena) throws IOException {
            var bytes = this.inputStream.readAllBytes();
            return arena.allocateFrom(ValueLayout.JAVA_BYTE, bytes);
        }
    }

    private record ByteArray(@ShaderFormat int shaderFormat, byte[] bytes) implements Source<RuntimeException> {
        @Override
        public MemorySegment load(Arena arena) {
            return arena.allocateFrom(ValueLayout.JAVA_BYTE, this.bytes);
        }
    }

    private record Memory(@ShaderFormat int shaderFormat, MemorySegment segment) implements Source<RuntimeException> {
        @Override
        public MemorySegment load(Arena arena) {
            return this.segment;
        }
    }


}
