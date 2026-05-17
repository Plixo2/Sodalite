package io.github.plixo2.sodalite.category.gpu;


import io.github.plixo2.sodalite.category.video.Window;
import io.github.plixo2.sodalite.memory.CStruct;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.MemorySegment;

/// @apiNote SDL_GPUCommandBuffer
public class CommandBuffer implements AutoCloseable {

    private final MemorySegment segment;
    private boolean isCanceled = false;
    private boolean isSubmitted = false;

    @Nullable Texture acquiredSwapchainTexture;

    CommandBuffer(MemorySegment segment) {
        this.segment = segment;
    }

    public @Nullable Texture waitAndAcquireSwapchainTexture(Window window) {
        return GPU.waitAndAcquireGPUSwapchainTexture(this, window);
    }

    public RenderPass beginRenderPass(
            @Nullable RenderPass.DepthStencilTargetInfo depthStencilTarget,
            RenderPass.ColorTargetInfo... colorTargets
    ) {
        return GPU.beginGPURenderPass(this, colorTargets, depthStencilTarget);
    }

    public CopyPass beginCopyPass() {
        return GPU.beginGPUCopyPass(this);
    }

    public void pushFragmentUniform(int slot, CStruct struct) {
        GPU.pushGPUFragmentUniformData(this, slot, struct);
    }
    public void pushVertexUniform(int slot, CStruct struct) {
        GPU.pushGPUVertexUniformData(this, slot, struct);
    }
    public void pushComputeUniform(int slot, CStruct struct) {
        GPU.pushGPUComputeUniformData(this, slot, struct);
    }


    public MemorySegment segment() {
        if (this.isCanceled) {
            throw new IllegalStateException("Command buffer has been canceled");
        } else if (this.isSubmitted) {
            throw new IllegalStateException("Command buffer has already been submitted");
        }
        return this.segment;
    }

    public void cancel() {
        if (this.isSubmitted) {
            throw new IllegalStateException("Command buffer has already been submitted");
        }
        this.isCanceled = true;
        GPU.cancelGPUCommandBuffer(this);
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
            GPU.submitGPUCommandBuffer(this);
            this.isSubmitted = true;
        }
    }
}
