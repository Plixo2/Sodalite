package io.github.plixo2.sodalite.category.video;


import io.github.plixo2.sodalite.Internal;

/// @apiNote SDL_DisplayOrientation
public enum DisplayOrientation {
    UNKNOWN,
    LANDSCAPE,
    LANDSCAPE_FLIPPED,
    PORTRAIT,
    PORTRAIT_FLIPPED,

    ;

    public static DisplayOrientation fromCode(int code) {
        return Internal.enumFromCode(DisplayOrientation.class, code, UNKNOWN);
    }

    public int code() {
        return this.ordinal();
    }
}
