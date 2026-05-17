package io.github.plixo2.sodalite.resource;

import io.github.plixo2.sodalite.Internal;

public abstract class ResourceObject {
    private boolean released = false;

    public void ensureNotReleased() {
        if (!Internal.ASSERTIONS_ENABLED || !this.released) {
            return;
        }
        var className = this.getClass().getTypeName();
        throw new IllegalStateException(className + " has already been released");
    }

    public void markReleased() {
        if (Internal.ASSERTIONS_ENABLED && this.released) {
            var className = this.getClass().getTypeName();
            throw new IllegalStateException("Attempted to release " + className + " multiple times");
        }

        this.released = true;
    }
}
