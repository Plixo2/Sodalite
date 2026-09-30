package io.github.plixo2.sodalite.resource;

import org.jetbrains.annotations.Nullable;

import java.lang.foreign.Arena;
import java.util.ArrayList;
import java.util.List;

/// @see ResourceSet#ofConfined() for more details on this class.
final class SharedConfinedResourceSet extends ResourceObject implements ResourceSet {

    private List<Resource> resources = new ArrayList<>();
    private List<ResourceObject> objects = new ArrayList<>();

    private boolean closed = false;
    private boolean closedFromParent = false;
    private @Nullable Arena arena;


    private SharedConfinedResourceSet() {

    }

    static SharedConfinedResourceSet create() {
        return new SharedConfinedResourceSet();
    }

    static SharedConfinedResourceSet create(ResourceSet parent) {
        if (parent instanceof AutoResourceSet) {
            throw new IllegalArgumentException("Cannot create a confined resource set from an auto resource set");
        }
        var set = new SharedConfinedResourceSet();
        parent.register(set, set::releaseFromParent);
        return set;
    }

    @Override
    public synchronized void register(ResourceObject owner, Resource resource) {
        ensureAccess();
        this.objects.add(owner);
        this.resources.add(resource);
    }

    @Override
    public synchronized Arena arena() {
        ensureAccess();
        if (this.arena == null) {
            this.arena = Arena.ofShared();
        }
        return this.arena;
    }


    @Override
    public synchronized void close() {
        if (this.closed) {
            throw new DoubleReleaseException(this, "Already closed");
        } else if (this.closedFromParent) {
            throw new DoubleReleaseException(this, "Already closed (from parent)");
        }
        this.closed = true;
        release();
    }

    private synchronized void releaseFromParent() {

        if (this.closedFromParent) {
            throw new DoubleReleaseException(this, "Already closed (from parent)");
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
            throw new UseAfterReleaseException(this, "Already closed");
        }
    }
}
