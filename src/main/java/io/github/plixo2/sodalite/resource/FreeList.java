package io.github.plixo2.sodalite.resource;

import io.github.plixo2.sodalite.Internal;
import io.github.plixo2.sodalite.category.events.Events;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import io.github.plixo2.sodalite.category.init.Init;

public class FreeList {

    private static final List<GlobalResource> globalResources = Collections.synchronizedList(new ArrayList<>());
    private static final Set<Resource> openAutoResources = ConcurrentHashMap.newKeySet();
    private static final Queue<Resource> pendingAutoFrees = new ConcurrentLinkedQueue<>();


    /// Registers a global resource to be freed on shutdown
    static void pushGlobal(ResourceObject owner, Resource resource) {
        globalResources.add(new GlobalResource(owner, resource));
    }


    /// @return a Runnable to be passed to the cleaner, thus
    ///         the resource in enqueued to be released on the main thread.
    static Runnable pushAutoResource(Resource resource) {
        openAutoResources.add(resource);
        return () -> pendingAutoFrees.add(resource);
    }

    /// This function will free any global
    /// and non-collected gc-managed resources.
    ///
    /// Called on application shutdown by [Init#quit],
    /// dont call this method manually unless you know what you are doing.
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
    /// [Events#waitEvent]/[Events#pumpEvents]/[Events#pollEvent] and [Events#pollEvents]
    /// will call this method implicitly.
    ///
    /// @threadSafety This should be called on the main thread, like the Event methods.
    public static void drain() {
        Resource resource;
        while ((resource = pendingAutoFrees.poll()) != null) {
            openAutoResources.remove(resource);
            resource.free();
        }
    }

    private record GlobalResource(ResourceObject owner, Resource resource) { }

}
