package io.github.plixo2.sodalite.category.gpu;

import io.github.plixo2.sodalite.memory.MemorySource;
import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.With;

import java.lang.foreign.MemorySegment;

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
        @ShaderFormat int shaderFormat,
        MemorySource<T> source,
        Shader.Parameters parameter
    ) {
        public static <T extends Exception> Creator<T> of(
                @ShaderFormat int shaderFormat,
                MemorySource<T> source,
                Shader.Parameters parameter
        ) {
            return new Creator<>(shaderFormat, source, parameter);
        }
    }

    @With
    public record Parameters(
            int samplers,
            int storageTextures,
            int storageBuffers,
            int uniformBuffers,
            String entryPoint
    ) {
        public Parameters {
            if (samplers < 0) throw new IllegalArgumentException("numSamplers must be non-negative");
            if (storageTextures < 0) throw new IllegalArgumentException("numStorageTextures must be non-negative");
            if (storageBuffers < 0) throw new IllegalArgumentException("numStorageBuffers must be non-negative");
            if (uniformBuffers < 0) throw new IllegalArgumentException("numUniformBuffers must be non-negative");
            if (entryPoint.isEmpty()) throw new IllegalArgumentException("entryPoint must be non-empty");
        }

        public Parameters(
                int samplers,
                int storageTextures,
                int storageBuffers,
                int uniformBuffers
        ) {
            this(
                    samplers,
                    storageTextures,
                    storageBuffers,
                    uniformBuffers,
                    "main"
            );
        }

        public static Parameters of(
                int samplers,
                int storageTextures,
                int storageBuffers,
                int uniformBuffers,
                String entryPoint
        ) {
            return new Parameters(
                    samplers,
                    storageTextures,
                    storageBuffers,
                    uniformBuffers,
                    entryPoint
            );
        }

        public static Parameters of(
                int samplers,
                int storageTextures,
                int storageBuffers,
                int uniformBuffers
        ) {
            return new Parameters(
                    samplers,
                    storageTextures,
                    storageBuffers,
                    uniformBuffers
            );
        }
        public static Parameters none() {
            return Parameters.of(0, 0, 0, 0);
        }

    }

    @Override
    public String toString() {
        return "Shader{" +
                "segment=" + this.segment.address() +
                '}';
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.segment.address());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Shader other && this.segment.address() == other.segment.address();
    }
}
