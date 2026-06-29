package io.github.plixo2.sodalite.category.video;


import io.github.plixo2.sodalite.category.scancode.Scancode;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.libsdl.sdl.SDL3_h.*;

/// @sdlAPI SDL_ProgressState
@RequiredArgsConstructor
public enum ProgressState {
    INVALID(SDL_PROGRESS_STATE_INVALID),
    NONE(SDL_PROGRESS_STATE_NONE),
    INDETERMINATE(SDL_PROGRESS_STATE_INDETERMINATE),
    NORMAL(SDL_PROGRESS_STATE_NORMAL),
    PAUSED(SDL_PROGRESS_STATE_PAUSED),
    ERROR(SDL_PROGRESS_STATE_ERROR),

    ;
    private final int code;

    private static final Map<Integer, ProgressState> codeMap = Arrays.stream(values()).collect(
            Collectors.toMap(ProgressState::code, Function.identity())
    );

    public static ProgressState fromCode(int code) {
        return codeMap.getOrDefault(code, INVALID);
    }


    public int code() {
        return this.code;
    }

}
