package io.github.plixo2.sodalite.category.gpu;


/// @apiNote SDL_GPUSamplerAddressMode
public enum SamplerAddressMode {
    REPEAT,
    MIRRORED_REPEAT,
    CLAMP_TO_EDGE,

    ;

    public int code() {
        return this.ordinal();
    }
}
