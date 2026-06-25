package io.github.plixo2.sodalite.category.mouse;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/// Return value of `SDL_GetMouseState`
@Getter
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode
@ToString
public class MouseState {
    private final @MouseButtonFlags int flags;
    private final float x;
    private final float y;
}
