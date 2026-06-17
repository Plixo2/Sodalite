package io.github.plixo2.sodalite.category.gpu;

import lombok.Getter;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/// return value for `SDL_GetGPUDeviceDriver`,
/// keys for `SDL_CreateGPUDevice` and `SDL_GPUSupportsShaderFormats`
public class GPUDriver {

    @Getter
    private final String internalName;

    GPUDriver(String internalName) {
        this.internalName = internalName;
    }

    MemorySegment nameSegment(Arena arena) {
        if (this.internalName.equals("unknown")) {
            return MemorySegment.NULL;
        }
        return arena.allocateFrom(this.internalName);
    }

    public boolean supportsShaderFormats(
            @ShaderFormat int shaderFormats
    ) {
        return GPU.supportsShaderFormats(shaderFormats, this);
    }


    public static GPUDriver vulkan() {
        return new GPUDriver("vulkan");
    }
    public static GPUDriver metal() {
        return new GPUDriver("metal");
    }
    public static GPUDriver direct3D12() {
        return new GPUDriver("direct3d12");
    }
    public static GPUDriver optimal() {
        return new GPUDriver("unknown");
    }
    public static GPUDriver ofUnknown(String internalName) {
        return new GPUDriver(internalName);
    }


}
