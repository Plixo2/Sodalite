package io.github.plixo2.sodalite.category.video;

import io.github.plixo2.sodalite.memory.BitMask;
import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.libsdl.sdl.SDL3_h.*;


/// @sdlAPI SDL_GLContextFlag
@MagicConstant(valuesFromClass = GLContextFlag.class)
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface GLContextFlag {

    int NONE = 0;
    int DEBUG = SDL_GL_CONTEXT_DEBUG_FLAG;
    int FORWARD_COMPATIBLE = SDL_GL_CONTEXT_FORWARD_COMPATIBLE_FLAG;
    int ROBUST_ACCESS = SDL_GL_CONTEXT_ROBUST_ACCESS_FLAG;
    int RESET_ISOLATION = SDL_GL_CONTEXT_RESET_ISOLATION_FLAG;

    BitMask.Int MASK = BitMask.ofInt(GLContextFlag.class);

}
