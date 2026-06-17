package io.github.plixo2.sodalite.category.main;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @see Callbacks
/// @sdlCategory CategoryMain
public class Main {

    /// @sdlAPI SDL_GDKSuspendComplete
    public static void suspendComplete() {
        SDL_GDKSuspendComplete();
    }

    /// @sdlAPI SDL_RegisterApp
    public static void registerApp(String name, int style, MemorySegment hInst) {
        try (var arena = Arena.ofConfined()) {
            var nameSegment = arena.allocateFrom(name);
            check(SDL_RegisterApp(
                    nameSegment,
                    style,
                    hInst
            ));
        }
    }

    /// @sdlAPI SDL_UnregisterApp
    public static void unregisterApp() {
        SDL_UnregisterApp();
    }

}
