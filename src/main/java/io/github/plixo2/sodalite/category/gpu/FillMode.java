package io.github.plixo2.sodalite.category.gpu;

/// @sdlAPI SDL_GPUFillMode
public enum FillMode {
    FILL,
    LINE,

    ;

    public int code() {
        return this.ordinal();
    }
}
