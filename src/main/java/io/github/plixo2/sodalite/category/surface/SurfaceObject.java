package io.github.plixo2.sodalite.category.surface;


import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;

import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_Surface
public class SurfaceObject extends ResourceObject {

    private final MemorySegment segment;

    SurfaceObject(
            ResourceSet resources,
            MemorySegment segment
    ) {
        this.segment = segment;
        throw new RuntimeException("Not implemented");
    }

    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }

}
