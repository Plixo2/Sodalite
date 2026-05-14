package io.github.plixo2.sodalite.video;

import io.github.plixo2.sodalite.Internal;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

public class Video {

    /// @apiNote SDL_CreateWindow
    public static Window createWindow(String title, int width, int height, @WindowFlags long flags) {

        MemorySegment window;
        try (var arena = Arena.ofConfined()) {
            var titleCStr = arena.allocateFrom(title);
            window = SDL_CreateWindow(titleCStr, width, height, flags);
        }

        check(window);

        return new Window(window);
    }

    public static int getWindowID(Window window) {
        Internal.checkDestroyed(window);
        return SDL_GetWindowID(window.segment);
    }

    /// @apiNote SDL_DestroyWindow
    public static void destroyWindow(Window window) {
        window.destroy();
    }

    /// @apiNote SDL_DestroyWindow
    static void destroyWindow(MemorySegment segment) {
        SDL_DestroyWindow(segment);
    }


}
