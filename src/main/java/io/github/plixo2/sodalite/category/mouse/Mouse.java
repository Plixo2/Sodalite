package io.github.plixo2.sodalite.category.mouse;

import io.github.plixo2.sodalite.category.video.MouseState;

import java.lang.foreign.Arena;
import java.lang.foreign.ValueLayout;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlCategory CategoryMouse
public class Mouse {
    private Mouse() {}



    /// @sdlAPI SDL_BUTTON_MASK
    /// @see #isLeftPressed
    /// @see #isMiddlePressed
    /// @see #isRightPressed
    /// @see #isX1Pressed
    /// @see #isX2Pressed
    public static boolean isPressed(@MouseButtonFlags int flags, @MouseButton int button) {
        var mask = 1 << (button - 1);
        return (flags & mask) != 0;
    }

    /// @sdlAPI SDL_BUTTON_LMASK
    public static boolean isLeftPressed(@MouseButtonFlags int flags) {
        return isPressed(flags, MouseButton.LEFT);
    }
    /// @sdlAPI SDL_BUTTON_MMASK
    public static boolean isMiddlePressed(@MouseButtonFlags int flags) {
        return isPressed(flags, MouseButton.MIDDLE);
    }
    /// @sdlAPI SDL_BUTTON_RMASK
    public static boolean isRightPressed(@MouseButtonFlags int flags) {
        return isPressed(flags, MouseButton.RIGHT);
    }
    /// @sdlAPI SDL_BUTTON_X1MASK
    public static boolean isX1Pressed(@MouseButtonFlags int flags) {
        return isPressed(flags, MouseButton.X1);
    }
    /// @sdlAPI SDL_BUTTON_X2MASK
    public static boolean isX2Pressed(@MouseButtonFlags int flags) {
        return isPressed(flags, MouseButton.X2);
    }

    /// Cached mouse state, updates by the last pump of the event queue.
    ///
    /// @sdlAPI SDL_GetMouseState
    /// @threadSafety This function should only be called on the main thread
    public static MouseState getMouseState() {
        try (var arena = Arena.ofConfined()) {
            var xSegment = arena.allocate(ValueLayout.JAVA_FLOAT);
            var ySegment = arena.allocate(ValueLayout.JAVA_FLOAT);
            var flags = SDL_GetMouseState(xSegment, ySegment);
            var x = xSegment.get(ValueLayout.JAVA_FLOAT, 0);
            var y = ySegment.get(ValueLayout.JAVA_FLOAT, 0);
            return MouseState.of(flags, x, y);
        }
    }

    /// Might be slower than [#getMouseState]
    ///
    /// @sdlAPI SDL_GetGlobalMouseState
    /// @threadSafety This function should only be called on the main thread
    public static MouseState getGlobalMouseState() {
        try (var arena = Arena.ofConfined()) {
            var xSegment = arena.allocate(ValueLayout.JAVA_FLOAT);
            var ySegment = arena.allocate(ValueLayout.JAVA_FLOAT);
            var flags = SDL_GetGlobalMouseState(xSegment, ySegment);
            var x = xSegment.get(ValueLayout.JAVA_FLOAT, 0);
            var y = ySegment.get(ValueLayout.JAVA_FLOAT, 0);
            return MouseState.of(flags, x, y);
        }
    }


}
