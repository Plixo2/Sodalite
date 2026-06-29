package io.github.plixo2.sodalite.resource;


import java.lang.foreign.Arena;

/// @see ResourceSet#global() for more details on this class.
final class GlobalResourceSet implements ResourceSet {
    private static final Arena ARENA = Arena.global();

    private GlobalResourceSet() {

    }

    static GlobalResourceSet create() {
        return new GlobalResourceSet();
    }

    @Override
    public void register(ResourceObject owner, Resource resource) {
        FreeList.pushGlobal(owner, resource);
    }

    @Override
    public Arena arena() {
        return ARENA;
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Attempted to close a global resource set");
    }
}
