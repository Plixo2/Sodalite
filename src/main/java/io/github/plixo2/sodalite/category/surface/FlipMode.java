package io.github.plixo2.sodalite.category.surface;


import io.github.plixo2.sodalite.memory.BitMask;
import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.libsdl.sdl.SDL3_h.*;

/// @sdlAPI SDL_FlipMode
@MagicConstant(flagsFromClass = FlipMode.class)
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface FlipMode {

    int NONE = SDL_FLIP_NONE;
    int HORIZONTAL = SDL_FLIP_HORIZONTAL;
    int VERTICAL = SDL_FLIP_VERTICAL;

    int MASK = BitMask.flagMaskInt(FlipMode.class);

}
