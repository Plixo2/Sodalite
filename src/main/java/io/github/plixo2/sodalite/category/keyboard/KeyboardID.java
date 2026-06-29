package io.github.plixo2.sodalite.category.keyboard;

import io.github.plixo2.sodalite.SDLException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/// @sdlAPI SDL_KeyboardID
@Getter
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode
@ToString
public class KeyboardID {
    private final int value;

    public String getName() throws SDLException {
        return Keyboard.getKeyboardName(this);
    }
}
