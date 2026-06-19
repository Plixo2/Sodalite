package io.github.plixo2.sodalite.category.blendmode;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlAPI SDL_BlendMode
/// @sdlCategory CategoryBlendmode
public class BlendMode {
    public static final BlendMode NONE = BlendMode.of(SDL_BLENDMODE_NONE());
    public static final BlendMode BLEND = BlendMode.of(SDL_BLENDMODE_BLEND());
    public static final BlendMode BLEND_PREMULTIPLIED = BlendMode.of(SDL_BLENDMODE_BLEND_PREMULTIPLIED());
    public static final BlendMode ADD = BlendMode.of(SDL_BLENDMODE_ADD());
    public static final BlendMode ADD_PREMULTIPLIED = BlendMode.of(SDL_BLENDMODE_ADD_PREMULTIPLIED());
    public static final BlendMode MOD = BlendMode.of(SDL_BLENDMODE_MOD());
    public static final BlendMode MUL = BlendMode.of(SDL_BLENDMODE_MUL());
    public static final BlendMode INVALID = BlendMode.of(SDL_BLENDMODE_INVALID());

    @Getter
    private final int code;

    BlendMode(int code) {
        this.code = code;
    }
    public static BlendMode of(int code) {
        return new BlendMode(code);
    }

    /// @sdlAPI SDL_ComposeCustomBlendMode
    public static BlendMode compose(
            BlendFactor srcColorFactor,
            BlendFactor dstColorFactor,
            BlendOperation colorOperation,
            BlendFactor srcAlphaFactor,
            BlendFactor dstAlphaFactor,
            BlendOperation alphaOperation
    ) {
        var code = SDL_ComposeCustomBlendMode(
                srcColorFactor.code(),
                dstColorFactor.code(),
                colorOperation.code(),
                srcAlphaFactor.code(),
                dstAlphaFactor.code(),
                alphaOperation.code()
        );
        return BlendMode.of(code);
    }


}
