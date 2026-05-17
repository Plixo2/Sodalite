package io.github.plixo2.sodalite.category.gpu;


import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;

import java.lang.foreign.MemorySegment;

/// @apiNote SDL_GPUGraphicsPipeline
public class GraphicsPipeline extends ResourceObject {

    private final MemorySegment segment;

    GraphicsPipeline(
            ResourceSet resources,
            Device device,
            MemorySegment segment
    ) {
        resources.register(this, () -> GPU.releaseGPUGraphicsPipeline(device, segment));
        this.segment = segment;
    }


    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }

}
