package io.github.plixo2.sodalite.category.gpu;


import com.google.errorprone.annotations.CheckReturnValue;
import io.github.plixo2.sodalite.category.video.Window;
import io.github.plixo2.sodalite.memory.WriteBuffer;
import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_GPUCommandBuffer
public class CommandBuffer implements AutoCloseable {

    private final MemorySegment segment;
    private final Device device;

    @Getter
    private boolean isCanceled = false;
    @Getter
    private boolean isSubmitted = false;


    CommandBuffer(
            Device device,
            MemorySegment segment
    ) {
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
        if (this.isSubmitted) {
            throw new IllegalStateException("Command buffer has already been submitted");
        }
        if (this.isCanceled) {
            return;
        }
        GPU.submitGPUCommandBuffer(this);
        this.isSubmitted = true;
    }

    @CheckReturnValue
    public Fence closeAndAcquireFence(ResourceSet resources) {
        if (this.isSubmitted) {
            throw new IllegalStateException("Command buffer has already been submitted");
        }
        if (this.isCanceled) {
            throw new IllegalStateException("Command buffer has been canceled");
        }
        var fence = GPU.submitCommandBufferAndAcquire(
                resources,
                this.device,
                this
        );
        this.isSubmitted = true;
        return fence;
    }


    public void cancel() {
        if (this.isSubmitted) {
            throw new IllegalStateException("Command buffer has already been submitted");
        }
        this.isCanceled = true;
        GPU.cancelGPUCommandBuffer(this);
    }

    /// @return a Texture that is owned by the swapchain and will be released when the
    ///         next frame is presented, or null
    public @Nullable Texture waitAndAcquireSwapchainTexture(Window window) {
        return GPU.waitAndAcquireSwapchainTexture(this, window);
    }

    /// "You should use [CommandBuffer#waitAndAcquireSwapchainTexture] unless you know what
    /// you are doing with timing."
    /// @return a Texture that is owned by the swapchain and will be released when the
    ///         next frame is presented, or null
    public @Nullable Texture acquireSwapchainTexture(Window window) {
        return GPU.acquireSwapchainTexture(this, window);
    }

    @CheckReturnValue
    public RenderPass beginRenderPass(
            @Nullable RenderPass.DepthStencilTargetInfo depthStencilTarget,
            RenderPass.ColorTargetInfo... colorTargets
    ) {
        return GPU.beginRenderPass(this, colorTargets, depthStencilTarget);
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
    /// which allows scaling and filtering.
    ///
    /// @see CopyPass#copy(TextureLocation, TextureLocation, long, long, long, Cycle)
    public void blit(
        BlitInfo blitInfo
    ) {
        GPU.blitTexture(this, blitInfo);
    }

    /// [CopyPass#copy] does transfer the memory directly. \
    /// [CommandBuffer#blit] will 'render' source onto destination,
    /// which allows scaling and filtering.
    ///
    /// @see CopyPass#copy(TextureLocation, TextureLocation, long, long, long, Cycle)
    public void blit(
            Texture source,
            Texture destination,
            Cycle cycle
    ) {
        blit(source, destination, 0, cycle);
    }

    // [CopyPass#copy] does transfer the memory directly. \
    /// [CommandBuffer#blit] will 'render' source onto destination,
    /// which allows for scaling and filtering.
    ///
    /// @see CopyPass#copy(TextureLocation, TextureLocation, long, long, long, Cycle)
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

    public void generateMipmaps(Texture texture) {
        GPU.generateMipmaps(this, texture);
    }

    public void setBlendConstants(float r, float g, float b, float a) {
        GPU.setBlendConstants(this, r, g, b, a);
    }
    public void setBlendConstants(Vector4f color) {
        setBlendConstants(color.x, color.y, color.z, color.w);
    }

    public void insertDebugLabel(String label) {
        GPU.insertDebugLabel(this, label);
    }

    /// @return a DebugGroup that will automatically pop the debug group when closed.
    /// @see #pushDebugGroup
    /// @see #popDebugGroup
    @CheckReturnValue
    public DebugGroup withDebugGroup(String groupLabel) {
        pushDebugGroup(groupLabel);
        return new DebugGroup();
    }

    /// @see #withDebugGroup for a variant to use in a try-with-resource block
    public void pushDebugGroup(String groupLabel) {
        GPU.pushDebugGroup(this, groupLabel);
    }

    /// @see #withDebugGroup for a variant to use in a try-with-resource block
    public void popDebugGroup() {
        GPU.popDebugGroup(this);
    }


    @Override
    public String toString() {
        return "CommandBuffer{" +
                "segment=" + this.segment.address() +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CommandBuffer other && this.segment.address() == other.segment.address();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.segment.address());
    }

    public class DebugGroup implements AutoCloseable {
        private DebugGroup() {}

        @Override
        public void close() {
            GPU.popDebugGroup(CommandBuffer.this);
        }
    }

    static class SwapchainTexture extends Texture {

        SwapchainTexture(
                MemorySegment segment,
                int width,
                int height
        ) {
            super(
                    segment,
                    TextureType.TEXTURE_2D,
                    TextureFormat.INVALID,
                    TextureUsageFlags.COLOR_TARGET,
                    width,
                    height,
                    1,
                    1,
                    SampleCount.COUNT_1,
                    null
            );
        }


        @Override
        public TextureFormat format() {
            throw new UnsupportedOperationException(
                    "Cannot get the format of a swapchain texture. "
                    + "Call Device.getSwapchainTextureFormat(window)"
            );
        }

        @Override
        public void setName(Device device, String name) {
            throw new UnsupportedOperationException(
                    "Cannot set the name of a swapchain texture. "
            );
        }

        @Override
        public SampleCount sampleCount() {
            throw new UnsupportedOperationException(
                    "Cannot get the sample count of a swapchain texture. "
            );
        }

        @Override
        public String toString() {
            return "SwapchainTexture{" +
                    "segment=" + this.segment.address() +
                    ", width=" + this.width() +
                    ", height=" + this.height() +
                    '}';
        }
    }

}
