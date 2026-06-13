package io.github.plixo2.sodalite.category.gpu;

/// @sdlAPI SDL_GPUCubeMapFace
public enum CubeMapFace {
    POSITIVE_X,
    NEGATIVE_X,

    POSITIVE_Y,
    NEGATIVE_Y,

    POSITIVE_Z,
    NEGATIVE_Z,

    ;

    public int code() {
        return this.ordinal();
    }
}
