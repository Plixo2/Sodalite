package io.github.plixo2.sodalite.category.gpu;



import io.github.plixo2.sodalite.resource.DoubleReleaseException;
import io.github.plixo2.sodalite.resource.UseAfterReleaseException;

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
            throw new UseAfterReleaseException(this, "Pass already ended");
        }
        return this.segment;
    }

    @Override
    public void close() {
        if (this.isEnded) {
            throw new DoubleReleaseException(this, "Pass already ended");
        }
        GPU.endGPUCopyPass(this);
        this.isEnded = true;
    }
    public boolean hasEnded() {
        return this.isEnded;
    }

    public void upload(
            TransferBuffer source,
            Buffer destinationBuffer,
            Cycle cycle
    ) {
        upload(source, 0, destinationBuffer, cycle);
    }

    public void upload(
            TransferBuffer source,
            long sourceOffset,
            Buffer destinationBuffer,
            Cycle cycle
    ) {
        upload(
                source,
                sourceOffset,
                destinationBuffer,
                0,
                destinationBuffer.size(),
                cycle
        );
    }

    public void upload(
            TransferBuffer source,
            Buffer destinationBuffer,
            long size,
            Cycle cycle
    ) {
        upload(source, 0, destinationBuffer, 0, size, cycle);
    }

    public void upload(
            TransferBuffer source,
            long sourceOffset,
            Buffer destinationBuffer,
            long destinationOffset,
            long size,
            Cycle cycle
    ) {
        GPU.uploadToBuffer(
            this,
            source,
            sourceOffset,
            destinationBuffer,
            destinationOffset,
            size,
            cycle
        );
    }

    public void upload(
            TransferBuffer source,
            TextureRegion destination,
            Cycle cycle
    ) {
        upload(source, 0, destination, cycle);
    }

    public void upload(
            TransferBuffer source,
            long sourceOffset,
            TextureRegion destination,
            Cycle cycle
    ) {
        GPU.uploadToTexture(
                this,
                source,
                sourceOffset,
                destination,
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
    /// which allows scaling and filtering.
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


    public void download(
            TransferBuffer destination,
            Buffer sourceBuffer
    ) {
        download(destination, 0, sourceBuffer);
    }

    public void download(
            TransferBuffer destination,
            long destinationOffset,
            Buffer sourceBuffer
    ) {
        download(
                destination,
                destinationOffset,
                sourceBuffer,
                0,
                sourceBuffer.size()
        );
    }

    public void download(
            TransferBuffer destination,
            Buffer sourceBuffer,
            long size
    ) {
        download(destination, 0, sourceBuffer, 0, size);
    }

    public void download(
            TransferBuffer destination,
            long destinationOffset,
            Buffer sourceBuffer,
            long sourceOffset,
            long size
    ) {
        GPU.downloadFromBuffer(
                this,
                destination,
                destinationOffset,
                sourceBuffer,
                sourceOffset,
                size
        );
    }

    public void download(
            TransferBuffer destination,
            TextureRegion source
    ) {
        download(destination, 0, source);
    }

    public void download(
            TransferBuffer destination,
            long destinationOffset,
            TextureRegion source
    ) {
        GPU.downloadFromTexture(
                this,
                destination,
                destinationOffset,
                source
        );
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.segment.address());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CopyPass other && this.segment.address() == other.segment.address();
    }

    @Override
    public String toString() {
        return "CopyPass{" +
                "segment=" + this.segment.address() +
                '}';
    }
}
