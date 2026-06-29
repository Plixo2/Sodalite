package io.github.plixo2.sodalite.category.video;

/// @sdlAPI SDL_HitTestResult
public enum HitTestResult {
    NORMAL,
    DRAGGABLE,
    RESIZE_TOPLEFT,
    RESIZE_TOP,
    RESIZE_TOPRIGHT,
    RESIZE_RIGHT,
    RESIZE_BOTTOMRIGHT,
    RESIZE_BOTTOM,
    RESIZE_BOTTOMLEFT,
    RESIZE_LEFT,

    ;
    public int code() {
        return this.ordinal();
    }
}
