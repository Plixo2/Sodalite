package io.github.plixo2.sodalite.category.keyboard;


import io.github.plixo2.sodalite.category.scancode.Scancode;
import lombok.Getter;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.function.Predicate;

/// Return value for `SDL_GetKeyboardState`
public class KeyboardState implements Predicate<Scancode> {

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


}
