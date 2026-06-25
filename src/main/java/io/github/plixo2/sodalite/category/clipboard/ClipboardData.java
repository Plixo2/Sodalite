package io.github.plixo2.sodalite.category.clipboard;


import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;

import java.lang.foreign.MemorySegment;


public class ClipboardData extends ResourceObject {

    private final MemorySegment segment;

    ClipboardData(ResourceSet resources, MemorySegment segment) {
        resources.register(this, () -> Clipboard.freeClipboardData(segment));
        this.segment = segment;
    }

    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClipboardData other && this.segment.address() == other.segment.address();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.segment.address());
    }

    @Override
    public String toString() {
        return "ClipboardData{" +
                "segment=" + this.segment.address() +
                '}';
    }
}