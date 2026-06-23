package io.github.plixo2.sodalite.category.gpu;


import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.Getter;

import java.lang.foreign.MemorySegment;
import java.util.Objects;

/// @sdlAPI SDL_GPUBuffer
public class Buffer extends ResourceObject {

    private final MemorySegment segment;
    @Getter
    private final long size;

    Buffer(
        ResourceSet resources,
        Device device,
        MemorySegment segment,
        long size
    ) {
        resources.register(this, () -> GPU.releaseBuffer(device, segment));
        this.size = size;
        this.segment = segment;
    }

    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }


    public void setName(Device device, String name) {
        GPU.setBufferName(device, this, name);
    }

    @Override
    public String toString() {
        return "Buffer{" +
                "segment=" + this.segment.address() +
                ", size=" + this.size +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.segment.address(), this.size);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Buffer other
                && this.segment.address() == other.segment.address()
                && this.size == other.size;
    }
}
