package io.github.plixo2.sodalite.category.gpu;

import io.github.plixo2.sodalite.memory.MemorySource;
import lombok.With;
import org.joml.Vector2i;
import org.joml.Vector3i;

import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.MemorySegment;
import java.nio.file.Path;

/// Helpers for `SDL_CreateGPUComputePipeline`
public class ComputeShader {
    private ComputeShader() {}

    public record Creator<T extends Exception>(
            @ShaderFormat int shaderFormat,
            MemorySource<T> source,
            ComputeShader.ThreadCount threadCount,
            ComputeShader.Parameters parameter
    ) {
        public static <T extends Exception> Creator<T> of(
                @ShaderFormat int shaderFormat,
                MemorySource<T> source,
                ThreadCount threadCount,
                ComputeShader.Parameters parameter
        ) {
            return new Creator<>(shaderFormat, source, threadCount, parameter);
        }
    }

    @With
    public record Parameters(
            int samplers,
            int readonlyStorageTextures,
            int readonlyStorageBuffers,
            int readwriteStorageTextures,
            int readwriteStorageBuffers,
            int nniformBuffers,
            String entryPoint
    ) {
        public Parameters {
            if (samplers < 0) throw new IllegalArgumentException("num_samplers must be non-negative");
            if (readonlyStorageTextures < 0)
                throw new IllegalArgumentException("num_readonly_storage_textures must be non-negative");
            if (readonlyStorageBuffers < 0)
                throw new IllegalArgumentException("num_readonly_storage_buffers must be non-negative");
            if (readwriteStorageTextures < 0)
                throw new IllegalArgumentException("num_readwrite_storage_textures must be non-negative");
            if (readwriteStorageBuffers < 0)
                throw new IllegalArgumentException("num_readwrite_storage_buffers must be non-negative");
            if (nniformBuffers < 0)
                throw new IllegalArgumentException("num_uniform_buffers must be non-negative");
            if (entryPoint.isEmpty()) throw new IllegalArgumentException("entryPoint must be non-empty");
        }
        public Parameters(
                int samplers,
                int readonlyStorageTextures,
                int readonlyStorageBuffers,
                int readwriteStorageTextures,
                int readwriteStorageBuffers,
                int uniformBuffers
        ) {
            this(
                    samplers,
                    readonlyStorageTextures,
                    readonlyStorageBuffers,
                    readwriteStorageTextures,
                    readwriteStorageBuffers,
                    uniformBuffers,
                    "main"
            );
        }

        public static Parameters of(
            int samplers,
            int readonlyStorageTextures,
            int readonlyStorageBuffers,
            int readwriteStorageTextures,
            int readwriteStorageBuffers,
            int uniformBuffers
        ) {
            return new Parameters(
                    samplers,
                    readonlyStorageTextures,
                    readonlyStorageBuffers,
                    readwriteStorageTextures,
                    readwriteStorageBuffers,
                    uniformBuffers
            );
        }

        public static Parameters of(
                int samplers,
                int readonlyStorageTextures,
                int readonlyStorageBuffers,
                int readwriteStorageTextures,
                int readwriteStorageBuffers,
                int uniformBuffers,
                String entryPoint
        ) {
            return new Parameters(
                    samplers,
                    readonlyStorageTextures,
                    readonlyStorageBuffers,
                    readwriteStorageTextures,
                    readwriteStorageBuffers,
                    uniformBuffers,
                    entryPoint
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
