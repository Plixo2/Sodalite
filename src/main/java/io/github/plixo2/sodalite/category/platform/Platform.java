package io.github.plixo2.sodalite.category.platform;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlCategory CategoryPlatform
public class Platform {
    private Platform() {}


    /// @sdlAPI SDL_GetPlatform
    public static String getPlatform() {
        return SDL_GetPlatform().toString();
    }

}
