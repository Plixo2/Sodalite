package io.github.plixo2.sodalite.category.mouse;

import io.github.plixo2.sodalite.Internal;

/// @sdlAPI SDL_MouseWheelDirection
public enum MouseWheelDirection {
    NORMAL,
    FLIPPED,

    ;

    public static MouseWheelDirection fromCode(int code) {
        return Internal.enumFromCode(MouseWheelDirection.class, code, NORMAL);
    }

    public int code() {
        return ordinal();
    }
}
