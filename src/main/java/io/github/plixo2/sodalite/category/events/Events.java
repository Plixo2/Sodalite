package io.github.plixo2.sodalite.category.events;

import io.github.plixo2.sodalite.resource.FreeList;
import org.jetbrains.annotations.Nullable;
import org.libsdl.sdl.SDL_Event;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static org.libsdl.sdl.SDL3_h.*;

public class Events {
    private static final EventIterator eventIterator = new EventIterator();
    private static final Iterable<EventOld> eventIterable = () -> {
        eventIterator.start();
        return eventIterator;
    };

    /// Should be called on the main thread.
    ///
    /// This method will also call {@link FreeList#drain()}
    /// to free any resources that were queued for freeing after the last event.
    ///
    /// @apiNote SDL_PollEvent
    public static @Nullable EventOld pollEvent() {
        try (var arena = Arena.ofConfined()) {
            MemorySegment eventOut = SDL_Event.allocate(arena);
            var hasEvent = SDL_PollEvent(eventOut);
            if (hasEvent) {
                return null;
            } else {
                FreeList.drain();
                return null;
            }
        }
    }

    public static Iterable<EventOld> pollEvents() {
        return eventIterable;
    }

}
