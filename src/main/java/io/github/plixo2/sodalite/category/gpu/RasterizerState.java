package io.github.plixo2.sodalite.category.gpu;

import org.libsdl.sdl.SDL_GPURasterizerState;

import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_GPURasterizerState
public record RasterizerState(
        FillMode fillMode,
        CullMode cullMode,
        FrontFace frontFace,
        float depthBiasConstantFactor,
        float depthBiasClamp,
        float depthBiasSlopeFactor,
        boolean depthBiasEnable,
        boolean enableDepthClip
) {
    public RasterizerState(
            FillMode fillMode,
            CullMode cullMode,
            FrontFace frontFace
    ) {
        this(
            fillMode,
            cullMode,
            frontFace,
            0,
            0,
            0,
            false,
            false
        );
    }

    public static RasterizerState defaultValue() {
        return new RasterizerState(
                FillMode.FILL,
                CullMode.BACK,
                FrontFace.DEFAULT()
        );
    }

    public static RasterizerState of(
            FillMode fillMode,
            CullMode cullMode,
            FrontFace frontFace
    ) {
        return new RasterizerState(fillMode, cullMode, frontFace);
    }


    void put(MemorySegment segment) {
        SDL_GPURasterizerState.initialize(
                segment,
                this.fillMode.code(),
                this.cullMode.code(),
                this.frontFace.code(),
                this.depthBiasConstantFactor,
                this.depthBiasClamp,
                this.depthBiasSlopeFactor,
                this.depthBiasEnable,
                this.enableDepthClip
        );
    }

}
