package io.github.plixo2.sodalite.category.gpu;


import lombok.Getter;

import java.lang.foreign.MemorySegment;

/// @apiNote SDL_GPUCopyPass
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
        if (this.isEnded) {
            throw new IllegalStateException("Copy pass has already been ended");
        }
        GPU.endGPUCopyPass(this);
        this.isEnded = true;
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

}
