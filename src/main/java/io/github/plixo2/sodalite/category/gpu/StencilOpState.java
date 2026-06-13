package io.github.plixo2.sodalite.category.gpu;

import org.libsdl.sdl.SDL_GPUStencilOpState;

import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_GPUStencilOpState
public record StencilOpState(
        StencilOp failOp,
        StencilOp passOp,
        StencilOp depthFailOp,
        CompareOp compareOp
) {
    public static  StencilOpState of(
            StencilOp failOp,
            StencilOp passOp,
            StencilOp depthFailOp,
            CompareOp compareOp
    ) {
        return new StencilOpState(failOp, passOp, depthFailOp, compareOp);
    }
    public static StencilOpState of() {
        return new StencilOpState(
                StencilOp.INVALID,
                StencilOp.INVALID,
                StencilOp.INVALID,
                CompareOp.INVALID
        );
    }


    void put(MemorySegment segment) {
         SDL_GPUStencilOpState.initialize(
                    segment,
                    this.failOp.code(),
                    this.passOp.code(),
                    this.depthFailOp.code(),
                    this.compareOp.code()
         );
    }
}
