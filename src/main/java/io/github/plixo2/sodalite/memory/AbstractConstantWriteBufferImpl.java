package io.github.plixo2.sodalite.memory;

import io.github.plixo2.sodalite.resource.ResourceSet;

import java.lang.foreign.MemorySegment;

import static io.github.plixo2.sodalite.Internal.isU32;


public sealed abstract class AbstractConstantWriteBufferImpl<Self extends AbstractConstantWriteBufferImpl<Self>>
        extends
            WriteBuffer<Self>
        permits
            ConstantWriteBuffer,
            CStruct
{

    private final MemorySegment segment;

    /// @throws IllegalArgumentException if `capacity` is negative
    /// @throws IllegalArgumentException if `capacity` exceeds 2^32 - 1 bytes (4 GiB)
    protected AbstractConstantWriteBufferImpl(
            ResourceSet resources,
            long capacity
    ) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be non-negative");
        }
        if (!isU32(capacity)) {
            throw new IllegalArgumentException(OVERFLOW_MESSAGE);
        }
        resources.register(this);
        this.capacity = capacity;
        this.segment = resources.allocate(capacity);
    }

    /// @throws IllegalArgumentException if the size of the segment exceeds 2^32 - 1 bytes (4 GiB)
    protected AbstractConstantWriteBufferImpl(
            MemorySegment segment
    ) {
        var capacity = segment.byteSize();
        if (!isU32(capacity)) {
            throw new IllegalArgumentException(OVERFLOW_MESSAGE);
        }
        this.capacity = capacity;
        this.segment = segment;
    }

    @Override
    protected final MemorySegment ensureCapacity(long requiredCapacity) {
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
    public final MemorySegment memory() {
        ensureNotReleased();
        return this.segment;
    }

}
