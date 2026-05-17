package io.github.plixo2.sodalite.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FreeList {

    private static final Queue<Resource> globalResources = new ConcurrentLinkedQueue<>();
    private static final Queue<Resource> pendingFrees = new ConcurrentLinkedQueue<>();

    static void addGlobal(Resource resource) {
        globalResources.add(resource);
    }

    static Runnable add(Resource resource) {
        return () -> pendingFrees.add(resource);
    }

    public static void freeGlobals() {
        Resource resource;
        while ((resource = globalResources.poll()) != null) {
            resource.free();
        }
    }

    /// Drains the free list.
    /// @apiNote This should be called on the main thread.
    public static void drain() {
        Resource resource;
        while ((resource = pendingFrees.poll()) != null) {
            resource.free();
        }
    }

}
