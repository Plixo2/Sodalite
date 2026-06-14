package io.github.plixo2.sodalite.category.gpu;



import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_GPUCopyPass
public class CopyPass implements AutoCloseable {

    private final MemorySegment segment;
    private boolean isEnded = false;

    CopyPass(
        MemorySegment segment
    ) {
        this.segment = segment;
    }

    public MemorySegment segment() {
        if (this.isEnded) {
            throw new IllegalStateException("Copy pass has already been ended");
        }
        return this.segment;
    }

    @Override
    public void close() {
        ensureNotEnded();
        GPU.endGPUCopyPass(this);
        this.isEnded = true;
    }
    private void ensureNotEnded() {
        if (this.isEnded) {
            throw new IllegalStateException("Copy pass has already been ended");
        }
    }

    public void upload(
            TransferBuffer src,
            Buffer dstBuffer,
            Cycle cycle
    ) {
        upload(src, 0, dstBuffer, cycle);
    }

    public void upload(
            TransferBuffer src,
            long srcOffset,
            Buffer dstBuffer,
            Cycle cycle
    ) {
        var size = dstBuffer.size();
        if (srcOffset + size > src.size()) {
            throw new IllegalArgumentException("Transfer buffer is too small for the destination buffer");
        }
        upload(
                src,
                srcOffset,
                dstBuffer,
                0,
                size,
                cycle
        );
    }

    public void upload(
            TransferBuffer src,
            Buffer dstBuffer,
            long size,
            Cycle cycle
    ) {
        upload(src, 0, dstBuffer, 0, size, cycle);
    }

    public void upload(
            TransferBuffer src,
            long srcOffset,
            Buffer dstBuffer,
            long dstOffset,
            long size,
            Cycle cycle
    ) {
        GPU.uploadToGPUBuffer(
            this,
            src,
            srcOffset,
            dstBuffer,
            dstOffset,
            size,
            cycle
        );
    }

    public void upload(
            TransferBuffer src,
            TextureRegion region,
            Cycle cycle
    ) {
        upload(src, 0, region, cycle);
    }

    public void upload(
            TransferBuffer src,
            long srcOffset,
            TextureRegion region,
            Cycle cycle
    ) {
        GPU.uploadToGPUTexture(
                this,
                src,
                srcOffset,
                region,
                cycle
        );
    }

    public void copy(
            Buffer source,
            long sourceOffset,
            Buffer destination,
            long destinationOffset,
            long size,
            Cycle cycle
    ) {
        GPU.copyBuffer(
                this,
                source,
                sourceOffset,
                destination,
                destinationOffset,
                size,
                cycle
        );
    }

    /// [CopyPass#copy] does transfer the memory directly. \
    /// [CommandBuffer#blit] will 'render' source onto destination,
    /// which allows for scaling and filtering.
    ///
    /// @see CommandBuffer#blit
    public void copy(
            TextureLocation source,
            TextureLocation destination,
            long width,
            long height,
            long depth,
            Cycle cycle
    ) {
        GPU.copyTexture(
                this,
                source,
                destination,
                width,
                height,
                depth,
                cycle
        );
    }

}
