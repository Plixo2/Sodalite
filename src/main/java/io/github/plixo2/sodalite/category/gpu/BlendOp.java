package io.github.plixo2.sodalite.category.gpu;

/// @apiNote SDL_GPUBlendOp
public enum BlendOp {
    INVALID,
    ADD,
    SUBTRACT,
    REVERSE_SUBTRACT,
    MIN,
    MAX,

    ;

    public int code() {
        return this.ordinal();
    }
}
