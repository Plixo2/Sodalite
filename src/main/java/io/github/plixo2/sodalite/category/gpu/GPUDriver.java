package io.github.plixo2.sodalite.category.gpu;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/// return value for `SDL_GetGPUDeviceDriver`,
/// keys for `SDL_CreateGPUDevice` and `SDL_GPUSupportsShaderFormats`
@EqualsAndHashCode
public class GPUDriver {

    @Getter
    private final String name;

    GPUDriver(String name) {
        this.name = name;
    }

    MemorySegment nameSegment(Arena arena) {
        if (this.name.equals("unknown")) {
            return MemorySegment.NULL;
        }
        return arena.allocateFrom(this.name);
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

    @Override
    public String toString() {
        return "GPUDriver(" + this.name + ")";
    }
}
