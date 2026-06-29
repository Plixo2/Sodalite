package io.github.plixo2.sodalite.category.video;

import io.github.plixo2.sodalite.Internal;

/// @sdlAPI SDL_SystemTheme
public enum SystemTheme {
    UNKNOWN,
    LIGHT,
    DARK,

    ;
    public static SystemTheme fromCode(int code) {
        return Internal.enumFromCode(SystemTheme.class, code, UNKNOWN);
    }

    public int code() {
        return this.ordinal();
    }
}
