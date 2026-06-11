package io.github.plixo2.sodalite.category.init;

import io.github.plixo2.sodalite.resource.PendingFrees;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

public class Init {

    /// @apiNote SDL_Init
    public static void init(@InitFlags int flags) {
        check(SDL_Init(flags));
    }

    /// Does exactly the same thing as {@link #init}
    ///
    /// @apiNote SDL_InitSubSystem
    public static void initSubSystem(@InitFlags int flags) {
        check(SDL_InitSubSystem(flags));
    }

    /// @apiNote SDL_WasInit
    public static boolean wasInit(@InitFlags int flags) {
        var result = SDL_WasInit(flags) & flags;
        return result == (Integer)flags;
    }

    /// @apiNote SDL_QuitSubSystem
    public static void quitSubSystem(@InitFlags int flags) {
        SDL_QuitSubSystem(flags);
    }

    /// Should only be called on the main thread.
    /// @apiNote SDL_Quit
    public static void quit() {
        try {
            PendingFrees.freeAll();
        } finally {
            SDL_Quit();
        }
    }

    /// @apiNote SDL_SetAppMetadata
    public static void setAppMetaData(@Nullable String name, @Nullable String version, @Nullable String identifier) {
        try (var arena = Arena.ofConfined()) {
            check(SDL_SetAppMetadata(
                    allocNullString(arena, name),
                    allocNullString(arena, version),
                    allocNullString(arena, identifier)
            ));
        }
    }

    /// @apiNote SDL_SetAppMetadataProperty
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
