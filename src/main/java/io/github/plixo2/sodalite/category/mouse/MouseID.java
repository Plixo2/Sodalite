package io.github.plixo2.sodalite.category.mouse;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/// @sdlAPI SDL_MouseID
@Getter
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode
@ToString
public class MouseID {
    private final int value;
}
