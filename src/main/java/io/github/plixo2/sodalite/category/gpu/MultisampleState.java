package io.github.plixo2.sodalite.category.gpu;

import org.libsdl.sdl.SDL_GPUMultisampleState;

import java.lang.foreign.MemorySegment;

/// @apiNote SDL_GPUMultisampleState
public sealed interface MultisampleState {

    record Enabled(SampleCount sampleCount, boolean enableAlphaToCoverage) implements MultisampleState {}
    record Disabled() implements MultisampleState {}

    static MultisampleState enabled(SampleCount sampleCount, boolean enableAlphaToCoverage) {
        return new Enabled(sampleCount, enableAlphaToCoverage);
    }
    static MultisampleState disabled() {
        return new Disabled();
    }

    /// @apiNote SDL_GPUSampleCount
    enum SampleCount {
        SAMPLECOUNT_1,
        SAMPLECOUNT_2,
        SAMPLECOUNT_4,
        SAMPLECOUNT_8,

        ;

        public int code() {
            return this.ordinal();
        }

        /// moved into here for package private access
        static void put(
                MemorySegment segment,
                MultisampleState state
        ) {
            switch (state) {
                case Disabled _ -> {
                    SDL_GPUMultisampleState.initialize(
                            segment,
                            SAMPLECOUNT_1.code(),
                            0,
                            false,
                            false
                    );
                }
                case Enabled enabled -> {
                    SDL_GPUMultisampleState.initialize(
                            segment,
                            enabled.sampleCount.code(),
                            0,
                            false,
                            enabled.enableAlphaToCoverage
                    );
                }

            }


        }
    }

}
