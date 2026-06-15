package io.github.plixo2.sodalite.category.gpu;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;


/// Keys for `SDL_CreateGPUDevice`
public enum PreferredDriver {

    VULKAN,
    METAL,
    DIRECT3D12,
    OPTIMAL,

    ;

    private static final MemorySegment VULKAN_STRING = Arena.global().allocateFrom("vulkan");
    private static final MemorySegment METAL_STRING = Arena.global().allocateFrom("metal");
    private static final MemorySegment DX12_STRING = Arena.global().allocateFrom("direct3d12");
    private static final MemorySegment OPTIMAL_STRING = MemorySegment.NULL;

    MemorySegment stringSegment() {
        return switch (this) {
            case VULKAN -> VULKAN_STRING;
            case METAL -> METAL_STRING;
            case DIRECT3D12 -> DX12_STRING;
            case OPTIMAL -> OPTIMAL_STRING;
        };
    }
}
