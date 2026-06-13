package io.github.plixo2.sodalite.category.gpu;


import org.libsdl.sdl.SDL_GPUViewport;

import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_GPUViewport
public record Viewport(
        float x,
        float y,
        float width,
        float height,
        float minDepth,
        float maxDepth
) {
    public static Viewport of(
            float x,
            float y,
            float width,
            float height,
            float minDepth,
            float maxDepth
    ) {
        return new Viewport(x, y, width, height, minDepth, maxDepth);
    }

    void put(MemorySegment segment) {
        SDL_GPUViewport.initialize(
                segment,
                this.x,
                this.y,
                this.width,
                this.height,
                this.minDepth,
                this.maxDepth
        );
    }
}
