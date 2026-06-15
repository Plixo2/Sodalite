package io.github.plixo2.sodalite.category.events;

import io.github.plixo2.sodalite.resource.PendingFrees;
import org.jetbrains.annotations.Nullable;
import org.libsdl.sdl.SDL_Event;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.List;

import static org.libsdl.sdl.SDL3_h.*;

/// @sdlCategory CategoryEvents
public class Events {
    private Events() {}

    /// Should be called on the main thread.
    /// @return true if there are more events, false otherwise.
    public static boolean pollEvent(EventConsumer consumer) {
        try (var arena = Arena.ofConfined()) {
            return pollSingleEvent(arena, consumer);
        }
    }

    /// Should be called on the main thread.
    public static void pollEvents(EventConsumer consumer) {
        try (var arena = Arena.ofConfined()) {
            //noinspection StatementWithEmptyBody
            while (pollSingleEvent(arena, consumer)) {
                // nothing
            }
        }
    }
    /// Should be called on the main thread.
    public static void pollEvents(EventConsumer... consumers) {
        try (var arena = Arena.ofConfined()) {
            //noinspection StatementWithEmptyBody
            while (pollSingleEvent(arena, consumers)) {
                // nothing
            }
        }
    }

    /// Should be called on the main thread.
    public static void pollEvents(List<? extends EventConsumer> consumers) {
        try (var arena = Arena.ofConfined()) {
            //noinspection StatementWithEmptyBody
            while (pollSingleEvent(arena, consumers)) {
                // nothing
            }
        }
    }

    /// Should be called on the main thread.
    ///
    /// This method will also call {@link PendingFrees#drain()}
    /// to free any resources that were queued for freeing after the last event.
    ///
    /// @sdlAPI SDL_PollEvent
    private static boolean pollSingleEvent(
            Arena arena,
            EventConsumer consumer
    ) {
        MemorySegment eventOut = SDL_Event.allocate(arena);
        var hasEvent = SDL_PollEvent(eventOut);
        if (hasEvent) {
            EventDispatch.dispatch(consumer, eventOut);
            return true;
        } else {
            PendingFrees.drain();
            return false;
        }
    }

    private static boolean pollSingleEvent(
            Arena arena,
            EventConsumer... consumers
    ) {
        MemorySegment eventOut = SDL_Event.allocate(arena);
        var hasEvent = SDL_PollEvent(eventOut);
        if (hasEvent) {
            for (var consumer : consumers) {
                EventDispatch.dispatch(consumer, eventOut);
            }
            return true;
        } else {
            PendingFrees.drain();
            return false;
        }
    }

    private static boolean pollSingleEvent(
            Arena arena,
            List<? extends EventConsumer> consumers
    ) {
        MemorySegment eventOut = SDL_Event.allocate(arena);
        var hasEvent = SDL_PollEvent(eventOut);
        if (hasEvent) {
            for (var consumer : consumers) {
                EventDispatch.dispatch(consumer, eventOut);
            }
            return true;
        } else {
            PendingFrees.drain();
            return false;
        }
    }
}
