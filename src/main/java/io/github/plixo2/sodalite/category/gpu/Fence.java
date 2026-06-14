package io.github.plixo2.sodalite.category.gpu;

import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_GPUFence
public class Fence implements AutoCloseable {
    private final Device device;
    private final MemorySegment segment;

    private boolean released = false;

    Fence(
            Device device,
            MemorySegment segment
    ) {
        this.device = device;
        this.segment = segment;
    }

    public MemorySegment segment() {
        if (this.released) {
            throw new IllegalStateException("Fence has already been released");
        }
        return this.segment;
    }

    @Override
    public void close() {
        if (this.released) {
            throw new IllegalStateException("Fence has already been released");
        }
        GPU.releaseFence(this.device, this.segment);
        this.released = true;
    }

    public boolean query() {
        return GPU.queryFence(this.device, this);
    }

    public void await() {
        GPU.waitForFence(this.device, this);
    }

    public static void waitAll(
            Device device,
            Fence... fences
    ) {
        for (var fence : fences) {
            if (fence.device != device) {
                throw new IllegalArgumentException("All fences must belong to the same device");
            }
        }

        GPU.waitForFences(device, true, fences);
    }

    public static void waitAny(
            Device device,
            Fence... fences
    ) {
        for (var fence : fences) {
            if (fence.device != device) {
                throw new IllegalArgumentException("All fences must belong to the same device");
            }
        }

        GPU.waitForFences(device, false, fences);
    }


}
