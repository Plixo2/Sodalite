package io.github.plixo2.sodalite.resource;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PendingFrees {

    private static final List<GlobalResource> globalResources = Collections.synchronizedList(new ArrayList<>());
    private static final Set<Resource> openAutoResources = ConcurrentHashMap.newKeySet();
    private static final Queue<Resource> pendingAutoFrees = new ConcurrentLinkedQueue<>();

    static void addGlobal(ResourceObject owner, Resource resource) {
        globalResources.add(new GlobalResource(owner, resource));
    }

    static Runnable addAutoResource(Resource resource) {
        openAutoResources.add(resource);
        return () -> pendingAutoFrees.add(resource);
    }

    /// Called on application shutdown.
    /// Dont call this method directly
    public static void freeGlobal() {
        pendingAutoFrees.clear();

        openAutoResources.forEach(Resource::free);
        openAutoResources.clear();

        for (var i = globalResources.size() - 1; i >= 0; i--) {
            var globalResource = globalResources.get(i);
            globalResource.resource.free();
            globalResource.owner.markReleased();
        }

        globalResources.clear();
    }

    /// Drains the free list for the resources registered to the auto resource set.
    ///
    /// @threadSafety This should be called on the main thread.
    public static void drain() {
        Resource resource;
        while ((resource = pendingAutoFrees.poll()) != null) {
            openAutoResources.remove(resource);
            resource.free();
        }
    }

    private record GlobalResource(ResourceObject owner, Resource resource) { }

}
