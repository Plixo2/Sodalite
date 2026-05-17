package io.github.plixo2.sodalite.category.gpu;

/// @apiNote SDL_GPUColorTargetDescription
public record ColorTargetDescription(
        TextureFormat format,
        ColorTargetBlendState blendState
) {
    public static ColorTargetDescription of(
            TextureFormat format,
            ColorTargetBlendState blendState
    ) {
        return new ColorTargetDescription(format, blendState);
    }
}