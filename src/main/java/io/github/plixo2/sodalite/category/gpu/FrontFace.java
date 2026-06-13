package io.github.plixo2.sodalite.category.gpu;

/// @sdlAPI SDL_GPUFrontFace
public enum FrontFace {
    COUNTER_CLOCKWISE,
    CLOCKWISE,

    ;

    public static FrontFace defaultValue() {
        return COUNTER_CLOCKWISE;
    }

    public int code() {
        return this.ordinal();
    }
}
