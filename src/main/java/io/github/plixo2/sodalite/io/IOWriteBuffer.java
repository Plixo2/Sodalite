package io.github.plixo2.sodalite.io;

import io.github.plixo2.sodalite.memory.GrowableWriteBuffer;
import io.github.plixo2.sodalite.resource.ResourceSet;

import java.io.IOException;
import java.nio.file.Path;

public class IOWriteBuffer extends GrowableWriteBuffer implements AutoCloseable {

    private final Path path;
    private final ResourceSet resources;

    IOWriteBuffer(ResourceSet resources, Path path, long initialSize) {
        super(resources, initialSize);
        this.path = path;
        this.resources = resources;
    }

    @Override
    public void close() throws IOException {
        try {
            FileIO.write(this.path, this.memory());
        } finally {
            this.resources.close();
        }
    }
}
