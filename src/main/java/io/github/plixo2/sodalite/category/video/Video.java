package io.github.plixo2.sodalite.category.video;


import io.github.plixo2.sodalite.category.properties.PropertyGroup;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.joml.Vector2i;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlCategory CategoryVideo
public class Video {
    private Video() {}

    /// Will implicitly initialize the video subsystem if needed
    /// (default `SDL_CreateWindow` behavior).
    ///
    /// @sdlAPI SDL_CreateWindow
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

    /// @sdlAPI SDL_CreatePopupWindow
    public static Window createPopupWindow(
            ResourceSet resources,
            Window parent,
            int offsetX,
            int offsetY,
            int width,
            int height,
            @WindowFlags long flags
    ) {
        var isTooltop = (flags & WindowFlags.TOOLTIP) != 0;
        var isPopup = (flags & WindowFlags.POPUP_MENU) != 0;
        if (!isTooltop && !isPopup) {
            throw new IllegalArgumentException("Flags must include either WindowFlags.TOOLTIP or WindowFlags.POPUP_MENU");
        }

        var window = check(SDL_CreatePopupWindow(parent.segment(), offsetX, offsetY, width, height, flags));

        return new Window(resources, window);
    }

    /// @sdlAPI SDL_CreateWindowWithProperties
    public static Window createWindowWithProperties(
            ResourceSet resources,
            PropertyGroup props
    ) {
        var window = check(SDL_CreateWindowWithProperties(props.id()));
        return new Window(resources, window);
    }

    /// @sdlAPI SDL_GetWindowID
    public static WindowID getWindowID(Window window) {
        var value = check(SDL_GetWindowID(window.segment()));
        return WindowID.of(value);
    }

    /// @sdlAPI SDL_DestroyWindow
    static void destroyWindow(MemorySegment window) {
        SDL_DestroyWindow(window);
    }

    /// @sdlAPI SDL_SetWindowTitle
    static void setWindowTitle(Window window, String title) {
        try (var arena = Arena.ofConfined()) {
            var titleCStr = arena.allocateFrom(title);
            check(SDL_SetWindowTitle(window.segment(), titleCStr));
        }
    }

    /// @sdlAPI SDL_GetWindowSize
    static Vector2i getWindowSize(Window window, Vector2i in) {
        try (var arena = Arena.ofConfined()) {
            var w = arena.allocate(ValueLayout.JAVA_INT);
            var h = arena.allocate(ValueLayout.JAVA_INT);
            SDL_GetWindowSize(window.segment(), w, h);
            in.set(w.get(ValueLayout.JAVA_INT, 0), h.get(ValueLayout.JAVA_INT, 0));
            return in;
        }
    }


    /// @sdlAPI SDL_GetWindowSizeInPixels
    static Vector2i getWindowSizeInPixels(Window window, Vector2i in) {
        try (var arena = Arena.ofConfined()) {
            var w = arena.allocate(ValueLayout.JAVA_INT);
            var h = arena.allocate(ValueLayout.JAVA_INT);
            SDL_GetWindowSizeInPixels(window.segment(), w, h);
            in.set(w.get(ValueLayout.JAVA_INT, 0), h.get(ValueLayout.JAVA_INT, 0));
            return in;
        }
    }

    /// @sdlAPI SDL_GetWindowDisplayScale
    static float getWindowDisplayScale(Window window) {
        return SDL_GetWindowDisplayScale(window.segment());
    }



}
