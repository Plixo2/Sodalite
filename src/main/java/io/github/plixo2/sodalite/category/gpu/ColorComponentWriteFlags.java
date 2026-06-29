package io.github.plixo2.sodalite.category.gpu;


import io.github.plixo2.sodalite.memory.BitMask;
import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.libsdl.sdl.SDL3_h.*;

/// @sdlAPI SDL_GPUColorComponentFlags
@MagicConstant(flagsFromClass = ColorComponentWriteFlags.class)
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface ColorComponentWriteFlags {

    int R = SDL_GPU_COLORCOMPONENT_R;
    int G = SDL_GPU_COLORCOMPONENT_G;
    int B = SDL_GPU_COLORCOMPONENT_B;
    int A = SDL_GPU_COLORCOMPONENT_A;

    int RGBA = R | G | B | A;

    BitMask.Int MASK = BitMask.ofInt(ColorComponentWriteFlags.class);

}
