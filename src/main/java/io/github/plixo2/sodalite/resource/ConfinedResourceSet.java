package io.github.plixo2.sodalite.resource;

import org.jetbrains.annotations.Nullable;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/// @see ResourceSet#ofConfined() for more details on this class.
final class ConfinedResourceSet extends ResourceObject implements ResourceSet {

    private List<Resource> resources = new ArrayList<>();
    private List<ResourceObject> objects = new ArrayList<>();

    private boolean closed = false;
    private boolean closedFromParent = false;
    private @Nullable Arena arena;


    private ConfinedResourceSet() {
    }

    static ConfinedResourceSet create() {
        return new ConfinedResourceSet();
    }

    static ConfinedResourceSet create(ResourceSet parent) {
        var set = new ConfinedResourceSet();
        parent.register(set, set::releaseFromParent);
        return set;
    }

    @Override
    public void register(ResourceObject owner, Resource resource) {
        ensureAccess();
        this.objects.add(owner);
        this.resources.add(resource);
    }

    @Override
    public Arena arena() {
        ensureAccess();
        if (this.arena == null) {
            this.arena = Arena.ofConfined();
        }
        return this.arena;
    }


    @Override
    public void close() {
        if (this.closed) {
            throw new IllegalStateException("Already closed");
        } else if (this.closedFromParent) {
            throw new IllegalStateException("Already closed from parent");
        }
        this.closed = true;
        release();
    }

    private void releaseFromParent() {
        if (this.closedFromParent) {
            throw new IllegalStateException("Already closed");
        } else if (this.closed) {
            // was already regularly closed, just return
            return;
        }
        this.closedFromParent = true;
        release();
    }

    private void release() {
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
        this.objects.clear();
        this.resources = null;
        this.objects = null;
    }

    private void ensureAccess() {
        if (this.closed || this.closedFromParent) {
            throw new IllegalStateException("Already closed");
        }
    }
}
