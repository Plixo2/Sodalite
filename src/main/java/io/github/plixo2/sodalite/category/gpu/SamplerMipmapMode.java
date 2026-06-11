package io.github.plixo2.sodalite.category.gpu;

/// @apiNote SDL_GPUSamplerMipmapMode
public enum SamplerMipmapMode {
    NEAREST,
    LINEAR,

    ;

    public int code() {
        return this.ordinal();
    }
}
