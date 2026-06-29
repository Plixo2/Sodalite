package io.github.plixo2.sodalite.category.gpu;


import io.github.plixo2.sodalite.memory.BitMask;
import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.libsdl.sdl.SDL3_h.*;

/// @sdlAPI SDL_GPUTextureUsageFlags
@MagicConstant(flagsFromClass = TextureUsageFlags.class)
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface TextureUsageFlags {

    int SAMPLER = SDL_GPU_TEXTUREUSAGE_SAMPLER;
    int COLOR_TARGET = SDL_GPU_TEXTUREUSAGE_COLOR_TARGET;
    int DEPTH_STENCIL_TARGET = SDL_GPU_TEXTUREUSAGE_DEPTH_STENCIL_TARGET;
    int GRAPHICS_STORAGE_READ = SDL_GPU_TEXTUREUSAGE_GRAPHICS_STORAGE_READ;
    int COMPUTE_STORAGE_READ = SDL_GPU_TEXTUREUSAGE_COMPUTE_STORAGE_READ;
    int COMPUTE_STORAGE_WRITE = SDL_GPU_TEXTUREUSAGE_COMPUTE_STORAGE_WRITE;
    int COMPUTE_STORAGE_SIMULTANEOUS_READ_WRITE = SDL_GPU_TEXTUREUSAGE_COMPUTE_STORAGE_SIMULTANEOUS_READ_WRITE;

    BitMask.Int MASK = BitMask.ofInt(TextureUsageFlags.class);

}
