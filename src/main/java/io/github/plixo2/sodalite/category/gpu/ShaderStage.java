package io.github.plixo2.sodalite.category.gpu;

/// @apiNote SDL_GPUShaderStage
public enum ShaderStage {

    VERTEX,
    FRAGMENT,

    ;


    public int code() {
        return this.ordinal();
    }
}
