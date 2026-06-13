package io.github.plixo2.sodalite.category.tray;


import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


import static org.libsdl.sdl.SDL3_h.*;

/// @sdlAPI SDL_TrayEntryFlags
@MagicConstant(flagsFromClass = TrayEntryFlags.class)
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface TrayEntryFlags {

    int BUTTON = SDL_TRAYENTRY_BUTTON();
    int CHECKBOX = SDL_TRAYENTRY_CHECKBOX();
    int SUBMENU = SDL_TRAYENTRY_SUBMENU();
    int DISABLED = SDL_TRAYENTRY_DISABLED();
    int CHECKED = SDL_TRAYENTRY_CHECKED();

}
