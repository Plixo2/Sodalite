package io.github.plixo2.sodalite.resource;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.ref.Cleaner;

final class AutoResourceSet implements ResourceSet {
    private final Cleaner cleaner;

    private AutoResourceSet(
            Cleaner cleaner
    ) {
        this.cleaner = cleaner;
    }

    static AutoResourceSet create(Cleaner cleaner) {
        return new AutoResourceSet(cleaner);
    }

    @Override
    public void register(ResourceObject owner, Resource resource) {
        this.cleaner.register(owner, FreeList.add(resource));
    }

    @Override
    public MemorySegment allocate(long byteSize, long byteAlignment) {
        return Arena.ofAuto().allocate(byteSize, byteAlignment);
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Attempted to close a non-closeable resource set");
    }
}
