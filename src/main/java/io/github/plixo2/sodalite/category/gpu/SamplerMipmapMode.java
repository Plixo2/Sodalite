package io.github.plixo2.sodalite.category.gpu;

/// @sdlAPI SDL_GPUSamplerMipmapMode
public enum SamplerMipmapMode {
    NEAREST,
    LINEAR,

    ;

    public int code() {
        return this.ordinal();
    }
}
