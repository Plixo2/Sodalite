package io.github.plixo2.sodalite.category.gpu;

/// @apiNote SDL_GPUPresentMode
public enum PresentMode {

    VSYNC,
    IMMEDIATE,
    MAILBOX,

    ;

    public int code() {
        return this.ordinal();
    }
}
