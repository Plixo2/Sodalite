package io.github.plixo2.sodalite.category.events;

import io.github.plixo2.sodalite.resource.PendingFrees;
import org.libsdl.sdl.SDL_Event;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.List;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlCategory CategoryEvents
public class Events {
    private Events() {}


    /// This method will also call [PendingFrees#drain]
    ///
    /// @sdlAPI SDL_PumpEvents
    /// @threadSafety This function should only be called on the main thread
    public static void pumpEvents() {
        SDL_PumpEvents();
        PendingFrees.drain();
    }

    /// This method will also call [PendingFrees#drain]
    ///
    /// @sdlAPI SDL_WaitEvent
    /// @threadSafety This function should only be called on the main thread
    public static void waitEvent(EventConsumer consumer) {
        try (var arena = Arena.ofConfined()) {
            waitSingleEvent(
                    SDL_Event.allocate(arena),
                    consumer
            );
        }
    }


    /// Consider using [#pollEvents(EventConsumer)] to poll all events at once.
    /// This method will also call [PendingFrees#drain] when there are no more events to poll.
    ///
    /// @return true if there are more events, false otherwise.
    /// @threadSafety This function should only be called on the main thread
    /// @see #pollEvents(EventConsumer)
    /// @sdlAPI SDL_PollEvent
    public static boolean pollEvent(EventConsumer consumer) {
        try (var arena = Arena.ofConfined()) {
            return pollSingleEvent(
                    SDL_Event.allocate(arena),
                    consumer
            );
        }
    }

    /// Consider using [#pollEvents(EventConsumer...)] to poll all events at once.
    /// This method will also call [PendingFrees#drain] when there are no more events to poll.
    ///
    /// @return true if there are more events, false otherwise.
    /// @threadSafety This function should only be called on the main thread
    /// @see #pollEvents(EventConsumer...)
    /// @sdlAPI SDL_PollEvent
    public static boolean pollEvent(EventConsumer... consumers) {
        try (var arena = Arena.ofConfined()) {
            return pollSingleEvent(
                    SDL_Event.allocate(arena),
                    consumers
            );
        }
    }

    /// Consider using [#pollEvents(Iterable)] to poll all events at once.
    /// This method will also call [PendingFrees#drain] when there are no more events to poll.
    ///
    /// @return true if there are more events, false otherwise.
    /// @threadSafety This function should only be called on the main thread
    /// @see #pollEvents(Iterable)
    /// @sdlAPI SDL_PollEvent
    public static boolean pollEvent(Iterable<? extends EventConsumer> consumers) {
        try (var arena = Arena.ofConfined()) {
            return pollSingleEvent(
                    SDL_Event.allocate(arena),
                    consumers
            );
        }
    }


    /// Polls all events at once and dispatches them to the given consumer.
    /// This method will also call [PendingFrees#drain]
    ///
    /// @threadSafety This function should only be called on the main thread
    /// @sdlAPI SDL_PollEvent
    public static void pollEvents(EventConsumer consumer) {
        try (var arena = Arena.ofConfined()) {
            var eventOut = SDL_Event.allocate(arena);
            //noinspection StatementWithEmptyBody
            while (pollSingleEvent(eventOut, consumer)) {
                // nothing
            }
        }
    }

    /// Polls all events at once and dispatches them to the given consumers.
    /// This method will also call [PendingFrees#drain]
    ///
    /// @threadSafety This function should only be called on the main thread
    /// @sdlAPI SDL_PollEvent
    public static void pollEvents(EventConsumer... consumers) {
        try (var arena = Arena.ofConfined()) {
            var eventOut = SDL_Event.allocate(arena);
            //noinspection StatementWithEmptyBody
            while (pollSingleEvent(eventOut, consumers)) {
                // nothing
            }
        }
    }

    /// Polls all events at once and dispatches them to the given consumers.
    /// This method will also call [PendingFrees#drain]
    ///
    /// @threadSafety This function should only be called on the main thread
    /// @sdlAPI SDL_PollEvent
    public static void pollEvents(Iterable<? extends EventConsumer> consumers) {
        try (var arena = Arena.ofConfined()) {
            var eventOut = SDL_Event.allocate(arena);
            //noinspection StatementWithEmptyBody
            while (pollSingleEvent(eventOut, consumers)) {
                // nothing
            }
        }
    }


    /// @sdlOther SDL_PollEvent
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

    /// @sdlOther SDL_PollEvent
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

    /// @sdlOther SDL_PollEvent
    private static boolean pollSingleEvent(
            MemorySegment eventOut,
            Iterable<? extends EventConsumer> consumers
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


    /// @sdlOther SDL_WaitEvent
    private static void waitSingleEvent(
            MemorySegment eventOut,
            EventConsumer consumer
    ) {
        check(SDL_WaitEvent(eventOut));
        EventDispatch.dispatch(consumer, eventOut);
        PendingFrees.drain();
    }

}
