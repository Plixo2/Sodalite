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

}
