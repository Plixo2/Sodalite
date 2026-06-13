package io.github.plixo2.sodalite.category.messagebox;


import org.libsdl.sdl.SDL_MessageBoxButtonData;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Objects;

/// @see MessageBoxBuilder
/// @sdlAPI SDL_MessageBoxButtonData
public record MessageBoxButtonData(
        @MessageBoxButtonFlags int flags,
        int buttonID,
        String text
) {
    public static MessageBoxButtonData of(
            @MessageBoxButtonFlags int flags,
            String text
    ) {
        return new MessageBoxButtonData(flags, -1, text);
    }

    public static MessageBoxButtonData of(
            @MessageBoxButtonFlags int flags,
            int customID,
            String text
    ) {
        return new MessageBoxButtonData(flags, customID, text);
    }


    void put(Arena arena, MemorySegment segment) {
        var textSegment = arena.allocateFrom(Objects.requireNonNull(this.text, "Text is required"));
        SDL_MessageBoxButtonData.initialize(segment, this.flags, this.buttonID, textSegment);
    }
}
