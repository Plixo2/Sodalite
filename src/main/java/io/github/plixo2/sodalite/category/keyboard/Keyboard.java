package io.github.plixo2.sodalite.category.keyboard;


import io.github.plixo2.sodalite.category.keycode.Keycode;
import io.github.plixo2.sodalite.category.keycode.Keymod;
import io.github.plixo2.sodalite.category.scancode.Scancode;
import io.github.plixo2.sodalite.category.video.Window;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.ArrayList;
import java.util.List;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlCategory CategoryKeyboard
public class Keyboard {
    private Keyboard() {}

    private static volatile KeyboardState keyboardState = null;

    /// @sdlAPI SDL_GetKeyboardState
    public static synchronized KeyboardState getKeyboardState() {
        if (keyboardState == null) {
            var stateArray = SDL_GetKeyboardState(MemorySegment.NULL);
            keyboardState = new KeyboardState(stateArray);
        }
        return keyboardState;
    }


    /// @sdlAPI SDL_ClearComposition
    public static void clearComposition(Window window) {
        check(SDL_ClearComposition(window.segment()));
    }

    /// @sdlAPI SDL_GetKeyboardFocus
    public static @Nullable Window getKeyboardFocus() {
        var result = SDL_GetKeyboardFocus();
        if (result == MemorySegment.NULL) {
            return null;
        }
        return Window.newUnchecked(result);
    }

    /// @sdlAPI SDL_GetKeyboardNameForID
    public static String getKeyboardName(KeyboardID keyboard) {
        var result = check(SDL_GetKeyboardNameForID(keyboard.id()));
        return result.getString(0);
    }

    /// @sdlAPI SDL_GetKeyboards
    public static List<KeyboardID> getKeyboards() {
        try (var arena = Arena.ofConfined()) {
            var countSeg = arena.allocate(ValueLayout.JAVA_INT);
            var result = check(SDL_GetKeyboards(countSeg));
            try {
                int count = countSeg.get(ValueLayout.JAVA_INT, 0);

                var keyboards = new ArrayList<KeyboardID>(count);
                for (int i = 0; i < count; i++) {
                    var id = result.getAtIndex(ValueLayout.JAVA_INT, i);
                    keyboards.add(KeyboardID.of(id));
                }
                return keyboards;
            } finally {
                SDL_free(result);
            }
        }
    }

    /// @sdlAPI SDL_GetKeyFromName
    public static Keycode getKeyFromName(String name, Keycode defaultValue) {
        try (var arena = Arena.ofConfined()) {
            var nameSeg = arena.allocateFrom(name);
            var result = SDL_GetKeyFromName(nameSeg);
            var keycode = Keycode.fromCode(result);
            if (keycode == Keycode.UNKNOWN) {
                return defaultValue;
            } else {
                return keycode;
            }
        }
    }

    /// @sdlAPI SDL_GetKeyFromScancode
    public static Keycode getKeyFromScancode(Scancode scancode, @Keymod int modstate, boolean key_event) {
        var result = SDL_GetKeyFromScancode(scancode.code(), (short) modstate, key_event);
        return Keycode.fromCode(result);
    }

    /// @sdlAPI SDL_GetKeyName
    public static String getKeyName(Keycode keycode) {
        var result = SDL_GetKeyName(keycode.code());
        return result.getString(0);
    }

    /// @sdlAPI SDL_GetModState
    public static @Keymod int getModState() {
        return SDL_GetModState();
    }

    /// @sdlAPI SDL_GetScancodeFromKey
    public static Scancode getScancodeFromKey(Keycode keycode) {
        var result = SDL_GetScancodeFromKey(keycode.code(), MemorySegment.NULL);
        return Scancode.fromCode(result);
    }

    /// @sdlAPI SDL_GetScancodeFromName
    public static Scancode getScancodeFromName(String name, Scancode defaultValue) {
        try (var arena = Arena.ofConfined()) {
            var nameSeg = arena.allocateFrom(name);
            var result = SDL_GetScancodeFromName(nameSeg);
            var scancode = Scancode.fromCode(result);
            if (scancode == Scancode.UNKNOWN) {
                return defaultValue;
            } else {
                return scancode;
            }
        }
    }

    /// @sdlAPI SDL_GetScancodeName
    public static String getScancodeName(Scancode scancode) {
        var result = SDL_GetScancodeName(scancode.code());
        return result.getString(0);
    }

}
