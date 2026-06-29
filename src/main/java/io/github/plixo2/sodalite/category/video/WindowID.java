package io.github.plixo2.sodalite.category.video;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

/// @sdlAPI SDL_WindowID
@Getter
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode
@ToString
public class WindowID {
    private final int value;

    public @Nullable Window getWindow() {
        return Video.getWindowFromID(this);
    }

}
