package io.github.plixo2.sodalite.category.mouse;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/// Return value of `SDL_GetMouseState`
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode
@ToString
public class MouseState {
    private final @MouseButtonFlags int flags;
    @Getter
    private final float x;
    @Getter
    private final float y;

    public @MouseButtonFlags int flags() {
        return this.flags;
    }
}
