package io.github.plixo2.sodalite.category.gpu;

import io.github.plixo2.sodalite.resource.ResourceSet;
import org.jetbrains.annotations.Nullable;
import org.libsdl.sdl.SDL_GPUTextureCreateInfo;

import java.lang.foreign.MemorySegment;

/// Consider using {@link TextureBuilder}
/// @sdlAPI SDL_GPUTextureCreateInfo
public record TextureCreateInfo(
        TextureType type,
        TextureFormat format,
        @TextureUsageFlags int usage,
        int width,
        int height,
        int layerCountOrDepth,
        int mipLevelCount,
        SampleCount sampleCount,
        @Nullable String name
) implements TextureInfo {

    public static TextureCreateInfo of(
            TextureInfo info
    ) {
        return new TextureCreateInfo(
                info.type(),
                info.format(),
                info.usage(),
                info.width(),
                info.height(),
                info.layerCountOrDepth(),
                info.mipLevelCount(),
                info.sampleCount(),
                info.name()
        );
    }

    public TextureBuilder toBuilder() {
        return TextureBuilder.of(this);
    }

    public Texture create(ResourceSet resources, Device device) {
        return GPU.createTexture(resources, device, this);
    }


    static void put(MemorySegment segment, TextureInfo info) {
        SDL_GPUTextureCreateInfo.initialize(
                segment,
                info.type().code(),
                info.format().code(),
                info.usage(),
                info.width(),
                info.height(),
                info.layerCountOrDepth(),
                info.mipLevelCount(),
                info.sampleCount().code(),
                0
        );
    }

}
