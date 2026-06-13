package io.github.plixo2.sodalite.category.gpu;


/// @sdlAPI SDL_GPUTextureType
public enum TextureType {
    TEXTURE_2D,
    TEXTURE_2D_ARRAY,
    TEXTURE_3D,
    TEXTURE_CUBE,
    TEXTURE_CUBE_ARRAY,

    ;

    public boolean isArray() {
        return this == TEXTURE_2D_ARRAY || this == TEXTURE_CUBE_ARRAY;
    }
    public boolean hasDepth() {
        return this == TEXTURE_3D;
    }

    public int code() {
        return this.ordinal();
    }
}
