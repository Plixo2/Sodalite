package io.github.plixo2.sodalite.category.gpu;

import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.nio.file.Path;

/// @sdlAPI SDL_GPUShader
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

    public record Creator<T extends Exception>(
        ShaderSource<T> source,
        Shader.Parameters parameter
    ) {
        public static Creator<IOException> of(
                @ShaderFormat int shaderFormat,
                Path path,
                Shader.Parameters parameter
        ) {
            return new Creator<>(ShaderSource.of(shaderFormat, path), parameter);
        }
        public static Creator<IOException> of(
                @ShaderFormat int shaderFormat,
                java.io.InputStream inputStream,
                Shader.Parameters parameter
        ) {
            return new Creator<>(ShaderSource.of(shaderFormat, inputStream), parameter);
        }
        public static Creator<RuntimeException> of(
                @ShaderFormat int shaderFormat,
                byte[] bytes,
                Shader.Parameters parameter
        ) {
            return new Creator<>(ShaderSource.of(shaderFormat, bytes), parameter);
        }
        public static Creator<RuntimeException> of(
                @ShaderFormat int shaderFormat,
                MemorySegment code,
                Shader.Parameters parameter
        ) {
            return new Creator<>(ShaderSource.of(shaderFormat, code), parameter);
        }
        public static <T extends Exception> Creator<T> of(
                ShaderSource<T> source,
                Shader.Parameters parameter
        ) {
            return new Creator<>(source, parameter);
        }
    }

    public record Parameters(
            String entryPoint,
            int num_samplers,
            int num_storage_textures,
            int num_storage_buffers,
            int num_uniform_buffers
    ) {
        public Parameters {
            if (num_samplers < 0) throw new IllegalArgumentException("num_samplers must be non-negative");
            if (num_storage_textures < 0) throw new IllegalArgumentException("num_storage_textures must be non-negative");
            if (num_storage_buffers < 0) throw new IllegalArgumentException("num_storage_buffers must be non-negative");
            if (num_uniform_buffers < 0) throw new IllegalArgumentException("num_uniform_buffers must be non-negative");
            if (entryPoint.isEmpty()) throw new IllegalArgumentException("entryPoint must be non-empty");
        }

        public Parameters(
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

        public static Parameters of(
                String entryPoint,
                int num_samplers,
                int num_storage_textures,
                int num_storage_buffers,
                int num_uniform_buffers
        ) {
            return new Parameters(
                    entryPoint,
                    num_samplers,
                    num_storage_textures,
                    num_storage_buffers,
                    num_uniform_buffers
            );
        }

        public static Parameters of(
                int num_samplers,
                int num_storage_textures,
                int num_storage_buffers,
                int num_uniform_buffers
        ) {
            return new Parameters(
                    num_samplers,
                    num_storage_textures,
                    num_storage_buffers,
                    num_uniform_buffers
            );
        }

    }




}
