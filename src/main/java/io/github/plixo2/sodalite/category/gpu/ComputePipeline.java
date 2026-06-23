package io.github.plixo2.sodalite.category.gpu;


import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;

import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_GPUComputePipeline
public class ComputePipeline extends ResourceObject {
    private final MemorySegment segment;

    ComputePipeline(
            ResourceSet resources,
            Device device,
            MemorySegment segment
    ) {
        resources.register(this, () -> GPU.releaseComputePipeline(device, segment));
        this.segment = segment;
    }

    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ComputePipeline other && this.segment.address() == other.segment.address();
    }

    @Override
    public String toString() {
        return "ComputePipeline{" +
                "segment=" + this.segment.address() +
                '}';
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.segment.address());
    }
}
