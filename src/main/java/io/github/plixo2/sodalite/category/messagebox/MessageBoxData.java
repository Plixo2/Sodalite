package io.github.plixo2.sodalite.category.messagebox;



import io.github.plixo2.sodalite.category.video.Window;
import org.jetbrains.annotations.Nullable;
import org.libsdl.sdl.SDL_MessageBoxButtonData;
import org.libsdl.sdl.SDL_MessageBoxColorScheme;
import org.libsdl.sdl.SDL_MessageBoxData;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;


/// Consider using [MessageBoxBuilder]
///
/// @param callback Handler for the id of the pressed button.
/// Can be ignored, as the [MessageBox#show] method will return the id of the pressed button.
///
/// @see MessageBoxBuilder
/// @sdlAPI SDL_MessageBoxData
public record MessageBoxData(
    @MessageBoxFlags int flags,
    @Nullable Window window,
    String title,
    String message,
    List<MessageBoxButtonData> buttons,
    @Nullable MessageBoxColorScheme colorScheme,
    @Nullable Consumer<Integer> callback
) {

    public static MessageBoxData of(
            @MessageBoxFlags int flags,
            @Nullable Window window,
            String title,
            String message,
            List<MessageBoxButtonData> buttons
    ) {
        return new MessageBoxData(flags, window, title, message, buttons, null, null);
    }
    public static MessageBoxData of(
            @MessageBoxFlags int flags,
            String title,
            String message,
            List<MessageBoxButtonData> buttons
    ) {
        return new MessageBoxData(flags, null, title, message, buttons, null, null);
    }

    public static MessageBoxData of(
            @MessageBoxFlags int flags,
            @Nullable Window window,
            String title,
            String message
    ) {
        return new MessageBoxData(flags, window, title, message, new ArrayList<>(), null, null);
    }
    public static MessageBoxData of(
            @MessageBoxFlags int flags,
            String title,
            String message
    ) {
        return new MessageBoxData(flags, null, title, message, new ArrayList<>(), null, null);
    }

    public int addButton(String text) {
        var autoID = this.buttons.size();
        return addButton(new MessageBoxButtonData(MessageBoxButtonFlags.NONE, autoID, text));
    }
    public int addButton(String text, @MessageBoxButtonFlags int flags) {
        var autoID = this.buttons.size();
        return addButton(new MessageBoxButtonData(flags, autoID, text));
    }
    public int addButton(MessageBoxButtonData data) {
        this.buttons.add(data);
        return data.buttonID();
    }

    /// @return the id of the pressed button
    public int show() {
        return MessageBox.show(this);
    }

    MemorySegment put(Arena arena) {
        var segment = SDL_MessageBoxData.allocate(arena);
        var titleSegment = arena.allocateFrom(Objects.requireNonNull(this.title, "Title is required"));
        var messageSegment = arena.allocateFrom(Objects.requireNonNull(this.message, "Message is required"));

        var buttonsSegment = MemorySegment.NULL;
        if (!this.buttons.isEmpty()) {
            buttonsSegment = SDL_MessageBoxButtonData.allocateArray(this.buttons.size(), arena);
            for (int i = 0; i < this.buttons.size(); i++) {
                var button = this.buttons.get(i);
                var slice = SDL_MessageBoxButtonData.asSlice(buttonsSegment, i);
                button.put(arena, slice);
            }
        }

        var colorSchemeSegment = MemorySegment.NULL;
        if (this.colorScheme != null) {
            colorSchemeSegment = SDL_MessageBoxColorScheme.allocate(arena);
            this.colorScheme.put(colorSchemeSegment);
        }

        SDL_MessageBoxData.initialize(
                segment,
                this.flags,
                this.window == null ? MemorySegment.NULL : this.window.segment(),
                titleSegment,
                messageSegment,
                this.buttons.size(),
                buttonsSegment,
                colorSchemeSegment
        );

        return segment;
    }
}
