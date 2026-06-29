package io.github.plixo2.sodalite.category.video;


import io.github.plixo2.sodalite.memory.BitMask;
import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.libsdl.sdl.SDL3_h.*;

/// @sdlAPI SDL_GLContextReleaseFlag
@MagicConstant(valuesFromClass = GLContextReleaseFlag.class)
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface GLContextReleaseFlag {

    int NONE = SDL_GL_CONTEXT_RELEASE_BEHAVIOR_NONE;
    int FLUSH = SDL_GL_CONTEXT_RELEASE_BEHAVIOR_FLUSH;

    BitMask.Int MASK = BitMask.ofInt(GLContextReleaseFlag.class);

}
