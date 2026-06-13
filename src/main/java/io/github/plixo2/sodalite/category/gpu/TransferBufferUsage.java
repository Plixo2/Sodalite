package io.github.plixo2.sodalite.category.gpu;

/// @sdlAPI SDL_GPUTransferBufferUsage
public enum TransferBufferUsage {
    UPLOAD,
    DOWNLOAD,

    ;

    public int code() {
        return this.ordinal();
    }
}
