package io.github.plixo2.sodalite.category.gpu;

import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_GPUTexture
@ToString(onlyExplicitlyIncluded = true, doNotUseGetters = true)
public non-sealed class Texture extends ResourceObject implements TextureInfo {

    private final MemorySegment segment;

    @Getter @ToString.Include private final TextureType type;
    @Getter @ToString.Include private final TextureFormat format;
    @Getter @ToString.Include private final @TextureUsageFlags int usage;
    @Getter @ToString.Include private final int width;
    @Getter @ToString.Include private final int height;
    @Getter @ToString.Include private final int layerCountOrDepth;
    @Getter @ToString.Include private final int mipLevelCount;
    @Getter @ToString.Include private final SampleCount sampleCount;
    @Getter @ToString.Include private @Nullable String name;

    /// for swapchain texture
    private Texture(
            MemorySegment segment,
            TextureType type,
            TextureFormat format,
            @TextureUsageFlags int usage,
            int width,
            int height,
            int layerCountOrDepth,
            int mipLevelCount,
            SampleCount sampleCount,
            @Nullable String name
    ) {
        this.segment = segment;
        this.type = type;
        this.format = format;
        this.usage = usage;
        this.width = width;
        this.height = height;
        this.layerCountOrDepth = layerCountOrDepth;
        this.mipLevelCount = mipLevelCount;
        this.sampleCount = sampleCount;
        this.name = name;
    }

    Texture(
            ResourceSet resources,
            Device device,
            MemorySegment segment,
            TextureInfo info
    ) {
        this(
                segment,
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
        resources.register(this, () -> GPU.releaseGPUTexture(device, segment));
    }


    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }

    /// Creates a new texture with the same parameters as this one
    public Texture createEmpty(ResourceSet resources, Device device) {
        return GPU.createTexture(resources, device, this);
    }

    public void setName(Device device, String name) {
        GPU.setTextureName(device, this, name);
        this.name = name;
    }

    public TextureBuilder newBuilder() {
        return TextureBuilder.of(this);
    }

    static Texture newSwapchainTexture(
            MemorySegment segment,
            int width,
            int height
    ) {
        return new Texture(
                segment,
                TextureType.TEXTURE_2D,
                TextureFormat.INVALID,
                TextureUsageFlags.COLOR_TARGET,
                width,
                height,
                1,
                1,
                SampleCount.COUNT_1,
                null
        ) {
            @Override
            public TextureFormat format() {
                throw new UnsupportedOperationException(
                        "Cannot get the format of a swapchain texture. "
                      + "Call Device.getSwapchainTextureFormat(window)"
                );
            }
        };
    }

    @ToString.Include(name = "segment", rank = 99)
    private long getSegment() {
        return this.segment.address();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.segment.address());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Texture other && this.segment.equals(other.segment);
    }
}
