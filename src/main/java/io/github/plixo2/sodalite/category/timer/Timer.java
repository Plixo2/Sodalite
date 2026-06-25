package io.github.plixo2.sodalite.category.timer;


import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlCategory CategoryTimer
public class Timer {
    private Timer() {}

    public static final long MS_PER_SECOND = SDL_MS_PER_SECOND;
    public static final long US_PER_SECOND = SDL_US_PER_SECOND;
    public static final long NS_PER_SECOND = SDL_NS_PER_SECOND;
    public static final long NS_PER_MS = SDL_NS_PER_MS;
    public static final long NS_PER_US = SDL_NS_PER_US;


    /// @sdlAPI SDL_GetTicksNS
    public static long getTicksNS() {
        return SDL_GetTicksNS();
    }


    /// @sdlAPI SDL_GetTicks
    public static long getTicksMS() {
        return SDL_GetTicks();
    }

}
