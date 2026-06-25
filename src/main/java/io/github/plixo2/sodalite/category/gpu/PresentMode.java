package io.github.plixo2.sodalite.category.gpu;

/// @sdlAPI SDL_GPUPresentMode
public enum PresentMode {

    VSYNC,          //  Wait for vsync (default)
    IMMEDIATE,      //  No synchronization, max frame rate
    MAILBOX,        //  Similar to VSYNC, but with reduced visual latency

    ;

    public int code() {
        return this.ordinal();
    }
}
