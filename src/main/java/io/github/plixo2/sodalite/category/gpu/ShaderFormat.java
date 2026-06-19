package io.github.plixo2.sodalite.category.gpu;

import io.github.plixo2.sodalite.Internal;
import io.github.plixo2.sodalite.category.pen.PenInputFlags;
import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.libsdl.sdl.SDL3_h.*;

/// @sdlAPI SDL_GPUShaderFormat
@MagicConstant(flagsFromClass = ShaderFormat.class)
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface ShaderFormat {

    int INVALID = SDL_GPU_SHADERFORMAT_INVALID;
    int PRIVATE = SDL_GPU_SHADERFORMAT_PRIVATE;
    int SPIRV = SDL_GPU_SHADERFORMAT_SPIRV;
    int DXBC = SDL_GPU_SHADERFORMAT_DXBC;
    int DXIL = SDL_GPU_SHADERFORMAT_DXIL;
    int MSL = SDL_GPU_SHADERFORMAT_MSL;
    int METALLIB = SDL_GPU_SHADERFORMAT_METALLIB;

    int MASK = Internal.flagMask(ShaderFormat.class);

}
