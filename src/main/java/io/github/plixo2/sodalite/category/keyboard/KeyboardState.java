package io.github.plixo2.sodalite.category.keyboard;


import io.github.plixo2.sodalite.category.scancode.Scancode;
import lombok.Getter;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.function.Predicate;

/// Return value for `SDL_GetKeyboardState`
public class KeyboardState implements Predicate<Scancode> {

    /// Array of booleans. Never freed or otherwise validated
    /// as it persists for the lifetime of the application and is owned by sdl.
    /// The length of this array is 512 (SDL_SCANCODE_COUNT)
    @Getter
    private final MemorySegment stateArray;

    KeyboardState(MemorySegment stateArray) {
        this.stateArray = stateArray;
    }

    @Override
    public boolean test(Scancode scancode) {
        var index = scancode.code();
        return this.stateArray.get(ValueLayout.JAVA_BOOLEAN, index);
    }

    public boolean test(int scancode) {
        if (scancode < 0 || scancode >= Scancode.COUNT()) {
            throw new IllegalArgumentException("scancode must be in range [0, 512)");
        }
        return this.stateArray.get(ValueLayout.JAVA_BOOLEAN, scancode);
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof KeyboardState other && this.stateArray.address() == other.stateArray.address();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.stateArray.address());
    }

    @Override
    public String toString() {
        return "KeyboardState{}";
    }
}
