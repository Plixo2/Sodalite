package io.github.plixo2.sodalite.category.gpu;

/// @apiNote SDL_GPUSwapchainComposition
public enum SwapchainComposition {

    SDR,
    SDR_LINEAR,
    HDR_EXTENDED_LINEAR,
    HDR10_ST2084,

    ;

    public int code() {
        return this.ordinal();
    }
}
