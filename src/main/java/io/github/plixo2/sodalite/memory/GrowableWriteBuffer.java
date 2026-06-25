package io.github.plixo2.sodalite.memory;

import io.github.plixo2.sodalite.resource.Resource;
import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.Getter;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static io.github.plixo2.sodalite.Internal.isU32;

public class GrowableWriteBuffer extends WriteBuffer<GrowableWriteBuffer> {

    private final CurrentSegment currentSegment;

    private double growthFactor = 2d;

    GrowableWriteBuffer(
            ResourceSet resources,
            long initialSize
    ) {
        var segment = new CurrentSegment();
        resources.register(this, segment);
        if (initialSize < 0) {
           throw new IllegalArgumentException("Initial size must be non-negative");
        }
        if (!isU32(initialSize)) {
            throw new IllegalArgumentException("Buffer capacity exceeds maximum allowed size of 2^32 bytes (4 GiB)");
        }

        this.currentSegment = segment;
        this.capacity = initialSize;
        if (initialSize > 0) {
            this.currentSegment.grow(initialSize);
        }
    }

    /// @throws IllegalArgumentException if `initialSize` is negative
    /// @throws IllegalArgumentException if `initialSize` exceeds 2^32 bytes (4 GiB)
    public static GrowableWriteBuffer create(ResourceSet resources, long initialSize) {
        return new GrowableWriteBuffer(resources, initialSize);
    }
    /// @throws IllegalArgumentException if `initialSize` is negative
    /// @throws IllegalArgumentException if `initialSize` exceeds 2^32 bytes (4 GiB)
    public static GrowableWriteBuffer create(ResourceSet resources) {
        return new GrowableWriteBuffer(resources, 0);
    }

    /// Sets the growth factor for the buffer.
    /// The growth factor determines how much the buffer will grow when it needs to expand.
    ///
    /// - `factor <= 1` means the buffer will grow only by the required amount.
    /// - `factor > 1` means the buffer will grow by n times, until the required capacity is met.
    ///
    /// E.g. the default factor of 2 means the buffer will double in size.
    ///
    /// @throws IllegalArgumentException if `factor` is less than 0 or greater than 255
    public void setGrowthFactor(double factor) {
        if (factor < 0) {
            throw new IllegalArgumentException("Growth factor must be at least 0");
        } else if (factor > 255) {
             throw new IllegalArgumentException("Growth factor must be at most 255");
        }
        this.growthFactor = factor;
    }

    /// The growth factor determines how much the buffer will grow when it needs to expand.
    ///
    /// - `factor <= 1` means the buffer will grow only by the required amount.
    /// - `factor > 1` means the buffer will grow by n times, until the required capacity is met.
    ///
    /// E.g. the default factor of 2 means the buffer will double in size.
    ///
    /// @return the current growth factor, defaults to 2
    public double growthFactor() {
        return this.growthFactor;
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
    protected final MemorySegment ensureCapacity(long requiredCapacity) {
        if (requiredCapacity <= this.capacity) {
            return this.currentSegment.segment;
        }

        long newCapacity;
        if (this.growthFactor <= 1.001) { // small epsilon to account for floating point errors
            newCapacity = requiredCapacity;
        } else {
            newCapacity = Math.max(8, this.capacity);
            do {
                newCapacity = (long) Math.ceil(newCapacity * this.growthFactor);
            } while (newCapacity < requiredCapacity);
        }

        if (!isU32(newCapacity)) {
            throw new IllegalStateException("Buffer capacity exceeds maximum allowed size of 2^32 bytes (4 GiB)");
        }
        
        this.currentSegment.grow(newCapacity);
        this.capacity = newCapacity;
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
