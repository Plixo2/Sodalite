package io.github.plixo2.sodalite.category.video;


import io.github.plixo2.sodalite.resource.ResourceSet;
import org.joml.Vector2i;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

public class Video {

    /// @apiNote SDL_CreateWindow
    public static Window createWindow(
            ResourceSet resources,
            String title,
            int width,
            int height,
            @WindowFlags long flags
    ) {

        MemorySegment window;
        try (var arena = Arena.ofConfined()) {
            var titleCStr = arena.allocateFrom(title);
            window = check(SDL_CreateWindow(titleCStr, width, height, flags));
        }

        return new Window(resources, window);
    }

    /// @apiNote SDL_GetWindowID
    static int getWindowID(Window window) {
        return SDL_GetWindowID(window.segment());
    }

    /// @apiNote SDL_DestroyWindow
    static void destroyWindow(Window.GPUClaim claim, MemorySegment window) {
        if (claim.claimed) {
            throw new IllegalStateException(
                    "Cannot destroy window while it is claimed by a GPU device. "
                    + "Call Device.releaseWindowForDevice(window) before destroying the window"
            );
        }

        SDL_DestroyWindow(window);
    }

    /// @apiNote SDL_GetWindowSize
    static Vector2i getWindowSize(Window window, Vector2i in) {
        try (var arena = Arena.ofConfined()) {
            var w = arena.allocate(ValueLayout.JAVA_INT);
            var h = arena.allocate(ValueLayout.JAVA_INT);
            SDL_GetWindowSize(window.segment(), w, h);
            in.set(w.get(ValueLayout.JAVA_INT, 0), h.get(ValueLayout.JAVA_INT, 0));
            return in;
        }
    }


    /// @apiNote SDL_GetWindowSizeInPixels
    static Vector2i getWindowSizeInPixels(Window window, Vector2i in) {
        try (var arena = Arena.ofConfined()) {
            var w = arena.allocate(ValueLayout.JAVA_INT);
            var h = arena.allocate(ValueLayout.JAVA_INT);
            SDL_GetWindowSizeInPixels(window.segment(), w, h);
            in.set(w.get(ValueLayout.JAVA_INT, 0), h.get(ValueLayout.JAVA_INT, 0));
            return in;
        }
    }

    /// @apiNote SDL_GetWindowDisplayScale
    static float getWindowDisplayScale(Window window) {
        return SDL_GetWindowDisplayScale(window.segment());
    }


}
