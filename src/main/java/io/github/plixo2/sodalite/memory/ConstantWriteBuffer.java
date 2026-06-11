package io.github.plixo2.sodalite.memory;

import io.github.plixo2.sodalite.resource.ResourceSet;

import java.lang.foreign.MemoryLayout;

public class ConstantWriteBuffer extends ConstantWriteBufferImpl<ConstantWriteBuffer> {

    ConstantWriteBuffer(
            ResourceSet resources,
            long capacity
    ) {
        super(resources, capacity);
    }

    public static ConstantWriteBuffer allocate(ResourceSet resources, long capacity) {
        return new ConstantWriteBuffer(resources, capacity);
    }
    public static ConstantWriteBuffer allocate(ResourceSet resources, MemoryLayout layout, int count) {
        return new ConstantWriteBuffer(resources, layout.byteSize() * count);
    }

}
