package io.github.plixo2.sodalite.category.video;


import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;

import java.lang.foreign.MemorySegment;


/// @sdlAPI SDL_GLContext
public class GLContextState extends ResourceObject {
    private final MemorySegment segment;

    GLContextState(
            ResourceSet resources,
            MemorySegment segment
    ) {
        resources.register(this, () -> Video.destroyGLContext(segment));
        this.segment = segment;
    }

    private GLContextState(MemorySegment segment) {
        this.segment = segment;
    }

    static GLContextState newUnchecked(MemorySegment segment) {
        return new GLContextState(segment);
    }

    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof GLContextState other && this.segment.address() == other.segment.address();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.segment.address());
    }

    @Override
    public String toString() {
        return "GLContextState{" +
                "segment=" + this.segment.address() +
                '}';
    }
}
