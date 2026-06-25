package io.github.plixo2.sodalite.memory;

import io.github.plixo2.sodalite.resource.ResourceSet;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;

/// The buffer can only hold up to 2^32 - 1 bytes (4 GiB) of data
/// @see CStruct
public class ConstantWriteBuffer extends ConstantWriteBufferImpl<ConstantWriteBuffer> {

    /// @throws IllegalArgumentException if `capacity` is negative
    /// @throws IllegalArgumentException if `capacity` exceeds 2^32 - 1 bytes (4 GiB)
    ConstantWriteBuffer(
            ResourceSet resources,
            long capacity
    ) {
        super(resources, capacity);
    }

    /// @throws IllegalArgumentException if the size of the segment exceeds 2^32 - 1 bytes (4 GiB)
    private ConstantWriteBuffer(MemorySegment segment) {
        super(segment);
    }

    /// @throws IllegalArgumentException if `capacity` exceeds 2^32 - 1 bytes (4 GiB)
    public static ConstantWriteBuffer allocate(ResourceSet resources, long capacity) {
        return new ConstantWriteBuffer(resources, capacity);
    }

    /// @throws IllegalArgumentException if `layout.byteSize() * count` exceeds 2^32 - 1 bytes (4 GiB)
    public static ConstantWriteBuffer allocate(ResourceSet resources, MemoryLayout layout, int count) {
        return new ConstantWriteBuffer(resources, layout.byteSize() * count);
    }

    /// @throws IllegalArgumentException if the size of the segment exceeds 2^32 - 1 bytes (4 GiB)
    public static ConstantWriteBuffer ofExisting(MemorySegment segment) {
        return new ConstantWriteBuffer(segment);
    }

}
