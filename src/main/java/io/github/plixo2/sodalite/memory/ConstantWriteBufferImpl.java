package io.github.plixo2.sodalite.memory;

import io.github.plixo2.sodalite.resource.ResourceSet;

import java.lang.foreign.MemorySegment;

import static io.github.plixo2.sodalite.Internal.isU32;

public abstract class ConstantWriteBufferImpl<Self extends ConstantWriteBufferImpl<Self>>
        extends WriteBuffer<Self>
{
    private final MemorySegment segment;

    /// @throws IllegalArgumentException if `capacity` is negative
    /// @throws IllegalArgumentException if `capacity` exceeds 2^32 bytes (4 GiB)
    ConstantWriteBufferImpl(
            ResourceSet resources,
            long capacity
    ) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be non-negative");
        }
        if (!isU32(capacity)) {
            throw new IllegalArgumentException("Buffer capacity exceeds maximum allowed size of 2^32 bytes (4 GiB)");
        }
        resources.register(this);
        this.capacity = capacity;
        this.segment = resources.allocate(capacity);
    }

    @Override
    protected MemorySegment ensureCapacity(long requiredCapacity) {
        if (requiredCapacity > this.capacity) {
            throw new IllegalStateException(
                    "Buffer capacity exceeded: required "
                    + requiredCapacity
                    + ", but capacity is "
                    + this.capacity
            );
        }
        return this.segment;
    }

    /// @return the full memory segment, not capped to `this.position`
    @Override
    public MemorySegment memory() {
        ensureNotReleased();
        return this.segment;
    }

}
