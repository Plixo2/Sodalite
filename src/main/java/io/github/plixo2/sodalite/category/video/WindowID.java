package io.github.plixo2.sodalite.category.video;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/// @sdlAPI SDL_WindowID
@Getter
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode
@ToString
public class WindowID {
    private final int value;
}
