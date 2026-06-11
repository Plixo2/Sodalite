package io.github.plixo2.sodalite.category.gpu;

import org.libsdl.sdl.SDL_GPUColorTargetBlendState;

import java.lang.foreign.MemorySegment;

/// @apiNote SDL_GPUColorTargetBlendState
public record ColorTargetBlendState(
        BlendFactor srcColorBlendfactor,
        BlendFactor dstColorBlendfactor,
        BlendOp colorBlendOp,
        BlendFactor srcAlphaBlendfactor,
        BlendFactor dstAlphaBlendfactor,
        BlendOp alphaBlendOp,
        @ColorComponentWriteFlags int colorWriteMask,
        boolean enabled
) {

    public ColorTargetBlendState {
        if (colorWriteMask < 0 || colorWriteMask > 0b1111) {
            throw new IllegalArgumentException("colorWriteMask must be between 0 and 0b1111");
        }
    }

    public static ColorTargetBlendState of(
            BlendFactor srcColorBlendfactor,
            BlendFactor dstColorBlendfactor,
            BlendOp colorBlendOp,
            BlendFactor srcAlphaBlendfactor,
            BlendFactor dstAlphaBlendfactor,
            BlendOp alphaBlendOp,
            @ColorComponentWriteFlags int colorWriteMask
    ) {
        return new ColorTargetBlendState(
                srcColorBlendfactor,
                dstColorBlendfactor,
                colorBlendOp,
                srcAlphaBlendfactor,
                dstAlphaBlendfactor,
                alphaBlendOp,
                colorWriteMask,
                true
        );
    }

    public static ColorTargetBlendState disabled() {
        return new ColorTargetBlendState(
                BlendFactor.ONE,
                BlendFactor.ZERO,
                BlendOp.ADD,
                BlendFactor.ONE,
                BlendFactor.ZERO,
                BlendOp.ADD,
                ColorComponentWriteFlags.ALL,
                false
        );
    }

    public static ColorTargetBlendState standardAlphaBlend() {
        return new ColorTargetBlendState(
                BlendFactor.SRC_ALPHA,
                BlendFactor.ONE_MINUS_SRC_ALPHA,
                BlendOp.ADD,
                BlendFactor.ONE,
                BlendFactor.ONE_MINUS_SRC_ALPHA,
                BlendOp.ADD,
                ColorComponentWriteFlags.ALL,
                true
        );
    }

    void put(MemorySegment segment) {
        var writeMaskEnabled = this.colorWriteMask != ColorComponentWriteFlags.ALL;

        SDL_GPUColorTargetBlendState.initialize(
                segment,
                this.srcColorBlendfactor.code(),
                this.dstColorBlendfactor.code(),
                this.colorBlendOp.code(),
                this.srcAlphaBlendfactor.code(),
                this.dstAlphaBlendfactor.code(),
                this.alphaBlendOp.code(),
                (byte) this.colorWriteMask,
                this.enabled,
                writeMaskEnabled
        );
    }
}
