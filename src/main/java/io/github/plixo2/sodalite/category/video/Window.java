package io.github.plixo2.sodalite.category.video;


import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.joml.Vector2i;

import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_Window
public class Window extends ResourceObject {
    private final MemorySegment segment;
    private final int id;

    Window(
            ResourceSet resources,
            MemorySegment segment
    ) {
        resources.register(this, () -> Video.destroyWindow(segment));
        this.segment = segment;
        this.id = Video.getWindowID(this);
    }

    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }

    public int id() {
        ensureNotReleased();
        return this.id;
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

}
