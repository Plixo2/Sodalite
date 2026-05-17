package io.github.plixo2.sodalite.category.gpu;

/// @apiNote SDL_GPUCullMode
public enum CullMode {
    NONE,
    FRONT,
    BACK,

    ;

    public int code() {
        return this.ordinal();
    }
}
