package io.github.plixo2.sodalite.resource;

import java.lang.foreign.Arena;
import java.lang.ref.Cleaner;

/// @see ResourceSet#ofAuto() for more details on this class.
final class AutoResourceSet implements ResourceSet {
    private static final Cleaner CLEANER = Cleaner.create();

    static AutoResourceSet create() {
        return new AutoResourceSet();
    }

    @Override
    public void register(ResourceObject owner, Resource resource) {
        CLEANER.register(owner, PendingFrees.pushAutoResource(resource));
    }

    @Override
    public Arena arena() {
        return Arena.ofAuto();
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Attempted to close a non-closeable resource set");
    }
}
