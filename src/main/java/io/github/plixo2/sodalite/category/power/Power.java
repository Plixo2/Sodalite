package io.github.plixo2.sodalite.category.power;

import java.lang.foreign.Arena;
import java.lang.foreign.ValueLayout;


import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlCategory CategoryPower
public class Power {
    private Power() {}

    /// @sdlAPI SDL_GetPowerInfo
    public static PowerInfo getPowerInfo() {
        try (var arena = Arena.ofConfined()) {
            var secondsSegment = arena.allocate(ValueLayout.JAVA_INT);
            var percentSegment = arena.allocate(ValueLayout.JAVA_INT);

            var state = SDL_GetPowerInfo(secondsSegment, percentSegment);
            check(state != PowerState.ERROR.code());

            var seconds = secondsSegment.get(ValueLayout.JAVA_INT, 0);
            var percent = percentSegment.get(ValueLayout.JAVA_INT, 0);
            var powerState = PowerState.fromCode(state);
            return new PowerInfo(powerState, seconds, percent);

        }
    }

}
