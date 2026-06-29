package io.github.plixo2.sodalite.category.video;

import io.github.plixo2.sodalite.Internal;

/// @sdlAPI SDL_FlashOperation
public enum FlashOperation {
    CANCEL,
    BRIEFLY,
    UNTIL_FOCUSED,

    ;
    public static FlashOperation fromCode(int code) {
        return Internal.enumFromCode(FlashOperation.class, code, CANCEL);
    }

    public int code() {
        return this.ordinal();
    }
}
