package io.github.plixo2.sodalite.category.gpu;

import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;

import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_GPUFence
public class Fence extends ResourceObject {
    private final Device device;
    private final MemorySegment segment;

    Fence(
            ResourceSet resources,
            Device device,
            MemorySegment segment
    ) {
        resources.register(this, () ->  GPU.releaseFence(device, segment));
        this.device = device;
        this.segment = segment;
    }

    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }

    public boolean query() {
        return GPU.queryFence(this.device, this);
    }

    public void await() {
        GPU.waitForFence(this.device, this);
    }

    public static void waitAll(
            Fence... fences
    ) {
        if (fences.length == 0) {
            return;
        }
        var device = fences[0].device;

        for (var fence : fences) {
            if (fence.device != device) {
                throw new IllegalArgumentException("All fences must belong to the same device");
            }
        }

        GPU.waitForFences(device, true, fences);
    }

    public static void waitAny(
            Fence... fences
    ) {
        if (fences.length == 0) {
            return;
        }
        var device = fences[0].device;

        for (var fence : fences) {
            if (fence.device != device) {
                throw new IllegalArgumentException("All fences must belong to the same device");
            }
        }

        GPU.waitForFences(device, false, fences);
    }

    @Override
    public String toString() {
        return "Fence{" +
                "segment=" + this.segment.address() +
                '}';
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.segment.address());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Fence other && this.segment.address() == other.segment.address();
    }
}
