package io.github.plixo2.sodalite.category.gpu;

import io.github.plixo2.sodalite.Internal;
import io.github.plixo2.sodalite.category.init.InitFlags;
import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


import static org.libsdl.sdl.SDL3_h.*;

/// @sdlAPI SDL_GPUBufferUsageFlags
@MagicConstant(flagsFromClass = BufferUsageFlags.class)
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface BufferUsageFlags {

    int VERTEX = SDL_GPU_BUFFERUSAGE_VERTEX();
    int INDEX = SDL_GPU_BUFFERUSAGE_INDEX();
    int INDIRECT = SDL_GPU_BUFFERUSAGE_INDIRECT();
    int GRAPHICS_STORAGE_READ = SDL_GPU_BUFFERUSAGE_GRAPHICS_STORAGE_READ();
    int COMPUTE_STORAGE_READ = SDL_GPU_BUFFERUSAGE_COMPUTE_STORAGE_READ();
    int COMPUTE_STORAGE_WRITE = SDL_GPU_BUFFERUSAGE_COMPUTE_STORAGE_WRITE();

    int MASK = Internal.flagMask(BufferUsageFlags.class);

}
