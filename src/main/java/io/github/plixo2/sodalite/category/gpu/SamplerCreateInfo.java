package io.github.plixo2.sodalite.category.gpu;

import io.github.plixo2.sodalite.resource.ResourceSet;
import org.libsdl.sdl.SDL_GPUSamplerCreateInfo;

import java.lang.foreign.MemorySegment;

/// Consider using {@link SamplerBuilder}
/// @apiNote SDL_GPUSamplerCreateInfo
public record SamplerCreateInfo(
        Filter minFilter,
        Filter magFilter,
        SamplerMipmapMode mipmapMode,
        SamplerAddressMode addressModeU,
        SamplerAddressMode addressModeV,
        SamplerAddressMode addressModeW,
        float mipLodBias,
        float maxAnisotropy,
        CompareOp compareOp,
        float minLod,
        float maxLod,
        boolean enableAnisotropy,
        boolean enableCompare
) implements SamplerInfo {

    public static SamplerCreateInfo of(SamplerInfo info) {
        return new SamplerCreateInfo(
                info.minFilter(),
                info.magFilter(),
                info.mipmapMode(),
                info.addressModeU(),
                info.addressModeV(),
                info.addressModeW(),
                info.mipLodBias(),
                info.maxAnisotropy(),
                info.compareOp(),
                info.minLod(),
                info.maxLod(),
                info.enableAnisotropy(),
                info.enableCompare()
        );
    }

    public SamplerBuilder toBuilder() {
        return SamplerBuilder.of(this);
    }

    public Sampler create(ResourceSet resources, Device device) {
        return GPU.createGPUSampler(resources, device, this);
    }

    static void put(MemorySegment segment, SamplerInfo info) {
        SDL_GPUSamplerCreateInfo.initialize(
            segment,
            info.minFilter().code(),
            info.magFilter().code(),
            info.mipmapMode().code(),
            info.addressModeU().code(),
            info.addressModeV().code(),
            info.addressModeW().code(),
            info.mipLodBias(),
            info.maxAnisotropy(),
            info.compareOp().code(),
            info.minLod(),
            info.maxLod(),
            info.enableAnisotropy(),
            info.enableCompare(),
            0
        );
    }
}
