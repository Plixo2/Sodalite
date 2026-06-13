package io.github.plixo2.sodalite.category.mouse;

import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.libsdl.sdl.SDL3_h.*;

/// Annotation for the bitmask of {@link MouseButton} values.
///
/// @sdlAPI SDL_MouseButtonFlags
@MagicConstant(flagsFromClass = MouseButton.class)
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface MouseButtonFlags {


}
