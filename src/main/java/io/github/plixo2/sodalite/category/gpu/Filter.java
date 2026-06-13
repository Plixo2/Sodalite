package io.github.plixo2.sodalite.category.gpu;

/// @sdlAPI SDL_GPUFilter
public enum Filter {
    NEAREST,
    LINEAR,

    ;

    public int code() {
        return this.ordinal();
    }
}
