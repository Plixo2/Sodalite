package io.github.plixo2.sodalite.category.gpu;

import lombok.With;
import org.joml.Vector2i;
import org.joml.Vector3i;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.nio.file.Path;

/// Helpers for `SDL_CreateGPUComputePipeline`
public class ComputeShader {

    public record Creator<T extends Exception>(
            ShaderSource<T> source,
            ComputeShader.ThreadCount threadCount,
            ComputeShader.Parameters parameter
    ) {
        public static Creator<IOException> of(
                @ShaderFormat int shaderFormat,
                Path path,
                ThreadCount threadCount,
                ComputeShader.Parameters parameter
        ) {
            return new Creator<>(ShaderSource.of(shaderFormat, path), threadCount, parameter);
        }
        public static Creator<IOException> of(
                @ShaderFormat int shaderFormat,
                java.io.InputStream inputStream,
                ThreadCount threadCount,
                ComputeShader.Parameters parameter
        ) {
            return new Creator<>(ShaderSource.of(shaderFormat, inputStream), threadCount, parameter);
        }
        public static Creator<RuntimeException> of(
                @ShaderFormat int shaderFormat,
                byte[] bytes,
                ThreadCount threadCount,
                ComputeShader.Parameters parameter
        ) {
            return new Creator<>(ShaderSource.of(shaderFormat, bytes), threadCount, parameter);
        }
        public static Creator<RuntimeException> of(
                @ShaderFormat int shaderFormat,
                MemorySegment code,
                ThreadCount threadCount,
                ComputeShader.Parameters parameter
        ) {
            return new Creator<>(ShaderSource.of(shaderFormat, code), threadCount, parameter);
        }
        public static <T extends Exception> Creator<T> of(
                ShaderSource<T> source,
                ThreadCount threadCount,
                ComputeShader.Parameters parameter
        ) {
            return new Creator<>(source, threadCount, parameter);
        }
    }

    @With
    public record Parameters(
            String entryPoint,
            int numSamplers,
            int numReadonlyStorageTextures,
            int numReadonlyStorageBuffers,
            int numReadwriteStorageTextures,
            int numReadwriteStorageBuffers,
            int numUniformBuffers
    ) {
        public Parameters {
            if (numSamplers < 0) throw new IllegalArgumentException("num_samplers must be non-negative");
            if (numReadonlyStorageTextures < 0)
                throw new IllegalArgumentException("num_readonly_storage_textures must be non-negative");
            if (numReadonlyStorageBuffers < 0)
                throw new IllegalArgumentException("num_readonly_storage_buffers must be non-negative");
            if (numReadwriteStorageTextures < 0)
                throw new IllegalArgumentException("num_readwrite_storage_textures must be non-negative");
            if (numReadwriteStorageBuffers < 0)
                throw new IllegalArgumentException("num_readwrite_storage_buffers must be non-negative");
            if (numUniformBuffers < 0)
                throw new IllegalArgumentException("num_uniform_buffers must be non-negative");
            if (entryPoint.isEmpty()) throw new IllegalArgumentException("entryPoint must be non-empty");
        }
        public Parameters(
                int num_samplers,
                int num_readonly_storage_textures,
                int num_readonly_storage_buffers,
                int num_readwrite_storage_textures,
                int num_readwrite_storage_buffers,
                int num_uniform_buffers
        ) {
            this(
                    "main",
                    num_samplers,
                    num_readonly_storage_textures,
                    num_readonly_storage_buffers,
                    num_readwrite_storage_textures,
                    num_readwrite_storage_buffers,
                    num_uniform_buffers
            );
        }

        public static Parameters of(
            int num_samplers,
            int num_readonly_storage_textures,
            int num_readonly_storage_buffers,
            int num_readwrite_storage_textures,
            int num_readwrite_storage_buffers,
            int num_uniform_buffers
        ) {
            return new Parameters(
                    num_samplers,
                    num_readonly_storage_textures,
                    num_readonly_storage_buffers,
                    num_readwrite_storage_textures,
                    num_readwrite_storage_buffers,
                    num_uniform_buffers
            );
        }

        public static Parameters of(
                String entryPoint,
                int num_samplers,
                int num_readonly_storage_textures,
                int num_readonly_storage_buffers,
                int num_readwrite_storage_textures,
                int num_readwrite_storage_buffers,
                int num_uniform_buffers
        ) {
            return new Parameters(
                    entryPoint,
                    num_samplers,
                    num_readonly_storage_textures,
                    num_readonly_storage_buffers,
                    num_readwrite_storage_textures,
                    num_readwrite_storage_buffers,
                    num_uniform_buffers
            );
        }
        public static Parameters none() {
            return Parameters.of(0, 0, 0, 0, 0, 0);
        }

    }

    public record ThreadCount(
            int x,
            int y,
            int z
    ) {
        public ThreadCount {
            if (x <= 0) throw new IllegalArgumentException("Thread count x must be positive");
            if (y <= 0) throw new IllegalArgumentException("Thread count y must be positive");
            if (z <= 0) throw new IllegalArgumentException("Thread count z must be positive");
        }

        public static ThreadCount of(int x, int y, int z) {
            return new ThreadCount(x, y, z);
        }
        public static ThreadCount of(int x, int y) {
            return of(x, y, 1);
        }
        public static ThreadCount of(int x) {
            return of(x, 1, 1);
        }
        public static ThreadCount of(Vector3i vector3i) {
            return of(vector3i.x, vector3i.y, vector3i.z);
        }
        public static ThreadCount of(Vector2i vector2i) {
            return of(vector2i.x, vector2i.y);
        }

    }

}
