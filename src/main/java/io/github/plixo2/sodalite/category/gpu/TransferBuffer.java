package io.github.plixo2.sodalite.category.gpu;

import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.Getter;

import java.lang.foreign.MemorySegment;


public class TransferBuffer extends ResourceObject {

    private final MemorySegment segment;
    @Getter
    private final long size;
    private boolean isMapped = false;

    TransferBuffer(
        ResourceSet resources,
        Device device,
        MemorySegment segment,
        long size
    ) {
        resources.register(this, () -> GPU.releaseGPUTransferBuffer(device, segment));
        this.size = size;
        this.segment = segment;
    }

    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }

    TransferBuffer.Mapped mapTransferBuffer(
            Device device,
            Cycle cycle
    ) {
        if (this.isMapped) {
            throw new IllegalStateException("Transfer buffer is already mapped");
        }
        this.isMapped = true;
        var memory = GPU.mapGPUTransferBuffer(device, this, cycle);
        return new Mapped(device, memory);
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
            if (!TransferBuffer.this.isMapped) {
                throw new IllegalStateException("Already unmapped");
            }
            return this.mappedMemory;
        }

        @Override
        public void close() {
            if (!TransferBuffer.this.isMapped) {
                throw new IllegalStateException("Already unmapped");
            }
            TransferBuffer.this.isMapped = false;
            GPU.unmapGPUTransferBuffer(this.device, TransferBuffer.this);
        }
    }


}
