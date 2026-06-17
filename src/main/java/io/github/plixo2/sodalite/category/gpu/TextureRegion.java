package io.github.plixo2.sodalite.category.gpu;

import org.libsdl.sdl.SDL_GPUTextureRegion;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static io.github.plixo2.sodalite.Internal.*;

/// @sdlAPI SDL_GPUTextureRegion
public record TextureRegion(
        Texture texture,
        long mipLevel,
        long layer,
        long x,
        long y,
        long z,
        long width,
        long height,
        long depth
) {
    public TextureRegion {
        assertU32(mipLevel, "mipLevel");
        assertU32(layer, "layer");
        assertU32(x, "x");
        assertU32(y, "y");
        assertU32(z, "z");
        assertU32(width, "width");
        assertU32(height, "height");
        assertU32(depth, "depth");
    }

    public static TextureRegion of(
            Texture texture,
            long mipLevel,
            long layer,
            long x,
            long y,
            long z,
            long width,
            long height,
            long depth
    ) {
        return new TextureRegion(texture, mipLevel, layer, x, y, z, width, height, depth);
    }

    public static TextureRegion of2D(
            Texture texture,
            long mipLevel,
            long x,
            long y,
            long width,
            long height
    ) {
        ensureTexture(texture, TextureType.TEXTURE_2D, mipLevel);
        return new TextureRegion(texture, mipLevel, 0, x, y, 0, width, height, 1);
    }

    public static TextureRegion of2DArray(
            Texture texture,
            long mipLevel,
            long index,
            long x,
            long y,
            long width,
            long height
    ) {
        ensureTexture(texture, TextureType.TEXTURE_2D_ARRAY, mipLevel);

        if (index >= texture.layerCountOrDepth()) {
            throw new IllegalArgumentException(
                    "The index must be less than the number of array layers in the texture"
            );
        }

        return new TextureRegion(texture, mipLevel, index, x, y, 0, width, height, 1);
    }

    public static TextureRegion of3D(
            Texture texture,
            long mipLevel,
            long x,
            long y,
            long z,
            long width,
            long height,
            long depth
    ) {
        ensureTexture(texture, TextureType.TEXTURE_3D, mipLevel);

        return new TextureRegion(texture, mipLevel, 0, x, y, z, width, height, depth);
    }

    public static TextureRegion ofCube(
            Texture texture,
            long mipLevel,
            CubeMapFace face,
            long x,
            long y,
            long width,
            long height
    ) {
        ensureTexture(texture, TextureType.TEXTURE_CUBE, mipLevel);
        return new TextureRegion(texture, mipLevel, face.code(), x, y, 0, width, height, 1);
    }

    public static TextureRegion ofCubeArray(
            Texture texture,
            long mipLevel,
            long index,
            CubeMapFace face,
            long x,
            long y,
            long width,
            long height
    ) {
        ensureTexture(texture, TextureType.TEXTURE_CUBE_ARRAY, mipLevel);

        var correctLayer = cubeArrayIndex(texture, index, face);

        return new TextureRegion(texture, mipLevel, correctLayer, x, y, 0, width, height, 1);
    }

    public static TextureRegion ofFull2D(
            Texture texture
    ) {
        return ofFull2D(texture, 0);
    }

    public static TextureRegion ofFull2D(
            Texture texture,
            long mipLevel
    ) {
        ensureTexture(texture, TextureType.TEXTURE_2D, mipLevel);
        return TextureRegion.of(
                texture,
                mipLevel,
                0,
                0, 0,
                0,
                texture.width() >> mipLevel,
                texture.height() >> mipLevel,
                1
        );
    }

    public static TextureRegion ofFull2DArray(
            Texture texture,
            long index
    ) {
        return ofFull2DArray(texture, 0, index);
    }

    public static TextureRegion ofFull2DArray(
            Texture texture,
            long mipLevel,
            long index
    ) {
        ensureTexture(texture, TextureType.TEXTURE_2D_ARRAY, mipLevel);

        if (index >= texture.layerCountOrDepth()) {
            throw new IllegalArgumentException(
                    "The index must be less than the number of array layers in the texture"
            );
        }

        return TextureRegion.of(
                texture,
                mipLevel,
                index,
                0, 0,
                0,
                texture.width() >> mipLevel,
                texture.height() >> mipLevel,
                1
        );
    }

    public static TextureRegion ofFull3D(
            Texture texture
    ) {
        return ofFull3D(texture, 0);
    }

    public static TextureRegion ofFull3D(
            Texture texture,
            long mipLevel
    ) {
        ensureTexture(texture, TextureType.TEXTURE_3D, mipLevel);
        return TextureRegion.of(
                texture,
                mipLevel,
                0,
                0, 0, 0,
                texture.width() >> mipLevel,
                texture.height() >> mipLevel,
                texture.layerCountOrDepth() >> mipLevel
        );
    }

    public static TextureRegion ofFullCube(
            Texture texture,
            CubeMapFace face
    ) {
        return ofFullCube(texture, 0, face);
    }

    public static TextureRegion ofFullCube(
            Texture texture,
            long mipLevel,
            CubeMapFace face
    ) {
        ensureTexture(texture, TextureType.TEXTURE_CUBE, mipLevel);
        return TextureRegion.of(
                texture,
                mipLevel,
                face.code(),
                0, 0,
                0,
                texture.width() >> mipLevel,
                texture.height() >> mipLevel,
                1
        );
    }

    public static TextureRegion ofFullCubeArray(
            Texture texture,
            long index,
            CubeMapFace face
    ) {
        return ofFullCubeArray(texture, 0, index, face);
    }

    public static TextureRegion ofFullCubeArray(
            Texture texture,
            long mipLevel,
            long index,
            CubeMapFace face
    ) {
        ensureTexture(texture, TextureType.TEXTURE_CUBE_ARRAY, mipLevel);

        var correctLayer = cubeArrayIndex(texture, index, face);

        return TextureRegion.of(
                texture,
                mipLevel,
                correctLayer,
                0, 0,
                0,
                texture.width() >> mipLevel,
                texture.height() >> mipLevel,
                1
        );
    }

    MemorySegment create(Arena arena) {
        return SDL_GPUTextureRegion.create(
                arena,
                this.texture.segment(),
                (int) this.mipLevel,
                (int) this.layer,
                (int) this.x,
                (int) this.y,
                (int) this.z,
                (int) this.width,
                (int) this.height,
                (int) this.depth
        );
    }

    private static void ensureTexture(Texture texture, TextureType type, long mipLevel) {
        if (texture.type() != type) {
            throw new IllegalArgumentException(
                    "The type of the texture must be " + type + " to use this method"
            );
        }
        ensureMipLevel(texture, mipLevel);
    }
    private static void ensureMipLevel(Texture texture, long mipLevel) {
        if (mipLevel >= texture.mipLevelCount()) {
            throw new IllegalArgumentException(
                    "The mip level must be less than the number of mip levels in the texture"
            );
        }
    }

    static long cubeArrayIndex(Texture texture, long index, CubeMapFace face) {
        var correctLayer = index * 6 + face.code();
        var layerCount = texture.layerCountOrDepth();
        if (correctLayer >= layerCount) {
            var hasCorrectLayerCount = layerCount % 6 == 0;

            var suffix = hasCorrectLayerCount
                    ? " (" + (layerCount / 6) + " cube maps)"
                    : ". Note that the texture has a layer count that is not a multiple of 6"
                      + ", which is required for cube map arrays";

            throw new IllegalArgumentException(
                    "The index must be less than the number of layers in the texture."
                    + "Calculated layer "
                    + correctLayer
                    + " for index "
                    + index
                    + " and face "
                    + face
                    + ", but the texture has only "
                    + layerCount
                    + " layers"
                    + suffix
            );
        }
        return correctLayer;
    }

}
