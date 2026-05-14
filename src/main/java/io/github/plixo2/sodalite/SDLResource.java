package io.github.plixo2.sodalite;

import java.lang.foreign.MemorySegment;
import java.lang.ref.Cleaner;
import java.util.function.Consumer;

public abstract class SDLResource {
    boolean destroyed = false;
    private final Cleaner.Cleanable cleanable;

    public SDLResource(Function freeFunction) {
        this.cleanable = SDLResourceManager.add(this, freeFunction);
    }

    public SDLResource(MemorySegment segment, Consumer<MemorySegment> freeFunction) {
        this.cleanable = SDLResourceManager.add(this, () -> freeFunction.accept(segment));
    }

    public final void destroy() {
        this.destroyed = true;
        this.cleanable.clean();
    }


    public interface Function {
        void free();
    }

}
