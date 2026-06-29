package io.github.plixo2.sodalite.category.misc;

import io.github.plixo2.sodalite.SDLException;

import java.lang.foreign.Arena;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlCategory CategoryMisc
public class Misc {
    private Misc() {}

    /// @sdlAPI SDL_OpenURL
    public static void openUrl(String url) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            check(SDL_OpenURL(arena.allocateFrom(url)));
        }
    }

}
