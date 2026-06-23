package io.github.plixo2.sodalite.category.thread;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/// @sdlAPI SDL_ThreadID
@Getter
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode
@ToString
public class ThreadID {
    private final long value;
}
