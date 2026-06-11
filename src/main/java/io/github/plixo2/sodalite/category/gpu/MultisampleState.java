package io.github.plixo2.sodalite.category.gpu;

import jdk.jfr.Enabled;
import org.libsdl.sdl.SDL_GPUMultisampleState;

import java.lang.foreign.MemorySegment;

/// @apiNote SDL_GPUMultisampleState
public record MultisampleState(
        SampleCount sampleCount,
        boolean enableAlphaToCoverage
) {
    public static MultisampleState enabled(SampleCount sampleCount, boolean enableAlphaToCoverage) {
        return new MultisampleState(sampleCount, enableAlphaToCoverage);
    }
    public static MultisampleState enabled(SampleCount sampleCount) {
        return new MultisampleState(sampleCount, false);
    }
    public static MultisampleState disabled() {
        return new MultisampleState(SampleCount.COUNT_1, false);
    }


    void put(
            MemorySegment segment
    ) {
        SDL_GPUMultisampleState.initialize(
                segment,
                this.sampleCount.code(),
                0,
                false,
                this.enableAlphaToCoverage
        );
    }
}
