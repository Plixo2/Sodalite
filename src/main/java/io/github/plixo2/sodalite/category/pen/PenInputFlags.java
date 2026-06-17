package io.github.plixo2.sodalite.category.pen;

import io.github.plixo2.sodalite.Internal;
import io.github.plixo2.sodalite.category.mouse.MouseButton;
import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.libsdl.sdl.SDL3_h.*;

/// @sdlAPI SDL_PenInputFlags
@MagicConstant(flagsFromClass = PenInputFlags.class)
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface PenInputFlags {

    int DOWN = SDL_PEN_INPUT_DOWN();
    int BUTTON_1 = SDL_PEN_INPUT_BUTTON_1();
    int BUTTON_2 = SDL_PEN_INPUT_BUTTON_2();
    int BUTTON_3 = SDL_PEN_INPUT_BUTTON_3();
    int BUTTON_4 = SDL_PEN_INPUT_BUTTON_4();
    int BUTTON_5 = SDL_PEN_INPUT_BUTTON_5();
    int ERASER_TIP = SDL_PEN_INPUT_ERASER_TIP();
    int IN_PROXIMITY = SDL_PEN_INPUT_IN_PROXIMITY();

    int MASK = Internal.flagMask(PenInputFlags.class);

}
