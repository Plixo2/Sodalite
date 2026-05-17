package io.github.plixo2.sodalite.memory;

import io.github.plixo2.sodalite.resource.Resource;
import io.github.plixo2.sodalite.resource.ResourceSet;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public class GrowableWriteBuffer extends WriteBuffer {

    private final CurrentSegment currentSegment;
    
    private long capacity;

    GrowableWriteBuffer(
            ResourceSet resources,
            long initialSize
    ) {
        var segment = new CurrentSegment();
        resources.register(this, segment);
        if (initialSize < 0) {
           throw new IllegalArgumentException("Initial size must be non-negative");
        }

        this.currentSegment = segment;
        this.capacity = initialSize;
        this.size = 0;
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

    @Override
    public MemorySegment memory() {
        ensureNotReleased();
        return this.currentSegment.segment.asSlice(0, this.size).asReadOnly();
    }

    @Override
    public void clear() {
        ensureNotReleased();
        this.size = 0;
    }

    @Override
    public synchronized long capacity() {
        ensureNotReleased();
        return this.capacity;
    }

    @Override
    protected synchronized final void ensureCapacity(long requiredCapacity) {
        ensureNotReleased();
        if (requiredCapacity <= this.capacity) {
            return;
        }
        
        long newCapacity;
        do {
            newCapacity = Math.max(8, this.capacity * 2);
        } while (newCapacity < requiredCapacity);
        
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
