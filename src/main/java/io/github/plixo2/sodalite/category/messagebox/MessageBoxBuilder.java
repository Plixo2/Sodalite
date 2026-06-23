package io.github.plixo2.sodalite.category.messagebox;

import io.github.plixo2.sodalite.category.video.Window;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/// Builder for [MessageBoxData] (`SDL_MessageBoxData`)
/// @see MessageBoxData
@ToString
@EqualsAndHashCode
public class MessageBoxBuilder {
    private @MessageBoxFlags int flags;
    private final @Nullable Window window;
    private final String title;
    private final String message;
    private final List<MessageBoxButtonData> buttons;
    private @Nullable MessageBoxColorScheme colorScheme;

    private final List<Runnable> actions;

    private MessageBoxBuilder(
            String title,
            String message,
            @Nullable Window window,
            @MessageBoxFlags int flags
    ) {
        this.flags = flags;
        this.window = window;
        this.title = title;
        this.message = message;
        this.buttons = new ArrayList<>();
        this.colorScheme = null;
        this.actions = new ArrayList<>();
    }

    public static MessageBoxBuilder error(String title, String message) {
        return error(title, message, null);
    }
    public static MessageBoxBuilder warning(String title, String message) {
        return warning(title, message, null);
    }
    public static MessageBoxBuilder info(String title, String message) {
        return info(title, message, null);
    }

    public static MessageBoxBuilder error(String title, String message, @Nullable Window window) {
        return new MessageBoxBuilder(title, message, window, MessageBoxFlags.ERROR);
    }
    public static MessageBoxBuilder warning(String title, String message, @Nullable Window window) {
        return new MessageBoxBuilder(title, message, window, MessageBoxFlags.WARNING);
    }
    public static MessageBoxBuilder info(String title, String message, @Nullable Window window) {
        return new MessageBoxBuilder(title, message, window, MessageBoxFlags.INFORMATION);
    }


    public MessageBoxBuilder leftToRight() {
        // remove BUTTONS_RIGHT_TO_LEFT flags and add BUTTONS_LEFT_TO_RIGHT flag
        this.flags = (this.flags & ~MessageBoxFlags.BUTTONS_RIGHT_TO_LEFT) | MessageBoxFlags.BUTTONS_LEFT_TO_RIGHT;
        return this;
    }
    public MessageBoxBuilder rightToLeft() {
        // remove BUTTONS_LEFT_TO_RIGHT flags and add BUTTONS_RIGHT_TO_LEFT flag
        this.flags = (this.flags & ~MessageBoxFlags.BUTTONS_LEFT_TO_RIGHT) | MessageBoxFlags.BUTTONS_RIGHT_TO_LEFT;
        return this;
    }

    public MessageBoxBuilder addButton(String text) {
        return addButton(text, MessageBoxButtonFlags.NONE);
    }
    public MessageBoxBuilder addButton(String text, @MessageBoxButtonFlags int flags) {
        return addButton(text, flags, () -> {});
    }
    public MessageBoxBuilder addButton(String text, Runnable runnable) {
        return addButton(text, MessageBoxButtonFlags.NONE, runnable);
    }
    public MessageBoxBuilder addButton(String text, @MessageBoxButtonFlags int flags, Runnable runnable) {
        var id = this.buttons.size();
        this.buttons.add(new MessageBoxButtonData(flags, id, text));
        this.actions.add(runnable);
        return this;
    }

    public MessageBoxBuilder theme(MessageBoxColorScheme colorScheme) {
        this.colorScheme = colorScheme;
        return this;
    }

    /// @return the id of the pressed button
    public int show() {
        return MessageBox.show(this.build());
    }

    public MessageBoxData build() {
        return new MessageBoxData(
                this.flags,
                this.window,
                this.title,
                this.message,
                this.buttons,
                this.colorScheme,
                this::handleButtonPress
        );
    }
    private void handleButtonPress(int id) {
        if (id < 0 || id >= this.actions.size()) {
            return;
        }
        var runnable = this.actions.get(id);
        runnable.run();
    }


}
