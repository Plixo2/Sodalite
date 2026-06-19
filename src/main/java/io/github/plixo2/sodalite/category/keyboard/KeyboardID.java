package io.github.plixo2.sodalite.category.keyboard;

import lombok.Getter;

/// @sdlAPI SDL_KeyboardID
public class KeyboardID {
    @Getter
    private final int id;

    KeyboardID(int id) {
        this.id = id;
    }
    public static KeyboardID of(int id) {
        return new KeyboardID(id);
    }

    public String getName() {
        return Keyboard.getKeyboardName(this);
    }

}
