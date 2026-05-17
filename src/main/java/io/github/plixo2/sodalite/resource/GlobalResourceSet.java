package io.github.plixo2.sodalite.resource;


import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;


final class GlobalResourceSet implements ResourceSet {
    private static final Arena ARENA = Arena.global();

    private GlobalResourceSet() {

    }

    static GlobalResourceSet create() {
        return new GlobalResourceSet();
    }

    @Override
    public void register(ResourceObject owner, Resource resource) {
        FreeList.addGlobal(resource);
    }

    @Override
    public MemorySegment allocate(long byteSize, long byteAlignment) {
        return ARENA.allocate(byteSize, byteAlignment);
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Attempted to close a non-closeable resource set");
    }
}
