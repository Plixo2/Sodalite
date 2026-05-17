package io.github.plixo2.sodalite.category.pen;

import io.github.plixo2.sodalite.Internal;

/// @apiNote SDL_PenAxis
public enum PenAxis {
    PRESSURE,
    XTILT,
    YTILT,
    DISTANCE,
    ROTATION,
    SLIDER,
    TANGENTIAL_PRESSURE,
    COUNT,

    ;

    public static PenAxis fromCode(int code) {
        return Internal.enumFromCode(PenAxis.class, code, PRESSURE);
    }

    public int code() {
        return this.ordinal();
    }
}
