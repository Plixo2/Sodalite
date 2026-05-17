package io.github.plixo2.sodalite.category.gpu;


/// @apiNote SDL_GPULoadOp
public enum LoadOp {
    LOAD,
    CLEAR,
    DONT_CARE,

    ;

    public int code() {
        return this.ordinal();
    }
}
