package io.github.plixo2.sodalite.category.blendmode;

import lombok.RequiredArgsConstructor;
import static org.libsdl.sdl.SDL3_h.*;

/// @sdlAPI SDL_BlendFactor
@RequiredArgsConstructor
public enum BlendFactor {
    ZERO(SDL_BLENDFACTOR_ZERO()),
    ONE(SDL_BLENDFACTOR_ONE()),
    SRC_COLOR(SDL_BLENDFACTOR_SRC_COLOR()),
    ONE_MINUS_SRC_COLOR(SDL_BLENDFACTOR_ONE_MINUS_SRC_COLOR()),
    SRC_ALPHA(SDL_BLENDFACTOR_SRC_ALPHA()),
    ONE_MINUS_SRC_ALPHA(SDL_BLENDFACTOR_ONE_MINUS_SRC_ALPHA()),
    DST_COLOR(SDL_BLENDFACTOR_DST_COLOR()),
    ONE_MINUS_DST_COLOR(SDL_BLENDFACTOR_ONE_MINUS_DST_COLOR()),
    DST_ALPHA(SDL_BLENDFACTOR_DST_ALPHA()),
    ONE_MINUS_DST_ALPHA(SDL_BLENDFACTOR_ONE_MINUS_DST_ALPHA()),

    ;


    private final int code;
    public int code() {
        return this.code;
    }
}
