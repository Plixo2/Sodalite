package io.github.plixo2.sodalite;

import lombok.RequiredArgsConstructor;

import java.lang.ref.Cleaner;
import java.util.ArrayList;
import java.util.List;

public class SDLResourceManager {
    public static boolean ENABLED = true;
    private static final SDLResourceManager INSTANCE = new SDLResourceManager();

    private final List<SDLResource.Function> toClean = new ArrayList<>();
    private final Cleaner cleaner = Cleaner.create();


    public static Cleaner.Cleanable add(Object owner, SDLResource.Function resource) {
        if (ENABLED) {
            return INSTANCE.addResource(owner, resource);
        } else {
            // create a guard to avoid double free's, similar to how the cleaner would work
            @RequiredArgsConstructor
            class Guard implements Cleaner.Cleanable {
                private final SDLResource.Function resource;
                private boolean cleaned = false;
                @Override
                public void clean() {
                    if (!Guard.this.cleaned) {
                        Guard.this.cleaned = true;
                        Guard.this.resource.free();
                    }
                }
            }

            return new Guard(resource);
        }
    }

    /// should be called periodically on the render thread
    public static void cleanup() {
        if (!ENABLED) return;

       INSTANCE.clear();
    }


    private void clear() {
        var toClean = this.toClean;
        synchronized (toClean) {
            if (toClean.isEmpty()) {
                return;
            }
            var toRemove = toClean.removeLast();
            toRemove.free();
        }
    }

    private Cleaner.Cleanable addResource(Object owner, SDLResource.Function resource) {
        return this.cleaner.register(
            owner, () -> {
                this.scheduleDead(resource);
            }
        );
    }

    private void scheduleDead(SDLResource.Function resource) {
        var toClean = this.toClean;
        synchronized (toClean) {
            toClean.add(resource);
        }
    }


}
