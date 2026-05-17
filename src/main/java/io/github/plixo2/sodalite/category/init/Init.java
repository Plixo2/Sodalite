package io.github.plixo2.sodalite.category.init;

import io.github.plixo2.sodalite.resource.FreeList;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

public class Init {

    /// @apiNote SDL_Init
    public static void init(@InitFlags int flags) {
        check(SDL_Init(flags));
    }

    /// Should only be called on the main thread.
    /// @apiNote SDL_Quit
    public static void quit() {
        try {
            FreeList.drain();
            FreeList.freeGlobals();
        } finally {
            SDL_Quit();
        }
    }

}
