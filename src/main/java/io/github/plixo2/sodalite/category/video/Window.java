package io.github.plixo2.sodalite.category.video;



import io.github.plixo2.sodalite.category.gpu.Device;
import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_Window
public class Window extends ResourceObject {
    private final MemorySegment segment;
    private final int id;

    private final GPUClaim gpuClaim;

    Window(
            ResourceSet resources,
            MemorySegment segment
    ) {
        var claim = this.gpuClaim = new GPUClaim();

        resources.register(this, () -> {
            Video.destroyWindow(claim, segment);
        });
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

    void setClaimedGPU(boolean claimed) {
        ensureNotReleased();
        this.gpuClaim.claimed = claimed;
    }

    public boolean isClaimedbyGPU() {
        ensureNotReleased();
        return this.gpuClaim.claimed;
    }

    static class GPUClaim {
        boolean claimed = false;
    }
}
