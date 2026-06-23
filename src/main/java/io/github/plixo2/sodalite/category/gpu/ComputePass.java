package io.github.plixo2.sodalite.category.gpu;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joml.Vector3i;
import org.libsdl.sdl.SDL_GPUStorageBufferReadWriteBinding;
import org.libsdl.sdl.SDL_GPUStorageTextureReadWriteBinding;

import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_GPUComputePass
public class ComputePass implements AutoCloseable {

    private final MemorySegment segment;
    private boolean isEnded = false;

    ComputePass(
            MemorySegment segment
    ) {
        this.segment = segment;
    }

    public MemorySegment segment() {
        if (this.isEnded) {
            throw new IllegalStateException("Compute pass has already been ended");
        }
        return this.segment;
    }

    @Override
    public void close() {
        if (this.isEnded) {
            throw new IllegalStateException("Compute pass has already been ended");
        }
        GPU.endComputePass(this);
        this.isEnded = true;
    }

    public boolean hasEnded() {
        return this.isEnded;
    }

    public void bindPipeline(ComputePipeline pipeline) {
        GPU.bindComputePipeline(this, pipeline);
    }

    public void dispatch(
            int groupCountX,
            int groupCountY,
            int groupCountZ
    ) {
        GPU.dispatchCompute(this, groupCountX, groupCountY, groupCountZ);
    }

    public void dispatch(
            Vector3i groupCount
    ) {
        dispatch(groupCount.x, groupCount.y, groupCount.z);
    }

    public void dispatchIndirect(
            Buffer indirectBuffer,
            long offset
    ) {
        GPU.dispatchComputeIndirect(this, indirectBuffer, offset);
    }

    public void dispatchIndirect(
            Buffer indirectBuffer
    ) {
        dispatchIndirect(indirectBuffer, 0);
    }


    public void bindComputeSampler(
            int slot,
            Texture texture,
            Sampler sampler
    ) {
        GPU.bindComputeSampler(
                this,
                slot,
                texture,
                sampler
        );
    }
    public void bindComputeSamplers(
            int firstSlot,
            Texture[] textures,
            Sampler[] samplers
    ) {
        GPU.bindComputeSamplers(
                this,
                firstSlot,
                textures,
                samplers
        );
    }
    public void bindComputeSamplers(
            int firstSlot,
            Texture[] textures,
            Sampler sampler
    ) {
        GPU.bindComputeSamplers(
                this,
                firstSlot,
                textures,
                sampler
        );
    }

    public void bindBuffer(
            int slot,
            Buffer buffer
    ) {
        GPU.bindGPUComputeStorageBuffer(this, slot, buffer);
    }

    public void bindStorageTexture(
            int slot,
            Texture texture
    ) {
        GPU.bindGPUComputeStorageTexture(this, slot, texture);
    }

    @Override
    public String toString() {
        return "ComputePass{" +
                "segment=" + this.segment.address() +
                '}';
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.segment.address());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ComputePass other && this.segment.address() == other.segment.address();
    }

    public sealed interface Binding permits TextureBinding, BufferBinding {

        static Binding texture(
                Texture texture,
                Cycle cycle
        ) {
            return TextureBinding.of(texture, cycle);
        }
        static Binding buffer(
                Buffer buffer,
                Cycle cycle
        ) {
            return BufferBinding.of(buffer, cycle);
        }

    }

    /// @sdlAPI SDL_GPUStorageTextureReadWriteBinding
    @Setter
    @Getter
    @ToString
    @EqualsAndHashCode
    public static final class TextureBinding implements Binding {

        private Texture texture;
        private int mipLevel = 0;
        private int layer = 0;
        private Cycle cycle;

        private TextureBinding(
                Texture texture,
                Cycle cycle
        ) {
            this.texture = texture;
            this.cycle = cycle;
        }

        public static TextureBinding of(
                Texture texture,
                Cycle cycle
        ) {
            return new TextureBinding(texture, cycle);
        }

        public static TextureBinding ofMipLevel(
                Texture texture,
                Cycle cycle,
                int mipLevel
        ) {
            var binding = new TextureBinding(texture, cycle);
            return binding.mipLevel(mipLevel);
        }
        public static TextureBinding ofLayer(
                Texture texture,
                Cycle cycle,
                int layer
        ) {
            var binding = new TextureBinding(texture, cycle);
            return binding.layer(layer);
        }

        public static TextureBinding of(
                Texture texture,
                Cycle cycle,
                int mipLevel,
                int layer
        ) {
            var binding = new TextureBinding(texture, cycle);
            return binding.mipLevel(mipLevel).layer(layer);
        }


        public ComputePass.TextureBinding mipLevel(
                int mipLevel
        ) {
            if (mipLevel < 0 || mipLevel >= this.texture.mipLevelCount()) {
                throw new IllegalArgumentException("Mip level index out of bounds");
            }
            this.mipLevel = mipLevel;
            return this;
        }
        public ComputePass.TextureBinding layer(
                int layer
        ) {
            if (layer < 0 || layer >= this.texture.layerCountOrDepth()) {
                throw new IllegalArgumentException("Layer index out of bounds");
            }
            this.layer = layer;
            return this;
        }

        void put(MemorySegment segment) {
            SDL_GPUStorageTextureReadWriteBinding.initialize(
                    segment,
                    this.texture.segment(),
                    this.mipLevel,
                    this.layer,
                    this.cycle.value()
            );
        }
    }


    /// @sdlAPI SDL_GPUStorageBufferReadWriteBinding
    @Setter
    @Getter
    @ToString
    @EqualsAndHashCode
    public static final class BufferBinding implements Binding {
        private Buffer buffer;
        private Cycle cycle;

        private BufferBinding(
                Buffer buffer,
                Cycle cycle
        ) {
            this.buffer = buffer;
            this.cycle = cycle;
        }
        public static BufferBinding of(
                Buffer buffer,
                Cycle cycle
        ) {
            return new BufferBinding(buffer, cycle);
        }

        void put(MemorySegment segment) {
            SDL_GPUStorageBufferReadWriteBinding.initialize(
                    segment,
                    this.buffer.segment(),
                    this.cycle.value()
            );
        }
    }

}
