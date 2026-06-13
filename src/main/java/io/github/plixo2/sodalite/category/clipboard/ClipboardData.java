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

}