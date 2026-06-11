package io.github.plixo2.sodalite.category.gpu;

import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SamplerBuilder implements SamplerInfo {

    private Filter minFilter;
    private Filter magFilter;
    private SamplerMipmapMode mipmapMode;
    private SamplerAddressMode addressModeU;
    private SamplerAddressMode addressModeV;
    private SamplerAddressMode addressModeW;
    private float mipLodBias;
    private float maxAnisotropy;
    private CompareOp compareOp;
    private float minLod;
    private float maxLod;
    private boolean enableAnisotropy;
    private boolean enableCompare;

    public static SamplerBuilder of(
            Filter filter,
            SamplerAddressMode addressMode,
            float maxLod
    ) {
        return new SamplerBuilder(
                filter,
                filter,
                SamplerMipmapMode.LINEAR,
                addressMode,
                addressMode,
                addressMode,
                0.0f,
                0.0f,
                CompareOp.INVALID,
                0.0f,
                maxLod,
                false,
                false
        );
    }

    public static SamplerBuilder of(SamplerInfo info) {
        return new SamplerBuilder(
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

    public SamplerBuilder setFilter(Filter filter) {
        this.minFilter = filter;
        this.magFilter = filter;
        return this;
    }
    public SamplerBuilder setMinFilter(Filter filter) {
        this.minFilter = filter;
        return this;
    }
    public SamplerBuilder setMagFilter(Filter filter) {
        this.magFilter = filter;
        return this;
    }
    public SamplerBuilder setMipmapMode(SamplerMipmapMode mipmapMode) {
        this.mipmapMode = mipmapMode;
        return this;
    }
    public SamplerBuilder setAddressMode(SamplerAddressMode addressMode) {
        this.addressModeU = addressMode;
        this.addressModeV = addressMode;
        this.addressModeW = addressMode;
        return this;
    }
    public SamplerBuilder setAddressModeU(SamplerAddressMode addressModeU) {
        this.addressModeU = addressModeU;
        return this;
    }
    public SamplerBuilder setAddressModeV(SamplerAddressMode addressModeV) {
        this.addressModeV = addressModeV;
        return this;
    }
    public SamplerBuilder setAddressModeW(SamplerAddressMode addressModeW) {
        this.addressModeW = addressModeW;
        return this;
    }
    public SamplerBuilder setMipLodBias(float mipLodBias) {
        this.mipLodBias = mipLodBias;
        return this;
    }
    public SamplerBuilder enableAnisotropy(float maxAnisotropy) {
        this.maxAnisotropy = maxAnisotropy;
        if (maxAnisotropy < 0) {
            throw new IllegalArgumentException("maxAnisotropy must be non-negative");
        }
        this.enableAnisotropy = maxAnisotropy > 0.0001;

        return this;
    }
    public SamplerBuilder disableAnisotropy() {
        this.maxAnisotropy = 0.0f;
        this.enableAnisotropy = false;
        return this;
    }
    public SamplerBuilder enableCompare(CompareOp compareOp) {
        this.compareOp = compareOp;
        this.enableCompare = true;
        return this;
    }
    public SamplerBuilder disableCompare() {
        this.compareOp = CompareOp.INVALID;
        this.enableCompare = false;
        return this;
    }
    public SamplerBuilder setLodRange(float minLod, float maxLod) {
        if (minLod < 0 || maxLod < 0) {
            throw new IllegalArgumentException("minLod and maxLod must be non-negative");
        }
        if (maxLod < minLod) {
            throw new IllegalArgumentException("maxLod must be greater than or equal to minLod");
        }
        this.minLod = minLod;
        this.maxLod = maxLod;
        return this;
    }
    public SamplerBuilder setMinLod(float minLod) {
        if (minLod < 0) {
            throw new IllegalArgumentException("minLod must be non-negative");
        }
        this.minLod = minLod;
        return this;
    }
    public SamplerBuilder setMaxLod(float maxLod) {
        if (maxLod < 0) {
            throw new IllegalArgumentException("maxLod must be non-negative");
        }
        this.maxLod = maxLod;
        return this;
    }



    public Sampler build(
            ResourceSet resources,
            Device device
    ) {
        return GPU.createGPUSampler(resources, device, this);
    }

}
