package io.github.plixo2.sodalite.category.video;

import io.github.plixo2.sodalite.memory.BitMask;
import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.libsdl.sdl.SDL3_h.*;


/// @sdlAPI SDL_GLProfile
@MagicConstant(flagsFromClass = GLProfile.class)
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface GLProfile {

    int NONE = 0;
    int CORE = SDL_GL_CONTEXT_PROFILE_CORE;
    int COMPATIBILITY = SDL_GL_CONTEXT_PROFILE_COMPATIBILITY;
    int ES = SDL_GL_CONTEXT_PROFILE_ES;

    BitMask.Int MASK = BitMask.ofInt(GLProfile.class);

}
