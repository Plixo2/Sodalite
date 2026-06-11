package io.github.plixo2.sodalite.memory;

import io.github.plixo2.sodalite.Internal;
import io.github.plixo2.sodalite.resource.ResourceSet;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;

public abstract class ConstantWriteBufferImpl<Self extends ConstantWriteBufferImpl<Self>>
        extends WriteBuffer<Self>
{

    private final MemorySegment segment;

    ConstantWriteBufferImpl(
            ResourceSet resources,
            long capacity
    ) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be non-negative");
        }
        if (!Internal.isU32(capacity)) {
            throw new IllegalStateException("Buffer capacity exceeds maximum allowed size of 2^32 bytes (4 GiB)");
        }
        resources.register(this);
        this.capacity = capacity;
        this.segment = resources.allocate(capacity);
    }

    @Override
    protected void ensureCapacity(long requiredCapacity) {
        if (requiredCapacity > capacity()) {
            throw new IllegalStateException("Buffer capacity exceeded: required " + requiredCapacity + ", but capacity is " + capacity());
        }
    }

    @Override
    protected MemorySegment currentSegmentUnchecked() {
        return this.segment;
    }

    @Override
    public MemorySegment memory() {
        ensureNotReleased();
        return this.segment;
    }

}
