package io.github.plixo2.sodalite.category.gpu;

import org.libsdl.sdl.SDL_GPUDepthStencilState;

import java.lang.foreign.MemorySegment;

/// @apiNote SDL_GPUDepthStencilState
public record DepthStencilState(
    CompareOp compareOp,
    StencilOpState backStencilState,
    StencilOpState frontStencilState,
    byte compareMask,
    byte writeMask,
    DepthTest enableDepthTest,
    DepthWrite enableDepthWrite,
    StencilTest enableStencilTest
) {
    public static DepthStencilState of(
            CompareOp compareOp,
            DepthTest enableDepthTest,
            DepthWrite enableDepthWrite
    ) {
        return new DepthStencilState(
            compareOp,
            StencilOpState.of(),
            StencilOpState.of(),
            (byte) 0,
            (byte) 0,
            enableDepthTest,
            enableDepthWrite,
            StencilTest.DISABLED
        );
    }

    public static DepthStencilState disabled() {
        return new DepthStencilState(
            CompareOp.ALWAYS,
            StencilOpState.of(),
            StencilOpState.of(),
            (byte) 0,
            (byte) 0,
            DepthTest.DISABLED,
            DepthWrite.DISABLED,
            StencilTest.DISABLED
        );
    }

    void put(
            MemorySegment segment
    ) {
        var backStencilState = SDL_GPUDepthStencilState.back_stencil_state(segment);
        var frontStencilState = SDL_GPUDepthStencilState.front_stencil_state(segment);
        this.backStencilState.put(backStencilState);
        this.frontStencilState.put(frontStencilState);

        SDL_GPUDepthStencilState.initialize(
            segment,
            this.compareOp.code(),
            backStencilState,
            frontStencilState,
            this.compareMask,
            this.writeMask,
            this.enableDepthTest.value(),
            this.enableDepthWrite.value(),
            this.enableStencilTest.value()
        );
    }

}
