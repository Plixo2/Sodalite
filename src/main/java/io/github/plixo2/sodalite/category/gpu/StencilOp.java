package io.github.plixo2.sodalite.category.gpu;

/// @sdlAPI SDL_GPUStencilOp
public enum StencilOp {
    INVALID,
    KEEP,
    ZERO,
    REPLACE,
    INCREMENT_AND_CLAMP,
    DECREMENT_AND_CLAMP,
    INVERT,
    INCREMENT_AND_WRAP,
    DECREMENT_AND_WRAP,

    ;

    public int code() {
        return this.ordinal();
    }
}
