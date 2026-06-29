package io.github.plixo2.sodalite.category.mouse;

import io.github.plixo2.sodalite.memory.BitMask;
import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Annotation and values for a SINGLE mouse button.
/// See [MouseButton] for the individual button values
///
/// @see Mouse#isPressed
/// @see MouseButton
/// @sdlAPI SDL_MouseButtonFlags
@MagicConstant(flagsFromClass = MouseButton.class)
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface MouseButtonFlags {

    BitMask.Int MASK = BitMask.ofInt(MouseButton.class);

    // Flags are in the MouseButton class

}
