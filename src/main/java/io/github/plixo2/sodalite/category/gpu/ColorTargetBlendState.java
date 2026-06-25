package io.github.plixo2.sodalite.category.gpu;

import org.libsdl.sdl.SDL_GPUColorTargetBlendState;

import java.lang.foreign.MemorySegment;

import static io.github.plixo2.sodalite.Internal.assertU8;

/// @sdlAPI SDL_GPUColorTargetBlendState
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
                ColorComponentWriteFlags.RGBA,
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
                ColorComponentWriteFlags.RGBA,
                true
        );
    }

    void put(MemorySegment segment) {
        var writeMaskEnabled = this.colorWriteMask != ColorComponentWriteFlags.RGBA;

        SDL_GPUColorTargetBlendState.initialize(
                segment,
                this.srcColorBlendfactor.code(),
                this.dstColorBlendfactor.code(),
                this.colorBlendOp.code(),
                this.srcAlphaBlendfactor.code(),
                this.dstAlphaBlendfactor.code(),
                this.alphaBlendOp.code(),
                assertU8(this.colorWriteMask, "colorWriteMask"),
                this.enabled,
                writeMaskEnabled
        );
    }
}
