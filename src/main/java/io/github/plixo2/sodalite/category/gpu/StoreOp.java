package io.github.plixo2.sodalite.category.gpu;


/// @sdlAPI SDL_GPUStoreOp
public enum StoreOp {
    STORE,
    DONT_CARE,
    RESOLVE,
    RESOLVE_AND_STORE,

    ;

    public int code() {
        return this.ordinal();
    }
}
