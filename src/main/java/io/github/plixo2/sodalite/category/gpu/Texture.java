package io.github.plixo2.sodalite.category.gpu;

import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.Getter;

import java.lang.foreign.MemorySegment;

/// @apiNote SDL_GPUTexture
public class Texture extends ResourceObject {

    private final MemorySegment segment;
    @Getter
    private final int width;
    @Getter
    private final int height;
    @Getter
    private final int depth;

    /// for swapchain texture
    private Texture(
            MemorySegment segment,
            int width,
            int height,
            int depth
    ) {
        this.segment = segment;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    Texture(
            ResourceSet resources,
            Device device,
            MemorySegment segment,
            int width,
            int height,
            int depth
    ) {
        this(segment, width, height, depth);
        resources.register(this, () -> GPU.releaseGPUTexture(device, segment));
    }

    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }

    static Texture newSwapchainTexture(MemorySegment segment, int width, int height) {
        return new Texture(segment, width, height, 1);
    }

}
