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

    /// This method will also call {@link PendingFrees#drain()}
    ///
    /// @return true if there are more events, false otherwise.
    /// @threadSafety This function should only be called on the main thread
    public static boolean pollEvent(EventConsumer consumer) {
        try (var arena = Arena.ofConfined()) {
            return pollSingleEvent(
                    SDL_Event.allocate(arena),
                    consumer
            );
        }
    }

    /// This method will also call {@link PendingFrees#drain()}
    ///
    /// @threadSafety This function should only be called on the main thread
    public static void pollEvents(EventConsumer consumer) {
        try (var arena = Arena.ofConfined()) {
            var eventOut = SDL_Event.allocate(arena);
            //noinspection StatementWithEmptyBody
            while (pollSingleEvent(eventOut, consumer)) {
                // nothing
            }
        }
    }

    /// This method will also call {@link PendingFrees#drain()}
    ///
    /// @threadSafety This function should only be called on the main thread
    public static void pollEvents(EventConsumer... consumers) {
        try (var arena = Arena.ofConfined()) {
            var eventOut = SDL_Event.allocate(arena);
            //noinspection StatementWithEmptyBody
            while (pollSingleEvent(eventOut, consumers)) {
                // nothing
            }
        }
    }

    /// This method will also call {@link PendingFrees#drain()}
    ///
    /// @threadSafety This function should only be called on the main thread
    public static void pollEvents(List<? extends EventConsumer> consumers) {
        try (var arena = Arena.ofConfined()) {
            var eventOut = SDL_Event.allocate(arena);
            //noinspection StatementWithEmptyBody
            while (pollSingleEvent(eventOut, consumers)) {
                // nothing
            }
        }
    }


    /// @sdlAPI SDL_PollEvent
    private static boolean pollSingleEvent(
            MemorySegment eventOut,
            EventConsumer consumer
    ) {
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
            MemorySegment eventOut,
            EventConsumer... consumers
    ) {
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
            MemorySegment eventOut,
            List<? extends EventConsumer> consumers
    ) {
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
