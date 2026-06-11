package io.github.plixo2.sodalite.category.gpu;


import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.Getter;

import java.lang.foreign.MemorySegment;

/// @apiNote SDL_GPUSampler
public class Sampler extends ResourceObject implements SamplerInfo {

    private final MemorySegment segment;

    @Getter private final Filter minFilter;
    @Getter private final Filter magFilter;
    @Getter private final SamplerMipmapMode mipmapMode;
    @Getter private final SamplerAddressMode addressModeU;
    @Getter private final SamplerAddressMode addressModeV;
    @Getter private final SamplerAddressMode addressModeW;
    @Getter private final float mipLodBias;
    @Getter private final float maxAnisotropy;
    @Getter private final CompareOp compareOp;
    @Getter private final float minLod;
    @Getter private final float maxLod;
    @Getter private final boolean enableAnisotropy;
    @Getter private final boolean enableCompare;

    Sampler(
        ResourceSet resources,
        Device device,
        MemorySegment segment,
        SamplerInfo createInfo
    ) {
        resources.register(this, () -> GPU.releaseGPUSampler(device, segment));
        this.segment = segment;
        this.minFilter = createInfo.minFilter();
        this.magFilter = createInfo.magFilter();
        this.mipmapMode = createInfo.mipmapMode();
        this.addressModeU = createInfo.addressModeU();
        this.addressModeV = createInfo.addressModeV();
        this.addressModeW = createInfo.addressModeW();
        this.mipLodBias = createInfo.mipLodBias();
        this.maxAnisotropy = createInfo.maxAnisotropy();
        this.compareOp = createInfo.compareOp();
        this.minLod = createInfo.minLod();
        this.maxLod = createInfo.maxLod();
        this.enableAnisotropy = createInfo.enableAnisotropy();
        this.enableCompare = createInfo.enableCompare();
    }

    public SamplerBuilder newBuilder() {
        return SamplerBuilder.of(this);
    }

    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }

}
