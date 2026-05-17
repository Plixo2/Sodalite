package io.github.plixo2.sodalite.category.gpu;


import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;
import org.libsdl.sdl.SDL_FColor;
import org.libsdl.sdl.SDL_GPUColorTargetInfo;
import org.libsdl.sdl.SDL_GPUDepthStencilTargetInfo;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Objects;


/// @apiNote SDL_GPURenderPass
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

    public void bindPipeline(GraphicsPipeline pipeline) {
        GPU.bindGPUGraphicsPipeline(this, pipeline);
    }

    public void bindVertexBuffer(
            int slot,
            Buffer buffer,
            int bufferOffset
    ) {
        GPU.bindGPUVertexBuffer(this, slot, buffer, bufferOffset);
    }

    public void bindVertexBuffer(
            int slot,
            Buffer buffer
    ) {
        GPU.bindGPUVertexBuffer(this, slot, buffer, 0);
    }

    public void bindVertexStorageBuffer(
            int slot,
            Buffer buffer
    ) {
        GPU.bindGPUVertexStorageBuffer(this, slot, buffer);
    }

    public void drawPrimitives(
            int numVertices,
            int numInstances,
            int firstVertex,
            int firstInstance
    ) {
        GPU.drawGPUPrimitives(this, numVertices, numInstances, firstVertex, firstInstance);
    }


    @Override
    public void close() {
        if (this.isEnded) {
            throw new IllegalStateException("Render pass has already been ended");
        }
        GPU.endGPURenderPass(this);
        this.isEnded = true;
    }


    /// @apiNote SDL_GPUColorTargetInfo
    public static class ColorTargetInfo {
        public Texture texture;
        public int mipLevel = 0;
        public int layerOrDepthPlane = 0;
        public Vector4f clearColor = new Vector4f();
        public LoadOp loadOp;
        public StoreOp storeOp = StoreOp.STORE;
        public @Nullable Texture resolveTexture = null;
        public int resolveMipLevel = 0;
        public int resolveLayer = 0;
        public Cycle cycle;
        public Cycle cycleResolveTexture = Cycle.FALSE;

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

        void put(MemorySegment segment) {
            SDL_GPUColorTargetInfo.initialize(
                    segment,
                    Objects.requireNonNull(this.texture).segment(),
                    this.mipLevel,
                    this.layerOrDepthPlane,
                    Fcolor(this.clearColor),
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


    /// @apiNote SDL_GPUDepthStencilTargetInfo
    public static class DepthStencilTargetInfo {
        public Texture texture;
        public float clearDepth = 0f;
        public LoadOp loadOp;
        public StoreOp storeOp = StoreOp.STORE;
        public LoadOp stencilLoadOp = LoadOp.DONT_CARE;
        public StoreOp stencilStoreOp = StoreOp.DONT_CARE;
        public Cycle cycle;
        public byte clearStencil = 0;
        public byte mip_level = 0;
        public byte layer = 0;

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
            SDL_GPUDepthStencilTargetInfo.initialize(
                    segment,
                    Objects.requireNonNull(this.texture).segment(),
                    this.clearDepth,
                    this.loadOp.code(),
                    this.storeOp.code(),
                    this.stencilLoadOp.code(),
                    this.stencilStoreOp.code(),
                    this.cycle.value(),
                    this.clearStencil,
                    this.mip_level,
                    this.layer
            );
        }

    }

    private static final MemorySegment colorSegment = Arena.global().allocate(SDL_FColor.layout());

    /// @apiNote SDL_FColor
    private static MemorySegment Fcolor(Vector4f color) {
        SDL_FColor.initialize(colorSegment, color.x, color.y, color.z, color.w);
        return colorSegment;
    }



}
