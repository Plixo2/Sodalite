package io.github.plixo2.sodalite.events;

import org.libsdl.sdl.SDL_Event;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

public class Events {


    public static Event pollEvent() {

        try (var arena = Arena.ofConfined()) {
            MemorySegment eventOut = SDL_Event.allocate(arena);
            var hasEvent = SDL_PollEvent(eventOut);
            if (hasEvent) {
                return Event.from(eventOut);
            } else {
                return null;
            }
        }
    }

}
