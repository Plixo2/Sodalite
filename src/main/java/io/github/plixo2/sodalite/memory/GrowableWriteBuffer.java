package io.github.plixo2.sodalite.memory;

import io.github.plixo2.sodalite.resource.Resource;
import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.Getter;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;

import static io.github.plixo2.sodalite.Internal.*;

/// The buffer can only hold up to 2^32 - 1 bytes (4 GiB) of data
public class GrowableWriteBuffer extends WriteBuffer<GrowableWriteBuffer> {

    private final CurrentSegment currentSegment;

    @Getter
    private GrowthStrategy growthStrategy = new GrowthStrategy.Factor(2);

    GrowableWriteBuffer(
            ResourceSet resources,
            long initialSize
    ) {
        var segment = new CurrentSegment();
        resources.register(this, segment);
        if (initialSize < 0) {
           throw new IllegalArgumentException("Initial size must be non-negative");
        }
        checkCapacity(initialSize);

        this.currentSegment = segment;
        this.capacity = initialSize;
        if (initialSize > 0) {
            this.currentSegment.growTo(initialSize);
        }
    }

    /// @throws IllegalArgumentException if `initialSize` is negative
    /// @throws IllegalArgumentException if `initialSize` exceeds 2^32 - 1 bytes (4 GiB)
    public static GrowableWriteBuffer create(
            ResourceSet resources,
            long initialSize
    ) {
        return new GrowableWriteBuffer(resources, initialSize);
    }

    /// create a new `GrowableWriteBuffer` with an initial size of 64 bytes
    public static GrowableWriteBuffer create(
            ResourceSet resources
    ) {
        return new GrowableWriteBuffer(resources, 64);
    }

    /// create a new `GrowableWriteBuffer` with an initial size of 64 bytes and a growth strategy
    public static GrowableWriteBuffer create(
            ResourceSet resources,
            GrowthStrategy growthStrategy
    ) {
        return new GrowableWriteBuffer(resources, 64).growthStrategy(growthStrategy);
    }

    /// create a new `GrowableWriteBuffer` with an initial size of 64 bytes and a custom growth function
    public static GrowableWriteBuffer create(
            ResourceSet resources,
            GrowthStrategy.Custom customFunction
    ) {
        return new GrowableWriteBuffer(resources, 64).growthStrategy(customFunction);
    }

    /// create a new `GrowableWriteBuffer` that grows according to the given `layout` times `elementGrowCount`
    /// @param layout the memory layout of the elements to be stored in the buffer
    /// @param elementGrowCount the number of elements to grow by when the buffer is full
    public static GrowableWriteBuffer create(
            ResourceSet resources,
            MemoryLayout layout,
            long elementGrowCount
    ) {
        if (elementGrowCount <= 0) {
            throw new IllegalArgumentException("Element grow count must be at least 1");
        }
        var size = layout.byteSize() * elementGrowCount;
        var buffer = new GrowableWriteBuffer(resources, 0);
        buffer.growthStrategy(new GrowthStrategy.Constant(size));
        return buffer;
    }

    public GrowableWriteBuffer growthStrategy(GrowthStrategy growthStrategy) {
        this.growthStrategy = growthStrategy;
        return this;
    }
    public GrowableWriteBuffer growthStrategy(GrowthStrategy.Custom customFunction) {
        this.growthStrategy = customFunction;
        return this;
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
        if (requiredCapacity < 0) {
            throw new IllegalArgumentException("Required capacity must be non-negative");
        }
        if (requiredCapacity <= this.capacity) {
            return this.currentSegment.segment;
        }
        checkCapacity(requiredCapacity);
        long newCapacity;
        if (this.growthStrategy instanceof GrowthStrategy.Custom userFunction) {
            newCapacity = growByCustomFunction(userFunction, this.capacity, requiredCapacity);
        } else {
            newCapacity = this.growthStrategy.next(this.capacity, requiredCapacity);
        }

        this.currentSegment.growTo(newCapacity);
        this.capacity = newCapacity;
        return this.currentSegment.segment;
    }

    private static long growByCustomFunction(GrowthStrategy.Custom userFunction, long currentCapacity, long requiredCapacity) {
        var capacity = currentCapacity;
        while (capacity < requiredCapacity) {
            var previousCapacity = capacity;
            capacity = userFunction.next(capacity, requiredCapacity);
            if (capacity <= previousCapacity) {
                throw new IllegalStateException("Custom growth function did not grow the buffer");
            }
            checkCapacity(capacity);
        }

        return capacity;
    }

    private static void checkCapacity(long capacity) {
        if (!isU32(capacity)) {
            throw new IllegalStateException(OVERFLOW_MESSAGE);
        }
    }

    private final static class CurrentSegment implements Resource {
        private MemorySegment segment;
        private Arena arena;

        private void growTo(long newCapacity) {
            if (this.arena == null) {
                this.arena = Arena.ofShared();
                this.segment = this.arena.allocate(newCapacity);
                return;
            }

            var newArena = Arena.ofShared();
            var newSegment = newArena.allocate(newCapacity);
            newSegment.copyFrom(this.segment);
            this.segment = newSegment;
            this.arena.close();
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
