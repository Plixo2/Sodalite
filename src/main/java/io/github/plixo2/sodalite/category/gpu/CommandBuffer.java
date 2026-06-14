package io.github.plixo2.sodalite.category.gpu;


import com.google.errorprone.annotations.CheckReturnValue;
import io.github.plixo2.sodalite.category.video.Window;
import io.github.plixo2.sodalite.memory.WriteBuffer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_GPUCommandBuffer
public class CommandBuffer implements AutoCloseable {

    private final MemorySegment segment;
    private final Device device;

    @Getter
    private boolean isCanceled = false;
    @Getter
    private boolean isSubmitted = false;

    private final @Nullable FenceReference fenceReference;

    @Nullable Texture acquiredSwapchainTexture;

    CommandBuffer(
            @Nullable FenceReference fenceReference,
            Device device,
            MemorySegment segment
    ) {
        this.fenceReference = fenceReference;
        if (fenceReference != null) {
            fenceReference.setCommandBuffer(this);
        }
        this.device = device;
        this.segment = segment;
    }

    public MemorySegment segment() {
        if (this.isCanceled) {
            throw new IllegalStateException("Command buffer has been canceled");
        } else if (this.isSubmitted) {
            throw new IllegalStateException("Command buffer has already been submitted");
        }
        return this.segment;
    }

    @Override
    public void close() {
        if (this.acquiredSwapchainTexture != null) {
            this.acquiredSwapchainTexture.markReleased();
        }
        if (!this.isCanceled) {
            if (this.isSubmitted) {
                throw new IllegalStateException("Command buffer has already been submitted");
            }
            if (this.fenceReference != null) {
                this.fenceReference.setFence(
                        GPU.submitCommandBufferAndAcquire(
                                this.device,
                                this
                        )
                );
            } else {
                GPU.submitGPUCommandBuffer(this);
            }
            this.isSubmitted = true;
        }
    }

    public void cancel() {
        if (this.isSubmitted) {
            throw new IllegalStateException("Command buffer has already been submitted");
        }
        this.isCanceled = true;
        GPU.cancelGPUCommandBuffer(this);
    }


    public @Nullable Texture waitAndAcquireSwapchainTexture(Window window) {
        return GPU.waitAndAcquireGPUSwapchainTexture(this, window);
    }

    /// You should use [CommandBuffer#waitAndAcquireSwapchainTexture] unless you know what
    /// you are doing with timing.
    public @Nullable Texture acquireGPUSwapchainTexture(Window window) {
        return GPU.acquireGPUSwapchainTexture(this, window);
    }

    @CheckReturnValue
    public RenderPass beginRenderPass(
            @Nullable RenderPass.DepthStencilTargetInfo depthStencilTarget,
            RenderPass.ColorTargetInfo... colorTargets
    ) {
        return GPU.beginGPURenderPass(this, colorTargets, depthStencilTarget);
    }

    @CheckReturnValue
    public ComputePass beginComputePass(
            ComputePass.Binding... bindings
    ) {
        return GPU.beginComputePass(this, bindings);
    }

    @CheckReturnValue
    public CopyPass beginCopyPass() {
        return GPU.beginGPUCopyPass(this);
    }

    public void pushFragmentUniform(int slot, WriteBuffer<?> struct) {
        GPU.pushGPUFragmentUniformData(this, slot, struct);
    }
    public void pushVertexUniform(int slot, WriteBuffer<?> struct) {
        GPU.pushGPUVertexUniformData(this, slot, struct);
    }
    public void pushComputeUniform(int slot, WriteBuffer<?> struct) {
        GPU.pushGPUComputeUniformData(this, slot, struct);
    }

    /// [CopyPass#copy] does transfer the memory directly. \
    /// [CommandBuffer#blit] will 'render' source onto destination,
    /// which allows for scaling and filtering.
    ///
    /// @see CopyPass#copy(TextureLocation, TextureLocation, long, long, long, Cycle)
    public void blit(
        BlitInfo blitInfo
    ) {
        GPU.blitTexture(this, blitInfo);
    }

    /// [CopyPass#copy] does transfer the memory directly. \
    /// [CommandBuffer#blit] will 'render' source onto destination,
    /// which allows for scaling and filtering.
    ///
    /// @see CopyPass#copy(TextureLocation, TextureLocation, long, long, long, Cycle)
    public void blit(
            Texture source,
            Texture destination,
            Cycle cycle
    ) {
        blit(source, destination, 0, cycle);
    }

    public void blit(
            Texture source,
            Texture destination,
            int mipLevel,
            Cycle cycle
    ) {
        var info = BlitInfo.of(
                BlitInfo.Region.of(source, mipLevel),
                BlitInfo.Region.of(destination, mipLevel),
                cycle
        );
        blit(info);
    }



}
