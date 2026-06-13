package io.github.plixo2.sodalite.category.power;

import io.github.plixo2.sodalite.category.scancode.Scancode;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.libsdl.sdl.SDL3_h.*;

/// @sdlAPI SDL_PowerState
@RequiredArgsConstructor
public enum PowerState {
    ERROR(SDL_POWERSTATE_ERROR()),
    UNKNOWN(SDL_POWERSTATE_UNKNOWN()),
    ON_BATTERY(SDL_POWERSTATE_ON_BATTERY()),
    NO_BATTERY(SDL_POWERSTATE_NO_BATTERY()),
    CHARGING(SDL_POWERSTATE_CHARGING()),
    CHARGED(SDL_POWERSTATE_CHARGED()),

    ;

    private static final Map<Integer, PowerState> codeMap = Arrays.stream(values()).collect(
            Collectors.toMap(PowerState::code, Function.identity())
    );

    public static PowerState fromCode(int code) {
        return codeMap.getOrDefault(code, UNKNOWN);
    }

    private final int code;
    public int code() {
        return this.code;
    }
}
