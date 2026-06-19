package io.github.plixo2.sodalite.category.init;

import io.github.plixo2.sodalite.category.properties.PropertyKey;
import io.github.plixo2.sodalite.resource.PendingFrees;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.Arena;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlCategory CategoryInit
public class Init {
    private Init() {}

    /// @sdlAPI SDL_Init
    public static void init(@InitFlags int flags) {
        check(SDL_Init(flags));
    }

    /// Does exactly the same thing as [#init]
    ///
    /// @sdlAPI SDL_InitSubSystem
    public static void initSubSystem(@InitFlags int flags) {
        check(SDL_InitSubSystem(flags));
    }

    public static void ensureInit(@InitFlags int flags) {
        forEachFlag(
                flags,
                InitFlags.MASK,
                (@InitFlags int flag) -> {
                    if (!wasInit(flag)) {
                        initSubSystem(flag);
                    }
                }
        );
    }

    /// @sdlAPI SDL_WasInit
    public static boolean wasInit(@InitFlags int flags) {
        var result = SDL_WasInit(flags) & flags;
        //noinspection MagicConstant
        return result == flags;
    }

    /// @sdlAPI SDL_QuitSubSystem
    public static void quitSubSystem(@InitFlags int flags) {
        SDL_QuitSubSystem(flags);
    }

    /// @sdlAPI SDL_Quit
    /// @threadSafety This function should only be called on the main thread
    public static void quit() {
        try {
            PendingFrees.freeGlobal();
        } finally {
            SDL_Quit();
        }
    }

    /// @sdlAPI SDL_SetAppMetadata
    public static void setAppMetaData(@Nullable String name, @Nullable String version, @Nullable String identifier) {
        try (var arena = Arena.ofConfined()) {
            check(SDL_SetAppMetadata(
                    allocNullString(arena, name),
                    allocNullString(arena, version),
                    allocNullString(arena, identifier)
            ));
        }
    }

    /// @sdlAPI SDL_SetAppMetadataProperty
    public static void setAppMetadataProperty(PropertyKey<String> name, @Nullable String value) {
        try (var arena = Arena.ofConfined()) {
            check(SDL_SetAppMetadataProperty(
                    name.nameSegment(),
                    allocNullString(arena, value)
            ));
        }
    }


}
