package io.github.plixo2.sodalite.resource;

import org.jetbrains.annotations.Nullable;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.List;

final class ConfinedResourceSet implements ResourceSet {
    private final List<Resource> resources = new ArrayList<>();
    private final List<ResourceObject> objects = new ArrayList<>();
    private final Thread owningThread;
    private boolean closed = false;
    private @Nullable Arena arena;

    private ConfinedResourceSet(Thread owningThread) {
        this.owningThread = owningThread;
    }

    static ConfinedResourceSet create(Thread owningThread) {
        return new ConfinedResourceSet(owningThread);
    }

    @Override
    public void register(ResourceObject owner, Resource resource) {
        ensureAccess();
        this.objects.add(owner);
        this.resources.add(resource);
    }

    @Override
    public MemorySegment allocate(long byteSize, long byteAlignment) {
        ensureAccess();
        if (this.arena == null) {
            this.arena = Arena.ofConfined();
        }
        return this.arena.allocate(byteSize, byteAlignment);
    }

    @Override
    public void close() {
        ensureAccess();
        this.closed = true;

        if (this.arena != null) {
            this.arena.close();
            this.arena = null;
        }

        for (var i = this.resources.size() - 1; i >= 0; i--) {
            var resource = this.resources.get(i);
            var object = this.objects.get(i);
            resource.free();
            object.markReleased();
        }
        this.resources.clear();
    }

    private void ensureAccess() {
        if (Thread.currentThread() != this.owningThread) {
            throw new WrongThreadException("Attempted access outside owning thread");
        }
        if (this.closed) {
            throw new IllegalStateException("Already closed");
        }
    }
}
