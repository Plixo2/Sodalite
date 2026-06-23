package io.github.plixo2.sodalite.category.messagebox;


import io.github.plixo2.sodalite.Internal;
import io.github.plixo2.sodalite.category.keycode.Keymod;
import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.libsdl.sdl.SDL3_h.*;

/// @sdlAPI SDL_MessageBoxButtonFlags
@MagicConstant(flagsFromClass = MessageBoxButtonFlags.class)
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface MessageBoxButtonFlags {

    int NONE = 0;
    int RETURN_KEY_DEFAULT = SDL_MESSAGEBOX_BUTTON_RETURNKEY_DEFAULT();
    int ESCAPE_KEY_DEFAULT = SDL_MESSAGEBOX_BUTTON_ESCAPEKEY_DEFAULT();

    int MASK = Internal.flagMask(MessageBoxButtonFlags.class);

}
