package io.github.plixo2.sodalite.category.main;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @see Callbacks
/// @sdlCategory CategoryMain
public class Main {

    /// Dont use this method unless you know what you are doing.
    /// @sdlAPI SDL_GDKSuspendComplete
    public static void suspendComplete() {
        SDL_GDKSuspendComplete();
    }

    /// Dont use this method unless you know what you are doing.
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

    /// Dont use this method unless you know what you are doing.
    /// @sdlAPI SDL_UnregisterApp
    public static void unregisterApp() {
        SDL_UnregisterApp();
    }

    /// @throws Exception a exception from any of the callbacks
    /// @return true for success, false for failure
    /// @sdlAPI SDL_EnterAppMainCallbacks
    public static boolean enterAppMainCallbacks(Callbacks callbacks, String[] args) throws Exception {
        var wrapper = new CallbackWrapper(callbacks);
        return wrapper.run(args);
    }

}
