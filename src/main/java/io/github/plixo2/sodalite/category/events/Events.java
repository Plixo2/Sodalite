package io.github.plixo2.sodalite.category.events;

import io.github.plixo2.sodalite.resource.PendingFrees;
import org.jetbrains.annotations.Nullable;
import org.libsdl.sdl.SDL_Event;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static org.libsdl.sdl.SDL3_h.*;

public class Events {


    /// Should be called on the main thread.
    /// @return true if there are more events, false otherwise.
    public static boolean pollEvent(@Nullable EventConsumer consumer) {
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
    ///
    /// This method will also call {@link PendingFrees#drain()}
    /// to free any resources that were queued for freeing after the last event.
    ///
    /// @apiNote SDL_PollEvent
    private static boolean pollSingleEvent(
            Arena arena,
            @Nullable EventConsumer consumer
    ) {
        MemorySegment eventOut = SDL_Event.allocate(arena);
        var hasEvent = SDL_PollEvent(eventOut);
        if (hasEvent) {
            if (consumer != null) {
                EventDispatch.dispatch(consumer, eventOut);
            }
            return true;
        } else {
            PendingFrees.drain();
            return false;
        }
    }

}
