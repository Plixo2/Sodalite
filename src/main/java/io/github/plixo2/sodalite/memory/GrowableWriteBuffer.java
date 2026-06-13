package io.github.plixo2.sodalite.memory;

import io.github.plixo2.sodalite.Internal;
import io.github.plixo2.sodalite.resource.Resource;
import io.github.plixo2.sodalite.resource.ResourceSet;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static io.github.plixo2.sodalite.Internal.*;

public class GrowableWriteBuffer extends WriteBuffer<GrowableWriteBuffer> {

    private final CurrentSegment currentSegment;

    GrowableWriteBuffer(
            ResourceSet resources,
            long initialSize
    ) {
        var segment = new CurrentSegment();
        resources.register(this, segment);
        if (initialSize < 0) {
           throw new IllegalArgumentException("Initial size must be non-negative");
        }
        if (!Internal.isU32(initialSize)) {
            throw new IllegalStateException("Buffer capacity exceeds maximum allowed size of 2^32 bytes (4 GiB)");
        }

        this.currentSegment = segment;
        this.capacity = initialSize;
        if (initialSize > 0) {
            this.currentSegment.grow(initialSize);
        }
    }

    public static GrowableWriteBuffer create(ResourceSet resources, long initialSize) {
        return new GrowableWriteBuffer(resources, initialSize);
    }
    public static GrowableWriteBuffer create(ResourceSet resources) {
        return new GrowableWriteBuffer(resources, 0);
    }

    /// @return the memory segment capped to `this.position`
    @Override
    public MemorySegment memory() {
        ensureNotReleased();
        var segment = this.currentSegment.segment;
        if (this.position != this.capacity) {
            segment = segment.asSlice(0, this.position);
        }
        return segment;
    }

    @Override
    protected final void ensureCapacity(long requiredCapacity) {
        if (requiredCapacity <= this.capacity) {
            return;
        }
        
        long newCapacity;
        do {
            newCapacity = Math.max(8, this.capacity * 2);
        } while (newCapacity < requiredCapacity);

        if (!Internal.isU32(newCapacity)) {
            throw new IllegalStateException("Buffer capacity exceeds maximum allowed size of 2^32 bytes (4 GiB)");
        }
        
        this.currentSegment.grow(newCapacity);
        this.capacity = newCapacity;
    }

    @Override
    protected MemorySegment currentSegmentUnchecked() {
        return this.currentSegment.segment;
    }

    private final static class CurrentSegment implements Resource {
        private MemorySegment segment;
        private Arena arena;

        private void grow(long newCapacity) {
            if (this.arena == null) {
                this.arena = Arena.ofShared();
                this.segment = this.arena.allocate(newCapacity);
                return;
            }

            var newArena = Arena.ofShared();
            var newSegment = newArena.allocate(newCapacity);
            newSegment.copyFrom(this.segment);
            this.arena.close();
            this.segment = newSegment;
            this.arena = newArena;
        }

        @Override
        public void free() {
            if (this.arena != null) {
                this.arena.close();
            }
        }
    }

}
