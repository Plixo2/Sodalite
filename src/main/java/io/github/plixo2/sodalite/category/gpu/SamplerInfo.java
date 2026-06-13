package io.github.plixo2.sodalite.category.gpu;


/// Call {@link SamplerCreateInfo#create} or {@link SamplerBuilder#build}
/// to create the sampler.
///
/// @see SamplerCreateInfo
/// @see SamplerBuilder
/// @see Sampler
public interface SamplerInfo {

    Filter minFilter();
    Filter magFilter();

    SamplerMipmapMode mipmapMode();
    SamplerAddressMode addressModeU();
    SamplerAddressMode addressModeV();
    SamplerAddressMode addressModeW();

    float mipLodBias();
    float maxAnisotropy();

    CompareOp compareOp(); // e.g. for shadow maps

    float minLod();
    float maxLod();

    boolean enableAnisotropy();
    boolean enableCompare();
}
