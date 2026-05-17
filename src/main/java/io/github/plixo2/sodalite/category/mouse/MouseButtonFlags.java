package io.github.plixo2.sodalite.category.mouse;

import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.libsdl.sdl.SDL3_h.*;

/// @apiNote SDL_MouseButtonFlags
@MagicConstant(flagsFromClass = MouseButtonFlags.class)
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface MouseButtonFlags {

    int LEFT = SDL_BUTTON_LEFT();
    int MIDDLE = SDL_BUTTON_MIDDLE();
    int RIGHT = SDL_BUTTON_RIGHT();
    int X1 = SDL_BUTTON_X1();
    int X2 = SDL_BUTTON_X2();
}
