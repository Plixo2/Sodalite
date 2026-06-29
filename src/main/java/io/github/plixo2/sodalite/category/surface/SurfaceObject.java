package io.github.plixo2.sodalite.category.surface;


import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;

import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_Surface
public class SurfaceObject extends ResourceObject {

    private final MemorySegment segment;

    protected SurfaceObject(
            ResourceSet resources,
            MemorySegment segment
    ) {
        this.segment = segment;
        throw new RuntimeException("Not implemented");
    }

    private SurfaceObject(MemorySegment segment) {
        this.segment = segment;
    }

    public static SurfaceObject newUnchecked(MemorySegment surfacePointer) {
        return new SurfaceObject(surfacePointer);
    }


    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SurfaceObject other && this.segment.address() == other.segment.address();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.segment.address());
    }

    @Override
    public String toString() {
        return "SurfaceObject{" +
                "segment=" + this.segment.address() +
                '}';
    }
}
