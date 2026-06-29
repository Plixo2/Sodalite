package io.github.plixo2.sodalite.category.gpu;


import io.github.plixo2.sodalite.SDLException;
import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

/// Use [#setArrayCount] for array textures (TEXTURE_2D_ARRAY and TEXTURE_CUBE_ARRAY) and
/// use [#setDepth] for 3D textures (TEXTURE_3D).
/// [#setArrayCount] will also multiply the count by 6 for cube maps, so the `layerCountOrDepth`
/// field will represent the actual number of layers for all texture types.
/// This builder also unsures that the `layerCountOrDepth` field is set to 6 for cube maps.
///
@Getter
@ToString
@EqualsAndHashCode
public final class TextureBuilder implements TextureInfo {

    private final TextureType type;
    private TextureFormat format;
    private @TextureUsageFlags int usage;
    private final int width;
    private final int height;
    private int layerCountOrDepth = 1;
    private int mipLevelCount = 1;
    private SampleCount sampleCount = SampleCount.COUNT_1;
    private @Nullable String name = null;

    private TextureBuilder(
            TextureType type,
            TextureFormat format,
            @TextureUsageFlags int usage,
            int width,
            int height
    ) {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("width and height must be at least 1");
        }

        this.type = type;
        this.format = format;
        this.width = width;
        this.height = height;
        this.usage = usage;
        if (this.type == TextureType.TEXTURE_CUBE) {
            this.layerCountOrDepth = 6;
        }
    }
    public static TextureBuilder of(
        Texture texture
    ) {
        return of((TextureInfo)texture);
    }

    public static TextureBuilder of(
        TextureInfo info
    ) {
        var builder = new TextureBuilder(
                info.type(),
                info.format(),
                info.usage(),
                info.width(),
                info.height()
        );
        builder.mipLevelCount = info.mipLevelCount();
        builder.sampleCount = info.sampleCount();
        builder.layerCountOrDepth = info.layerCountOrDepth();
        builder.name = info.name();
        return builder;
    }

    public static TextureBuilder of(
            TextureType type,
            TextureFormat format,
            @TextureUsageFlags int usage,
            int width,
            int height
    ) {
        return new TextureBuilder(
                type,
                format,
                usage,
                width,
                height
        );
    }

    public static TextureBuilder of2D(
            TextureFormat format,
            @TextureUsageFlags int usage,
            int width,
            int height
    ) {
        return new TextureBuilder(
                TextureType.TEXTURE_2D,
                format,
                usage,
                width,
                height
        );
    }
    public static TextureBuilder of3D(
            TextureFormat format,
            @TextureUsageFlags int usage,
            int width,
            int height,
            int depth
    ) {
        return new TextureBuilder(
                TextureType.TEXTURE_3D,
                format,
                usage,
                width,
                height
        ).setDepth(depth);
    }

    public TextureBuilder setUsage(@TextureUsageFlags int usage) {
        this.usage = usage;
        return this;
    }
    public TextureBuilder addUsage(@TextureUsageFlags int usage) {
        this.usage |= usage;
        return this;
    }
    public TextureBuilder setSampleCount(SampleCount sampleCount) {
        this.sampleCount = sampleCount;
        return this;
    }
    public TextureBuilder setTextureFormat(TextureFormat format) {
        this.format = format;
        return this;
    }
    public TextureBuilder setMipLevelCount(int mipLevelCount) {
        if (mipLevelCount < 1) {
            throw new IllegalArgumentException("mipLevelCount must be at least 1");
        }
        this.mipLevelCount = mipLevelCount;
        return this;
    }
    public TextureBuilder autoMipLevels() {
        this.mipLevelCount = calculateMipLevelCount(Math.max(this.width, this.height));
        return this;
    }
    public TextureBuilder setDepth(int depth) {
        if (!this.type.hasDepth()) {
            if (this.type.isArray()) {
                throw new IllegalStateException("Call `setLayerCount` for array textures instead of setDepth");
            }
            throw new IllegalStateException("Texture type " + this.type + " does not support depth (or layers)");
        }
        this.layerCountOrDepth = depth;
        return this;
    }

    public TextureBuilder setArrayCount(int count) {
        if (!this.type.isArray()) {
            if (this.type.hasDepth()) {
                throw new IllegalStateException("Call `setDepth` for depth textures instead of setLayerCount");
            }
            throw new IllegalStateException("Texture type " + this.type + " does not support layers (or depth)");
        }
        if (count < 1) {
            throw new IllegalArgumentException("layerCount must be at least 1");
        }
        if (this.type == TextureType.TEXTURE_CUBE_ARRAY) {
            this.layerCountOrDepth = count * 6;
        } else {
            this.layerCountOrDepth = count;
        }
        return this;
    }
    public TextureBuilder setName(@Nullable String name) {
        this.name = name;
        return this;
    }

    public Texture build(
            ResourceSet resources,
            Device device
    ) throws SDLException {
        return GPU.createTexture(resources, device, this);
    }

    /// @return the number of mip levels needed to reduce the given size to 2x2
    private static int calculateMipLevelCount(int size) {
        // Integer.SIZE - Integer.numberOfLeadingZeros(size >> 1);
        return Math.max((int) Math.floor(log2(size)), 1);
    }
    private static double log2(int x) {
        return Math.log(x) / Math.log(2);
    }

}
