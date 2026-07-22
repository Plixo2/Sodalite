package io.github.plixo2.sodalite.io;

import io.github.plixo2.sodalite.memory.ReadBuffer;
import io.github.plixo2.sodalite.resource.ResourceSet;

import java.lang.foreign.MemorySegment;

public class IOReadBuffer extends ReadBuffer implements AutoCloseable {

    private final ResourceSet resources;

    IOReadBuffer(ResourceSet resources, MemorySegment segment) {
        super(resources, segment);
        this.resources = resources;
    }

    @Override
    public void close() {
        this.resources.close();
    }
}
