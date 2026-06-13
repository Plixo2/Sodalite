package io.github.plixo2.sodalite.category.gpu;

import org.libsdl.sdl.SDL_GPUTextureLocation;

import java.lang.foreign.MemorySegment;

import static io.github.plixo2.sodalite.Internal.*;

/// @sdlAPI SDL_GPUTextureLocation
public record TextureLocation(
    Texture texture,
    long mipLevel,
    long layer,
    long x,
    long y,
    long z
) {


    public static TextureLocation of(
            Texture texture
    ) {
        return new TextureLocation(texture, 0, 0, 0, 0, 0);
    }

    public static TextureLocation of2D(
        Texture texture
    ) {
        return of2D(texture, 0);
    }
    public static TextureLocation of2D(
        Texture texture,
            long mipLevel
    ) {
        return of2D(texture, mipLevel, 0, 0);
    }
    public static TextureLocation of2D(
            Texture texture,
            long mipLevel,
            long x,
            long y
    ) {
        expectType(texture, TextureType.TEXTURE_2D);
        return new TextureLocation(texture, mipLevel, 0, x, y, 0);
    }

    public static TextureLocation of2DArray(
            Texture texture,
            long layer
    ) {
        return of2DArray(texture, layer, 0);
    }
    public static TextureLocation of2DArray(
            Texture texture,
            long layer,
            long mipLevel
    ) {
        return of2DArray(texture, mipLevel, layer, 0, 0);
    }
    public static TextureLocation of2DArray(
            Texture texture,
            long mipLevel,
            long layer,
            long x,
            long y
    ) {
        expectType(texture, TextureType.TEXTURE_2D_ARRAY);
        return new TextureLocation(texture, mipLevel, layer, x, y, 0);
    }

    public static TextureLocation of3D(
            Texture texture
    ) {
        return of3D(texture, 0);
    }
    public static TextureLocation of3D(
            Texture texture,
            long mipLevel
    ) {
        return of3D(texture, mipLevel, 0, 0, 0);
    }
    public static TextureLocation of3D(
            Texture texture,
            long mipLevel,
            long x,
            long y,
            long z
    ) {
        expectType(texture, TextureType.TEXTURE_3D);
        return new TextureLocation(texture, mipLevel, 0, x, y, z);
    }

    public static TextureLocation ofCube(
            Texture texture,
            CubeMapFace face
    ) {
        return ofCube(texture, 0, face);
    }
    public static TextureLocation ofCube(
            Texture texture,
            long mipLevel,
            CubeMapFace face
    ) {
        return ofCube(texture, mipLevel, face, 0, 0);
    }
    public static TextureLocation ofCube(
            Texture texture,
            long mipLevel,
            CubeMapFace face,
            long x,
            long y
    ) {
        expectType(texture, TextureType.TEXTURE_CUBE);
        return new TextureLocation(texture, mipLevel, face.code(), x, y, 0);
    }

    public static TextureLocation ofCubeArray(
            Texture texture,
            long index,
            CubeMapFace face
    ) {
        return ofCubeArray(texture, 0, index, face);
    }
    public static TextureLocation ofCubeArray(
            Texture texture,
            long mipLevel,
            long index,
            CubeMapFace face
    ) {
        return ofCubeArray(texture, mipLevel, index, face, 0, 0);
    }
    public static TextureLocation ofCubeArray(
            Texture texture,
            long mipLevel,
            long index,
            CubeMapFace face,
            long x,
            long y
    ) {
        expectType(texture, TextureType.TEXTURE_CUBE_ARRAY);
        var trueIndex = TextureRegion.cubeArrayIndex(texture, index, face);
        return new TextureLocation(
                texture,
                mipLevel,
                assertU32(trueIndex, "index"),
                x,
                y,
                0
        );
    }

    void put(MemorySegment segment) {
        SDL_GPUTextureLocation.initialize(
                segment,
                this.texture.segment(),
                assertU32(this.mipLevel, "mipLevel"),
                assertU32(this.layer, "layer"),
                assertU32(this.x, "x"),
                assertU32(this.y, "y"),
                assertU32(this.z, "z")
        );
    }

    private static void expectType(Texture texture, TextureType expected) {
        var type = texture.type();
        if (type != expected) {
            throw new IllegalArgumentException("Expected texture of type " + expected + " but got " + type);
        }
    }

}
