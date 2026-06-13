package io.github.plixo2.sodalite.category.gpu;


import io.github.plixo2.sodalite.category.rect.Rect;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;
import org.libsdl.sdl.SDL_FColor;
import org.libsdl.sdl.SDL_GPUColorTargetInfo;
import org.libsdl.sdl.SDL_GPUDepthStencilTargetInfo;

import java.lang.foreign.MemorySegment;


/// @sdlAPI SDL_GPURenderPass
public class RenderPass implements AutoCloseable {

    private final MemorySegment segment;
    private boolean isEnded = false;

    RenderPass(
        MemorySegment segment
    ) {
        this.segment = segment;
    }

    public MemorySegment segment() {
        if (this.isEnded) {
            throw new IllegalStateException("Render pass has already been ended");
        }
        return this.segment;
    }

    @Override
    public void close() {
        if (this.isEnded) {
            throw new IllegalStateException("Render pass has already been ended");
        }
        GPU.endGPURenderPass(this);
        this.isEnded = true;
    }

    public void bindPipeline(GraphicsPipeline pipeline) {
        GPU.bindGPUGraphicsPipeline(this, pipeline);
    }

    public void bindVertexBuffer(
            int slot,
            Buffer buffer,
            int bufferOffset
    ) {
        GPU.bindVertexBuffer(this, slot, buffer, bufferOffset);
    }

    public void bindVertexBuffer(
            int slot,
            Buffer buffer
    ) {
        GPU.bindVertexBuffer(this, slot, buffer, 0);
    }

    public void bindIndexBuffer(
            Buffer buffer,
            int bufferOffset,
            IndexElementSize indexElementSize
    ) {
        GPU.bindIndexBuffer(this, buffer, bufferOffset, indexElementSize);
    }

    public void bindIndexBuffer(
            Buffer buffer,
            IndexElementSize indexElementSize
    ) {
        GPU.bindIndexBuffer(this, buffer, 0, indexElementSize);
    }

    public void bindVertexStorageBuffer(
            int slot,
            Buffer buffer
    ) {
        GPU.bindGPUVertexStorageBuffer(this, slot, buffer);
    }

    public void bindFragmentSampler(
            int slot,
            Texture texture,
            Sampler sampler
    ) {
        GPU.bindFragmentSampler(
                this,
                slot,
                texture,
                sampler
        );
    }
    public void bindFragmentSamplers(
            int firstSlot,
            Texture[] textures,
            Sampler[] samplers
    ) {
        GPU.bindFragmentSamplers(
                this,
                firstSlot,
                textures,
                samplers
        );
    }

    public void bindFragmentSamplers(
            int firstSlot,
            Texture[] textures,
            Sampler sampler
    ) {
        GPU.bindFragmentSamplers(
                this,
                firstSlot,
                textures,
                sampler
        );
    }

    public void bindFragmentStorageBuffer(
            int slot,
            Buffer buffer
    ) {
        GPU.bindFragmentStorageBuffer(this, slot, buffer);
    }

    public void bindFragmentStorageTexture(
            int slot,
            Texture texture
    ) {
        GPU.bindFragmentStorageTexture(this, slot, texture);
    }


    public void bindVertexSampler(
            int slot,
            Texture texture,
            Sampler sampler
    ) {
        GPU.bindVertexSampler(
                this,
                slot,
                texture,
                sampler
        );
    }

    public void bindVertexStorageTexture(
            int slot,
            Texture texture
    ) {
        GPU.bindVertexStorageTexture(this, slot, texture);
    }


    public void drawPrimitives(
            int numVertices,
            int numInstances,
            int firstVertex,
            int firstInstance
    ) {
        GPU.drawGPUPrimitives(this, numVertices, numInstances, firstVertex, firstInstance);
    }

    public void drawIndexed(
            int numIndices,
            int numInstances,
            int firstIndex,
            int vertexOffset,
            int firstInstance
    ) {
        GPU.drawGPUIndexedPrimitives(
                this,
                numIndices,
                numInstances,
                firstIndex,
                vertexOffset,
                firstInstance
        );
    }

    public void drawPrimitivesIndirect(
            Buffer indirectBuffer,
            long bufferOffset,
            int drawCount
    ) {
        GPU.drawGPUPrimitivesIndirect(this, indirectBuffer, bufferOffset, drawCount);
    }

    public void drawIndexedIndirect(
            Buffer indirectBuffer,
            long bufferOffset,
            int drawCount
    ) {
        GPU.drawGPUIndexedPrimitivesIndirect(this, indirectBuffer, bufferOffset, drawCount);
    }


    public void setScissor(
            int x,
            int y,
            int width,
            int height
    ) {
        GPU.setGPUScissor(this, x, y, width, height);
    }

    public void setScissor(
            Rect rect
    ) {
        setScissor(rect.x(), rect.y(), rect.width(), rect.height());
    }

    public void setViewport(Viewport viewport) {
        GPU.setGPUViewport(this, viewport);
    }




    /// @sdlAPI SDL_GPUColorTargetInfo
    @Setter
    public static class ColorTargetInfo {
        private Texture texture;
        private int mipLevel = 0;
        private int layerOrDepthPlane = 0;
        private Vector4f clearColor = new Vector4f();
        private LoadOp loadOp;
        private StoreOp storeOp = StoreOp.STORE;
        private @Nullable Texture resolveTexture = null;
        private int resolveMipLevel = 0;
        private int resolveLayer = 0;
        private Cycle cycle;
        private Cycle cycleResolveTexture = Cycle.FALSE;

        private ColorTargetInfo(
                Texture texture,
                LoadOp loadOp,
                Cycle cycle
        ) {
            this.texture = texture;
            this.loadOp = loadOp;
            this.cycle = cycle;
        }

        public static ColorTargetInfo clear(
                Texture texture,
                Vector4f clearColor,
                Cycle cycle
        ) {
            var info = new ColorTargetInfo(
                texture,
                LoadOp.CLEAR,
                cycle
            );
            info.clearColor = clearColor;
            return info;
        }
        public static ColorTargetInfo noClear(
                Texture texture,
                Cycle cycle
        ) {
            return new ColorTargetInfo(
                texture,
                LoadOp.DONT_CARE,
                cycle
            );
        }
        public static ColorTargetInfo resolve(
                Texture texture,
                Vector4f clearColor,
                Texture resolveTexture,
                Cycle cycle,
                Cycle cycleResolveTexture
        ) {
            var info = clear(texture, clearColor, cycle);
            info.storeOp = StoreOp.RESOLVE;
            info.resolveTexture = resolveTexture;
            info.resolveMipLevel = 0;
            info.resolveLayer = 0;
            info.cycleResolveTexture = cycleResolveTexture;
            return info;
        }

        void put(MemorySegment segment) {
            SDL_GPUColorTargetInfo.initialize(
                    segment,
                    this.texture.segment(),
                    this.mipLevel,
                    this.layerOrDepthPlane,
                    fColor(SDL_GPUColorTargetInfo.clear_color(segment), this.clearColor),
                    this.loadOp.code(),
                    this.storeOp.code(),
                    this.resolveTexture == null ? MemorySegment.NULL : this.resolveTexture.segment(),
                    this.resolveMipLevel,
                    this.resolveLayer,
                    this.cycle.value(),
                    this.cycleResolveTexture.value()
            );
        }

    }


    /// @sdlAPI SDL_GPUDepthStencilTargetInfo
    @Setter
    public static class DepthStencilTargetInfo {
        private Texture texture;
        private float clearDepth = 0f;
        private LoadOp loadOp;
        private StoreOp storeOp = StoreOp.STORE;
        private LoadOp stencilLoadOp = LoadOp.DONT_CARE;
        private StoreOp stencilStoreOp = StoreOp.DONT_CARE;
        private Cycle cycle;
        private int clearStencil = 0;
        private int mip_level = 0;
        private int layer = 0;

        private DepthStencilTargetInfo(
                Texture texture,
                LoadOp loadOp,
                Cycle cycle
        ) {
            this.texture = texture;
            this.loadOp = loadOp;
            this.cycle = cycle;
        }

        public static DepthStencilTargetInfo clear(
                Texture texture,
                float clearDepth,
                Cycle cycle
        ) {
            var info = new DepthStencilTargetInfo(
                    texture,
                    LoadOp.CLEAR,
                    cycle
            );
            info.clearDepth = clearDepth;
            return info;
        }
        public static DepthStencilTargetInfo noClear(
                Texture texture,
                Cycle cycle
        ) {
            return new DepthStencilTargetInfo(
                    texture,
                    LoadOp.DONT_CARE,
                    cycle
            );
        }

        void put(MemorySegment segment) {
            if (this.clearDepth < 0 || this.clearDepth > 255) {
                throw new IllegalArgumentException("Clear depth must be between 0 and 255");
            }
            if (this.mip_level < 0 || this.mip_level > 255) {
                throw new IllegalArgumentException("Mip level must be between 0 and 255");
            }
            if (this.layer < 0 || this.layer > 255) {
                throw new IllegalArgumentException("Layer must be between 0 and 255");
            }

            SDL_GPUDepthStencilTargetInfo.initialize(
                    segment,
                    this.texture.segment(),
                    this.clearDepth,
                    this.loadOp.code(),
                    this.storeOp.code(),
                    this.stencilLoadOp.code(),
                    this.stencilStoreOp.code(),
                    this.cycle.value(),
                    (byte) this.clearStencil,
                    (byte) this.mip_level,
                    (byte) this.layer
            );
        }

    }


    /// @sdlAPI SDL_FColor
    private static MemorySegment fColor(MemorySegment segment, Vector4f color) {
        SDL_FColor.initialize(segment, color.x, color.y, color.z, color.w);
        return segment;
    }



}
