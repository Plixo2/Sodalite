package io.github.plixo2.sodalite.category.video;


import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.joml.Vector2i;

import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_Window
public class Window extends ResourceObject {
    private final MemorySegment segment;

    Window(
            ResourceSet resources,
            MemorySegment segment
    ) {
        resources.register(this, () -> Video.destroyWindow(segment));
        this.segment = segment;
    }

    private Window(
            MemorySegment segment
    ) {
        this.segment = segment;
    }

    public static Window newUnchecked(MemorySegment windowPointer) {
        return new Window(windowPointer);
    }

    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }

    public WindowID id() {
        return Video.getWindowID(this);
    }


    public Vector2i getSize(Vector2i in) {
        return Video.getWindowSize(this, in);
    }

    public Vector2i getSizeInPixels(Vector2i in) {
        return Video.getWindowSizeInPixels(this, in);
    }

    public float getDisplayScale() {
        return Video.getWindowDisplayScale(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Window other && this.segment.address() == other.segment.address();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.segment.address());
    }

    @Override
    public String toString() {
        return "Window{" +
                "segment=" + this.segment.address() +
                '}';
    }
}
