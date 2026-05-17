package io.github.plixo2.sodalite.category.video;

import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.libsdl.sdl.SDL3_h.*;


/// @apiNote SDL_WindowFlags
@MagicConstant(flagsFromClass = WindowFlags.class)
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface WindowFlags {

    long NONE = 0L;
    long FULLSCREEN = SDL_WINDOW_FULLSCREEN();
    long OPENGL = SDL_WINDOW_OPENGL();
    long OCCLUDED = SDL_WINDOW_OCCLUDED();
    long HIDDEN = SDL_WINDOW_HIDDEN();
    long BORDERLESS = SDL_WINDOW_BORDERLESS();
    long RESIZABLE = SDL_WINDOW_RESIZABLE();
    long MINIMIZED = SDL_WINDOW_MINIMIZED();
    long MAXIMIZED = SDL_WINDOW_MAXIMIZED();
    long MOUSE_GRABBED = SDL_WINDOW_MOUSE_GRABBED();
    long INPUT_FOCUS = SDL_WINDOW_INPUT_FOCUS();
    long MOUSE_FOCUS = SDL_WINDOW_MOUSE_FOCUS();
    long EXTERNAL = SDL_WINDOW_EXTERNAL();
    long MODAL = SDL_WINDOW_MODAL();
    long HIGH_PIXEL_DENSITY = SDL_WINDOW_HIGH_PIXEL_DENSITY();
    long MOUSE_CAPTURE = SDL_WINDOW_MOUSE_CAPTURE();
    long MOUSE_RELATIVE_MODE = SDL_WINDOW_MOUSE_RELATIVE_MODE();
    long ALWAYS_ON_TOP = SDL_WINDOW_ALWAYS_ON_TOP();
    long UTILITY = SDL_WINDOW_UTILITY();
    long TOOLTIP = SDL_WINDOW_TOOLTIP();
    long POPUP_MENU = SDL_WINDOW_POPUP_MENU();
    long KEYBOARD_GRABBED = SDL_WINDOW_KEYBOARD_GRABBED();
    long FILL_DOCUMENT = SDL_WINDOW_FILL_DOCUMENT();
    long VULKAN = SDL_WINDOW_VULKAN();
    long METAL = SDL_WINDOW_METAL();
    long TRANSPARENT = SDL_WINDOW_TRANSPARENT();
    long NOT_FOCUSABLE = SDL_WINDOW_NOT_FOCUSABLE();

}
