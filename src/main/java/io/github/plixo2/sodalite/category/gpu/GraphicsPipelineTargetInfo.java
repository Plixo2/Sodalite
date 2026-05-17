package io.github.plixo2.sodalite.category.gpu;

import org.jetbrains.annotations.Nullable;
import org.libsdl.sdl.SDL_GPUColorTargetDescription;
import org.libsdl.sdl.SDL_GPUGraphicsPipelineTargetInfo;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.util.List;

/// @apiNote SDL_GPUGraphicsPipelineTargetInfo
public record GraphicsPipelineTargetInfo(
    List<ColorTargetDescription> colorTargetDescriptions,
    @Nullable TextureFormat depthStencilFormat
) {
    public static GraphicsPipelineTargetInfo of() {
        return new GraphicsPipelineTargetInfo(List.of(), null);
    }

    public static GraphicsPipelineTargetInfo of(
            List<ColorTargetDescription> colorTargetDescriptions
    ) {
        return new GraphicsPipelineTargetInfo(colorTargetDescriptions, null);
    }
    public static GraphicsPipelineTargetInfo of(
            ColorTargetDescription... colorTargetDescriptions
    ) {
        return new GraphicsPipelineTargetInfo(List.of(colorTargetDescriptions), null);
    }
    public static GraphicsPipelineTargetInfo of(
            @Nullable TextureFormat depthStencilFormat,
            ColorTargetDescription... colorTargetDescriptions
    ) {
        return new GraphicsPipelineTargetInfo(List.of(colorTargetDescriptions), depthStencilFormat);
    }

    public static GraphicsPipelineTargetInfo of(
            TextureFormat format,
            ColorTargetBlendState blendState
    ) {
       return GraphicsPipelineTargetInfo.of(ColorTargetDescription.of(format, blendState));
    }
    public static GraphicsPipelineTargetInfo of(
            TextureFormat format1,
            ColorTargetBlendState blendState1,
            TextureFormat format2,
            ColorTargetBlendState blendState2
    ) {
        return GraphicsPipelineTargetInfo.of(
                ColorTargetDescription.of(format1, blendState1),
                ColorTargetDescription.of(format2, blendState2)
        );
    }
    public static GraphicsPipelineTargetInfo of(
            TextureFormat format1,
            ColorTargetBlendState blendState1,
            TextureFormat format2,
            ColorTargetBlendState blendState2,
            TextureFormat format3,
            ColorTargetBlendState blendState3
    ) {
        return GraphicsPipelineTargetInfo.of(
                ColorTargetDescription.of(format1, blendState1),
                ColorTargetDescription.of(format2, blendState2),
                ColorTargetDescription.of(format3, blendState3)
        );
    }

    void put(SegmentAllocator arena, MemorySegment segment) {
        var colorTargetDescriptions = SDL_GPUColorTargetDescription
                .allocateArray(this.colorTargetDescriptions.size(), arena);

        for (var i = 0; i < this.colorTargetDescriptions.size(); i++) {
            var colorTargetSegment = SDL_GPUColorTargetDescription.asSlice(colorTargetDescriptions, i);
            var colorTargetDescription = this.colorTargetDescriptions.get(i);
            var blendstate = SDL_GPUColorTargetDescription.blend_state(colorTargetSegment);
            colorTargetDescription.blendState().put(blendstate);
            SDL_GPUColorTargetDescription.initialize(
                    colorTargetSegment,
                    colorTargetDescription.format().code(),
                    blendstate
            );
        }

        var has_depth_stencil_target = this.depthStencilFormat != null;
        TextureFormat depthStencilFormat = this.depthStencilFormat == null ? TextureFormat.INVALID : this.depthStencilFormat;

        SDL_GPUGraphicsPipelineTargetInfo.initialize(
                segment,
                colorTargetDescriptions,
                this.colorTargetDescriptions.size(),
                depthStencilFormat.code(),
                has_depth_stencil_target
        );
    }


}
