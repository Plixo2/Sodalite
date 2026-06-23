package io.github.plixo2.sodalite.category.gpu;

import com.google.errorprone.annotations.CheckReturnValue;
import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.Getter;

import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_GPUTransferBuffer
public class TransferBuffer extends ResourceObject {

    private final MemorySegment segment;
    private final MappedState mappedState;

    @Getter
    private final long size;

    TransferBuffer(
        ResourceSet resources,
        Device device,
        MemorySegment segment,
        long size
    ) {
        var mappedState = this.mappedState = new MappedState();
        resources.register(this, () -> {
            if (mappedState.isMapped) {
                throw new IllegalStateException("Cannot release transfer buffer while it is still mapped");
            }
            GPU.releaseTransferBuffer(device, segment);
        });
        this.size = size;
        this.segment = segment;
    }

    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }

    @CheckReturnValue
    public TransferBuffer.Mapped map(
            Device device,
            Cycle cycle
    ) {
        if (this.mappedState.isMapped) {
            throw new IllegalStateException("Transfer buffer is already mapped");
        }
        this.mappedState.isMapped = true;
        var memory = GPU.mapGPUTransferBuffer(device, this, cycle);
        return new Mapped(device, memory.reinterpret(this.size));
    }

    private static class MappedState {
        private boolean isMapped = false;
    }

    public class Mapped implements AutoCloseable {
        private final Device device;
        private final MemorySegment mappedMemory;

        Mapped(
            Device device,
            MemorySegment mappedMemory
        ) {
            this.device = device;
            this.mappedMemory = mappedMemory;
        }

        public MemorySegment memory() {
            if (!TransferBuffer.this.mappedState.isMapped) {
                throw new IllegalStateException("Already unmapped");
            }
            return this.mappedMemory;
        }

        @Override
        public void close() {
            if (!TransferBuffer.this.mappedState.isMapped) {
                throw new IllegalStateException("Already unmapped");
            }
            TransferBuffer.this.mappedState.isMapped = false;
            GPU.unmapTransferBuffer(this.device, TransferBuffer.this);
        }
    }

    @Override
    public String toString() {
        return "TransferBuffer{" +
                "segment=" + this.segment.address() +
                ", size=" + this.size +
                '}';
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.segment.address());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TransferBuffer other && this.segment.address() == other.segment.address();
    }
}
