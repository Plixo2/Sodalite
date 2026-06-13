package io.github.plixo2.sodalite.category.gpu;


/// @sdlAPI SDL_GPUIndexElementSize
public enum IndexElementSize {
    U16,    // SDL_GPU_INDEXELEMENTSIZE_16BIT
    U32,    // SDL_GPU_INDEXELEMENTSIZE_32BIT

    ;

    public int code() {
        return ordinal();
    }
}
