package io.github.plixo2.sodalite.category.messagebox;


import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.libsdl.sdl.SDL3_h.*;

/// @sdlAPI SDL_MessageBoxFlags
@MagicConstant(flagsFromClass = MessageBoxFlags.class)
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface MessageBoxFlags {

    int ERROR = SDL_MESSAGEBOX_ERROR();
    int WARNING = SDL_MESSAGEBOX_WARNING();
    int INFORMATION = SDL_MESSAGEBOX_INFORMATION();
    int BUTTONS_LEFT_TO_RIGHT = SDL_MESSAGEBOX_BUTTONS_LEFT_TO_RIGHT();
    int BUTTONS_RIGHT_TO_LEFT = SDL_MESSAGEBOX_BUTTONS_RIGHT_TO_LEFT();
}
