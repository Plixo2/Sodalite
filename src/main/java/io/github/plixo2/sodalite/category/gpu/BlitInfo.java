package io.github.plixo2.sodalite.category.gpu;

import io.github.plixo2.sodalite.category.surface.FlipMode;
import lombok.Setter;
import org.joml.Vector4f;
import org.libsdl.sdl.SDL_FColor;
import org.libsdl.sdl.SDL_GPUBlitInfo;
import org.libsdl.sdl.SDL_GPUBlitRegion;

import java.lang.foreign.MemorySegment;


/// @sdlAPI SDL_GPUBlitInfo
public class BlitInfo {

    private final Region source;
    private final Region destination;
    private final Cycle cycle;

    private final Vector4f clearColor;
    private final LoadOp loadOp;

    @Setter
    private @FlipMode int flipMode = FlipMode.NONE;

    private Filter filter = Filter.NEAREST;

    private BlitInfo(
            Region source,
            Region destination,
            Cycle cycle,
            Vector4f clearColor,
            LoadOp loadOp
    ) {
        this.source = source;
        this.destination = destination;
        this.cycle = cycle;
        this.clearColor = clearColor;
        this.loadOp = loadOp;
    }

    public static BlitInfo clearDestination(
            Region source,
            Region destination,
            Vector4f clearColor,
            Cycle cycle
    ) {
        return new BlitInfo(
                source,
                destination,
                cycle,
                clearColor,
                LoadOp.CLEAR
        );
    }

    public static BlitInfo of(
            Region source,
            Region destination,
            Cycle cycle
    ) {
        return new BlitInfo(
                source,
                destination,
                cycle,
                new Vector4f(0, 0, 0, 0),
                LoadOp.DONT_CARE
        );
    }

    public static BlitInfo of(
            Region source,
            Region destination,
            Vector4f clearColor,
            LoadOp loadOp,
            Cycle cycle
    ) {
        return new BlitInfo(
                source,
                destination,
                cycle,
                clearColor,
                loadOp
        );
    }


    public BlitInfo setFilter(Filter filter) {
        this.filter = filter;
        return this;
    }

    public BlitInfo setFlipMode(@FlipMode int flipMode) {
        this.flipMode = flipMode;
        return this;
    }
    public BlitInfo flipHorizontal() {
        this.flipMode |= FlipMode.HORIZONTAL;
        return this;
    }
    public BlitInfo flipVertical() {
        this.flipMode |= FlipMode.VERTICAL;
        return this;
    }

    /// @sdlAPI SDL_GPUBlitRegion
    public record Region(
            Texture texture,
            int mipLevel,
            int levelOrDepth,
            int x,
            int y,
            int width,
            int height
    ) {

        public static Region of(
                Texture texture
        ) {
            return of(texture, 0);
        }

        public static Region of(
                Texture texture,
                int mipLevel
        ) {
            return of(
                    texture,
                    mipLevel,
                    0,
                    0,
                    texture.width() >> mipLevel,
                    texture.height() >> mipLevel
            );
        }

        public static Region of(
                Texture texture,
                int mipLevel,
                int x,
                int y,
                int width,
                int height
        ) {
            return new Region(
                    texture,
                    mipLevel,
                    0,
                    x,
                    y,
                    width,
                    height
            );
        }

        public static Region ofLayer(
                Texture texture,
                int layer
        ) {
            return ofLayer(texture, 0, layer);
        }

        public static Region ofLayer(
                Texture texture,
                int mipLevel,
                int layer
        ) {
            return ofLayer(
                    texture,
                    mipLevel,
                    layer,
                    0,
                    0,
                    texture.width() >> mipLevel,
                    texture.height() >> mipLevel
            );
        }

        public static Region ofLayer(
                Texture texture,
                int mipLevel,
                int layer,
                int x,
                int y,
                int width,
                int height
        ) {
            var type = texture.type();
            if (layer != 0) {
                if (!type.isArray()
                        && !type.hasDepth()
                        && type != TextureType.TEXTURE_CUBE
                ) {
                    throw new IllegalArgumentException("Texture type " + type + " does not support layers");
                }
            }

            return new Region(
                    texture,
                    mipLevel,
                    layer,
                    x,
                    y,
                    width,
                    height
            );
        }


        MemorySegment put(MemorySegment segment) {
            SDL_GPUBlitRegion.initialize(
                    segment,
                    this.texture.segment(),
                    this.mipLevel,
                    this.levelOrDepth,
                    this.x,
                    this.y,
                    this.width,
                    this.height
            );
            return segment;
        }

    }


    void put(MemorySegment segment) {
        SDL_GPUBlitInfo.initialize(
                segment,
                this.source.put(SDL_GPUBlitInfo.source(segment)),
                this.destination.put(SDL_GPUBlitInfo.destination(segment)),
                this.loadOp.code(),
                fColor(SDL_GPUBlitInfo.clear_color(segment), this.clearColor),
                this.flipMode,
                this.filter.code(),
                this.cycle.value()
        );
    }

    /// @sdlAPI SDL_FColor
    private static MemorySegment fColor(MemorySegment segment, Vector4f color) {
        SDL_FColor.initialize(segment, color.x, color.y, color.z, color.w);
        return segment;
    }

}
