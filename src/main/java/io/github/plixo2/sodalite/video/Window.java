package io.github.plixo2.sodalite.video;


import io.github.plixo2.sodalite.SDLResource;

import java.lang.foreign.MemorySegment;

public class Window extends SDLResource {
    final MemorySegment segment;
    Window(MemorySegment segment) {
        super(segment, Video::destroyWindow);
        this.segment = segment;
    }


    public int getWindowID() {
        return Video.getWindowID(this);
    }

}
