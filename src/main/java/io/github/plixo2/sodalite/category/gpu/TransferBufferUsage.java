package io.github.plixo2.sodalite.category.gpu;

/// @apiNote SDL_GPUTransferBufferUsage
public enum TransferBufferUsage {
    UPLOAD,
    DOWNLOAD,

    ;

    public int code() {
        return this.ordinal();
    }
}
