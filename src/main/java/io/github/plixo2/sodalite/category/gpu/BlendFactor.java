package io.github.plixo2.sodalite.category.gpu;

/// @apiNote SDL_GPUBlendFactor
public enum BlendFactor {
    INVALID,
    ZERO,
    ONE,
    SRC_COLOR,
    ONE_MINUS_SRC_COLOR,
    DST_COLOR,
    ONE_MINUS_DST_COLOR,
    SRC_ALPHA,
    ONE_MINUS_SRC_ALPHA,
    DST_ALPHA,
    ONE_MINUS_DST_ALPHA,
    CONSTANT_COLOR,
    ONE_MINUS_CONSTANT_COLOR,
    SRC_ALPHA_SATURATE,

    ;

    public int code() {
        return this.ordinal();
    }
}
