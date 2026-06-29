package io.github.plixo2.sodalite.resource;


public abstract class ResourceObject {
    private boolean released = false;

    /// @throws UseAfterReleaseException if this object has been released
    public final void ensureNotReleased() {
        if (!this.released) {
            return;
        }
        throw new UseAfterReleaseException(this);
    }

    /// @throws DoubleReleaseException if this object has already been released before,
    final void markReleased() {
        if (this.released) {
            throw new DoubleReleaseException(this);
        }

        this.released = true;
    }
}
