package io.github.plixo2.sodalite.category.init;

import io.github.plixo2.sodalite.Internal;
import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.libsdl.sdl.SDL3_h.*;

/// @sdlAPI SDL_InitFlags
@MagicConstant(flagsFromClass = InitFlags.class)
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface InitFlags {

    int AUDIO = SDL_INIT_AUDIO();
    int VIDEO = SDL_INIT_VIDEO();
    int JOYSTICK = SDL_INIT_JOYSTICK();
    int HAPTIC = SDL_INIT_HAPTIC();
    int GAMEPAD = SDL_INIT_GAMEPAD();
    int EVENTS = SDL_INIT_EVENTS();
    int SENSOR = SDL_INIT_SENSOR();
    int CAMERA = SDL_INIT_CAMERA();

    int MASK = Internal.flagMask(InitFlags.class);

}
