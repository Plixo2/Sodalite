package io.github.plixo2.sodalite.resource;


public abstract class ResourceObject {
    private boolean released = false;

    /// @throws IllegalStateException if this object has been released
    public final void ensureNotReleased() {
        if (!this.released) {
            return;
        }
        var className = this.getClass().getTypeName();
        throw new IllegalStateException(className + " has already been released");
    }

    /// @throws IllegalStateException if this object has already been released before
    final void markReleased() {
        if (this.released) {
            var className = this.getClass().getTypeName();
            throw new IllegalStateException("Attempted to release " + className + " multiple times");
        }

        this.released = true;
    }
}
