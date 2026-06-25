package io.github.plixo2.sodalite.category.keycode;


import io.github.plixo2.sodalite.memory.BitMask;
import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.libsdl.sdl.SDL3_h.*;

/// @sdlAPI SDL_Keymod
@MagicConstant(flagsFromClass = Keymod.class)
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface Keymod {

    int NONE = SDL_KMOD_NONE;
    int LSHIFT = SDL_KMOD_LSHIFT;
    int RSHIFT = SDL_KMOD_RSHIFT;
    int LEVEL5 = SDL_KMOD_LEVEL5;
    int LCTRL = SDL_KMOD_LCTRL;
    int RCTRL = SDL_KMOD_RCTRL;
    int LALT = SDL_KMOD_LALT;
    int RALT = SDL_KMOD_RALT;
    int LGUI = SDL_KMOD_LGUI;
    int RGUI = SDL_KMOD_RGUI;
    int NUM = SDL_KMOD_NUM;
    int CAPS = SDL_KMOD_CAPS;
    int MODE = SDL_KMOD_MODE;
    int SCROLL = SDL_KMOD_SCROLL;
    int CTRL = SDL_KMOD_CTRL;
    int SHIFT = SDL_KMOD_SHIFT;
    int ALT = SDL_KMOD_ALT;
    int GUI = SDL_KMOD_GUI;

    int MASK = BitMask.flagMaskInt(Keymod.class);

}
