package io.github.plixo2.sodalite.category.video;

import io.github.plixo2.sodalite.category.pixels.PixelFormat;
import lombok.*;
import org.libsdl.sdl.SDL_DisplayMode;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_DisplayMode

@EqualsAndHashCode
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DisplayMode {

    @Getter @Setter private DisplayID id;
    @Getter @Setter private PixelFormat pixelFormat;
    @Getter @Setter private int width;
    @Getter @Setter private int height;
    @Getter @Setter private float pixelDensity;
    @Getter @Setter private float refreshRate;
    @Getter @Setter private int refreshRateNumerator;
    @Getter @Setter private int refreshRateDenominator;

    /// @sdlOther SDL_DisplayModeData
    private MemorySegment internal;

    public static DisplayMode of(
            DisplayID id,
            PixelFormat pixelFormat,
            int width,
            int height,
            float pixelDensity,
            float refreshRate,
            int refreshRateNumerator,
            int refreshRateDenominator
    ) {
        return new DisplayMode(
                id,
                pixelFormat,
                width,
                height,
                pixelDensity,
                refreshRate,
                refreshRateNumerator,
                refreshRateDenominator,
                MemorySegment.NULL
        );
    }

    static DisplayMode of(MemorySegment segment) {
        return new DisplayMode(
                DisplayID.of(SDL_DisplayMode.displayID(segment)),
                PixelFormat.fromCode(SDL_DisplayMode.format(segment)),
                SDL_DisplayMode.w(segment),
                SDL_DisplayMode.h(segment),
                SDL_DisplayMode.pixel_density(segment),
                SDL_DisplayMode.refresh_rate(segment),
                SDL_DisplayMode.refresh_rate_numerator(segment),
                SDL_DisplayMode.refresh_rate_denominator(segment),
                SDL_DisplayMode.internal(segment)
        );
    }


    MemorySegment put(Arena arena) {
        return SDL_DisplayMode.create(
                arena,
                this.id.value(),
                this.pixelFormat.code(),
                this.width,
                this.height,
                this.pixelDensity,
                this.refreshRate,
                this.refreshRateNumerator,
                this.refreshRateDenominator,
                this.internal
        );
    }

}
