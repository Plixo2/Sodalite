package io.github.plixo2.sodalite.category.blendmode;

import lombok.RequiredArgsConstructor;

import static org.libsdl.sdl.SDL3_h.*;

/// @sdlAPI SDL_BlendOperation
@RequiredArgsConstructor
public enum BlendOperation {
    ADD(SDL_BLENDOPERATION_ADD()),
    SUBTRACT(SDL_BLENDOPERATION_SUBTRACT()),
    REV_SUBTRACT(SDL_BLENDOPERATION_REV_SUBTRACT()),
    MINIMUM(SDL_BLENDOPERATION_MINIMUM()),
    MAXIMUM(SDL_BLENDOPERATION_MAXIMUM()),

    ;


    private final int code;
    public int code() {
        return this.code;
    }
}
