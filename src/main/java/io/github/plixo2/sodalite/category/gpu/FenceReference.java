package io.github.plixo2.sodalite.category.gpu;


import com.google.errorprone.annotations.CheckReturnValue;
import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.jetbrains.annotations.Nullable;

/// Wrapper to be passed into [Device#acquireCommandBuffer].
/// The fence will be set once the command buffer is submitted.
/// @see Fence
public class FenceReference extends ResourceObject {

    private final Reference reference;

    private FenceReference(
            ResourceSet resources
    ) {
        var reference = new Reference();
        resources.register(this, () -> reference.close());
        this.reference = reference;
    }

    @CheckReturnValue
    public static FenceReference create(ResourceSet resources) {
        return new FenceReference(resources);
    }

    public Fence getFence() {
        ensureNotReleased();
        return this.reference.getFence();
    }

    void setCommandBuffer(CommandBuffer commandBuffer) {
        ensureNotReleased();
        this.reference.setCommandBuffer(commandBuffer);
    }

    // called once the command buffer is submitted
    void setFence(Fence fence) {
        ensureNotReleased();
        this.reference.setFence(fence);
    }



    private static class Reference {
        private @Nullable Fence fence = null;
        private @Nullable CommandBuffer commandBuffer = null;

        private synchronized void close() {
            if (this.commandBuffer == null) {
                throw new IllegalStateException(
                        "Fence reference has not been associated with a command buffer. "
                                + "Call Device.acquireCommandBuffer with this fence reference"
                );
            }
            if (!this.commandBuffer.isCanceled()
                    && !this.commandBuffer.isSubmitted()
            ) {
                throw new IllegalStateException("Command buffer has not been submitted or canceled");
            }
            if (this.fence != null) {
                this.fence.close();
            }
        }


        private synchronized Fence getFence() {
            if (this.commandBuffer == null) {
                throw new IllegalStateException(
                        "Fence reference has not been associated with a command buffer. "
                                + "Call Device.acquireCommandBuffer with this fence reference"
                );
            }
            if (this.commandBuffer.isCanceled()) {
                throw new IllegalStateException("Command buffer has been canceled, cannot get fence");
            }
            if (!this.commandBuffer.isSubmitted()) {
                throw new IllegalStateException("Command buffer has not been submitted yet, cannot get fence");
            }
            if (this.fence == null) {
                throw new IllegalStateException(
                        "Fence has not been set yet. This should never happen,"
                                + " as the fence should be set once the command buffer is submitted"
                );
            }
            return this.fence;
        }

        private synchronized void setCommandBuffer(CommandBuffer commandBuffer) {
            if (this.commandBuffer != null) {
                throw new IllegalStateException("Fence reference already has a command buffer");
            }
            this.commandBuffer = commandBuffer;
        }


        private synchronized void setFence(Fence fence) {
            if (this.fence != null) {
                throw new IllegalStateException("Fence reference already has a fence");
            }
            this.fence = fence;
        }

    }
}
