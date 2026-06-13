package io.github.plixo2.sodalite.category.gpu;

/// @sdlAPI SDL_GPUCompareOp
public enum CompareOp {
    INVALID,
    NEVER,
    LESS,
    EQUAL,
    LESS_OR_EQUAL,
    GREATER,
    NOT_EQUAL,
    GREATER_OR_EQUAL,
    ALWAYS,

    ;

    public int code() {
        return this.ordinal();
    }
}
