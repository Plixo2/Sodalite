package io.github.plixo2.sodalite.error;



import java.lang.foreign.Arena;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

public class Error {

    /// @apiNote SDL_GetError
    public static String getError() {
        var errorString = assertNotNull(SDL_GetError(), "SDL_GetError never returns null");
        return errorString.getString(0);
    }


    /// @apiNote SDL_ClearError
    public static void clearError() {
        assertTrue(SDL_ClearError(), "SDL_ClearError must return true");
    }


}
