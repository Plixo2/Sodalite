package io.github.plixo2.sodalite.category.error;




import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlCategory CategoryError
public class Error {
    private Error() {}

    private static final MethodHandle setError;
    static {
        var sdl_setError = SDL_SetError.makeInvoker(ValueLayout.ADDRESS).handle();
        sdl_setError = MethodHandles.insertArguments(sdl_setError, 0, Arena.global().allocateFrom("%s"));
        var newType = sdl_setError.type().changeReturnType(void.class);
        setError = MethodHandles.explicitCastArguments(sdl_setError, newType);
    }


    /// @sdlAPI SDL_GetError
    public static String getError() {
        var errorString = assertNotNull(SDL_GetError(), "SDL_GetError never returns null");
        return errorString.getString(0, StandardCharsets.UTF_8);
    }


    /// @sdlAPI SDL_ClearError
    public static void clearError() {
        assertTrue(SDL_ClearError(), "SDL_ClearError must return true");
    }

    /// @sdlAPI SDL_OutOfMemory
    public static void outOfMemory() {
        assertTrue(!SDL_OutOfMemory(), "SDL_OutOfMemory must return false");
    }

    /// @sdlAPI SDL_SetError
    public static void setError(String error) {
        try (var arena = Arena.ofConfined()) {
            var errorSegment = arena.allocateFrom(error);
            try {
                setError.invokeExact(errorSegment);
            } catch (ClassCastException | IllegalArgumentException ex$) {
                throw ex$;
            } catch (Throwable ex$) {
                throw new AssertionError("should not reach here", ex$);
            }
        }
    }
}
