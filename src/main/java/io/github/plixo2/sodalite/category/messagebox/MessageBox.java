package io.github.plixo2.sodalite.category.messagebox;

import io.github.plixo2.sodalite.category.video.Window;
import org.jetbrains.annotations.Nullable;
import org.libsdl.sdl.SDL_MessageBoxData;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Objects;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlCategory CategoryMessagebox
public class MessageBox {
    private MessageBox() {}

    /// @return the id of the pressed button
    /// @sdlAPI SDL_ShowMessageBox
    public static int show(
            MessageBoxData data
    ) {
        try (var arena = Arena.ofConfined()) {
            var segment = data.put(arena);
            var buttonIDSegment = arena.allocate(ValueLayout.JAVA_INT);
            check(SDL_ShowMessageBox(segment, buttonIDSegment));
            var buttonID = buttonIDSegment.get(ValueLayout.JAVA_INT, 0);
            var callback = data.callback();
            if (callback != null) {
                callback.accept(buttonID);
            }
            return buttonID;
        }
    }

    /// @return the id of the pressed button
    /// @sdlAPI SDL_ShowMessageBox
    public static int show(
            MessageBoxBuilder builder
    ) {
        return show(builder.build());
    }


    /// @sdlAPI SDL_ShowSimpleMessageBox
    public static void showSimple(
            @MessageBoxFlags int flags,
            String title,
            String message,
            @Nullable Window window
    ) {
        try (var arena = Arena.ofConfined()) {
            var titleSegment = arena.allocateFrom(Objects.requireNonNull(title, "Title is required"));
            var messageSegment = arena.allocateFrom(Objects.requireNonNull(message, "Message is required"));
            check(SDL_ShowSimpleMessageBox(
                    flags,
                    titleSegment,
                    messageSegment,
                    window == null ? MemorySegment.NULL : window.segment()
            ));
        }
    }

    public static void showSimpleError(
            String title,
            String message
    ) {
        showSimple(MessageBoxFlags.ERROR, title, message, null);
    }
    public static void showSimpleWarning(
            String title,
            String message
    ) {
        showSimple(MessageBoxFlags.WARNING, title, message, null);
    }
    public static void showSimpleInfo(
            String title,
            String message
    ) {
        showSimple(MessageBoxFlags.INFORMATION, title, message, null);
    }

}
