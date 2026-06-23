package io.github.plixo2.sodalite.category.thread;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlCategory CategoryThread
public class Threads {
    private Threads() {}

    /// @sdlAPI SDL_GetCurrentThreadID
    public static ThreadID getCurrentThreadID() {
        return ThreadID.of(SDL_GetCurrentThreadID());
    }


}
