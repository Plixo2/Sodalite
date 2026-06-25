package io.github.plixo2.sodalite.category.gpu;

import org.jetbrains.annotations.Nullable;
import org.libsdl.sdl.SDL_GPUColorTargetDescription;
import org.libsdl.sdl.SDL_GPUGraphicsPipelineTargetInfo;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/// @sdlAPI SDL_GPUGraphicsPipelineTargetInfo
public record GraphicsPipelineTargetInfo(
    @Nullable TextureFormat depthStencilFormat,
    List<ColorTargetDescription> colorTargetDescriptions
) {
    public static GraphicsPipelineTargetInfo of() {
        return new GraphicsPipelineTargetInfo(null, List.of());
    }

    public static GraphicsPipelineTargetInfo of(
            @Nullable TextureFormat depthStencilFormat,
            List<ColorTargetDescription> colorTargetDescriptions
    ) {
        return new GraphicsPipelineTargetInfo(depthStencilFormat, colorTargetDescriptions);
    }
    public static GraphicsPipelineTargetInfo of(
            ColorTargetDescription... colorTargetDescriptions
    ) {
        return new GraphicsPipelineTargetInfo(null, List.of(colorTargetDescriptions));
    }
    public static GraphicsPipelineTargetInfo of(
            @Nullable TextureFormat depthStencilFormat,
            ColorTargetDescription fst,
            ColorTargetDescription... rest
    ) {
        var list = new ArrayList<ColorTargetDescription>(rest.length + 1);
        list.add(fst);
        Collections.addAll(list, rest);
        return new GraphicsPipelineTargetInfo(depthStencilFormat, list);
    }
    public static GraphicsPipelineTargetInfo of(
            @Nullable TextureFormat depthStencilFormat,
            ColorTargetDescription[] colorTargetDescriptions
    ) {
        return new GraphicsPipelineTargetInfo(depthStencilFormat, List.of(colorTargetDescriptions));
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
