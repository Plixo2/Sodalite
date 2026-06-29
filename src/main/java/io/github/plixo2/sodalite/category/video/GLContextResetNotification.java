package io.github.plixo2.sodalite.category.video;


import io.github.plixo2.sodalite.memory.BitMask;
import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.libsdl.sdl.SDL3_h.*;

/// @sdlAPI SDL_GLContextResetNotification
@MagicConstant(valuesFromClass = GLContextResetNotification.class)
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface GLContextResetNotification {

    int NO_NOTIFICATION = SDL_GL_CONTEXT_RESET_NO_NOTIFICATION;
    int LOSE_CONTEXT = SDL_GL_CONTEXT_RESET_LOSE_CONTEXT;

    BitMask.Int MASK = BitMask.ofInt(GLContextResetNotification.class);

}
