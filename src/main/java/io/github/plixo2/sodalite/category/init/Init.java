package io.github.plixo2.sodalite.category.init;

import io.github.plixo2.sodalite.resource.PendingFrees;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

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

    /// @sdlAPI SDL_WasInit
    public static boolean wasInit(@InitFlags int flags) {
        var result = SDL_WasInit(flags) & flags;
        return result == (Integer)flags;
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
    public static void setAppMetadataProperty(AppMetadataKey name, @Nullable String value) {
        try (var arena = Arena.ofConfined()) {
            check(SDL_SetAppMetadataProperty(
                    name.stringSegment(),
                    allocNullString(arena, value)
            ));
        }
    }

    private static MemorySegment allocNullString(Arena arena, @Nullable String str) {
        if (str == null) {
            return MemorySegment.NULL;
        } else {
            return arena.allocateFrom(str);
        }
    }
}
