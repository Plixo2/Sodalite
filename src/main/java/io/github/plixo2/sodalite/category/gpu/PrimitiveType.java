package io.github.plixo2.sodalite.category.gpu;

/// @apiNote SDL_GPUPrimitiveType
public enum PrimitiveType {
    TRIANGLELIST,
    TRIANGLESTRIP,
    LINELIST,
    LINESTRIP,
    POINTLIST,

    ;

    public int code() {
        return this.ordinal();
    }
}
