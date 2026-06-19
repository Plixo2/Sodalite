package io.github.plixo2.sodalite.category.mouse;

import io.github.plixo2.sodalite.Internal;
import io.github.plixo2.sodalite.category.messagebox.MessageBoxFlags;
import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.libsdl.sdl.SDL3_h.*;


/// Annotation and values for a single mouse button.
/// See {@link MouseButtonFlags} for the bitmask.
///
/// @sdlAPI SDL_MouseButtonFlags
@MagicConstant(valuesFromClass = MouseButton.class)
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface MouseButton {

    int LEFT = SDL_BUTTON_LEFT;
    int MIDDLE = SDL_BUTTON_MIDDLE;
    int RIGHT = SDL_BUTTON_RIGHT;
    int X1 = SDL_BUTTON_X1;
    int X2 = SDL_BUTTON_X2;

}
