package io.github.plixo2.sodalite.category.init;

import io.github.plixo2.sodalite.SDLException;
import io.github.plixo2.sodalite.category.properties.PropertyKey;
import io.github.plixo2.sodalite.memory.BitMask;
import io.github.plixo2.sodalite.resource.FreeList;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.Arena;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlCategory CategoryInit
public class Init {
    private Init() {}

    /// SDL states:
    ///
    /// > On Apple platforms, the main thread is the thread that runs your program's main() entry point.
    /// > On other platforms, the main thread is the one that calls SDL_Init(SDL_INIT_VIDEO),
    /// > which should usually be the one that runs your program's main() entry point.
    ///
    /// This may differ from java's "main" thread. Consider the main thread the one
    /// that initializes the video subsystem.
    ///
    /// @sdlAPI SDL_IsMainThread
    public static boolean isMainThread() {
        return SDL_IsMainThread();
    }

    /// @sdlAPI SDL_Init
    /// @threadSafety This function should only be called on the main thread
    public static void init(@InitFlags int flags) throws SDLException {
        check(SDL_Init(flags));
    }

    /// Does exactly the same thing as [#init]
    ///
    /// @sdlAPI SDL_InitSubSystem
    /// @threadSafety This function should only be called on the main thread
    public static void initSubSystem(@InitFlags int flags) throws SDLException {
        check(SDL_InitSubSystem(flags));
    }

    /// Ensures that the given subsystems are initialized.
    ///
    /// Tests each subsystem in the given flags and initializes only
    /// that subsystem if it is not already initialized.
    /// @threadSafety This function should only be called on the main thread
    /// @see #wasAllInit
    /// @see #initSubSystem
    public static void ensureInit(@InitFlags int flags) throws SDLException {
        for (@InitFlags int flag : BitMask.bits(flags, InitFlags.MASK)) {
            if (!wasAllInit(flag)) {
                initSubSystem(flag);
            }
        }
    }

    /// @return true if any of the given subsystems were initialized, false otherwise.
    /// @sdlAPI SDL_WasInit
    public static boolean wasAnyInit(@InitFlags int flags) {
        var result = SDL_WasInit(flags) & flags;
        return result != 0;
    }

    /// @return true if all of the given subsystems were initialized, false otherwise.
    /// @sdlAPI SDL_WasInit
    public static boolean wasAllInit(@InitFlags int flags) {
        var result = SDL_WasInit(flags) & flags;
        //noinspection MagicConstant
        return result == flags;
    }

    /// Wrapper for `SDL_WasInit(0)`
    ///
    /// @return Iterable of the initialized subsystems
    /// @sdlAPI SDL_WasInit
    public static @InitFlags int getInit() {
        //noinspection MagicConstant
        return SDL_WasInit(0) & InitFlags.MASK.value();
    }

    /// You still need to call [#quit] even if you close all open subsystems.
    /// @sdlAPI SDL_QuitSubSystem
    public static void quitSubSystem(@InitFlags int flags) {
        SDL_QuitSubSystem(flags);
    }


    /// You should call this function even if you have
    /// already shutdown each initialized subsystem.
    ///
    /// This function will also call [FreeList#freeGlobal] to free any global
    /// and non-collected gc-managed resources.
    /// You are not protected when using a gc-managed resource after this function is called,
    /// consider them freed and unusable.
    ///
    /// @sdlAPI SDL_Quit
    /// @threadSafety This function should only be called on the main thread
    public static void quit() {

        // intentionally not surrounded with try/catch
        // to avoid subsequent errors (e.g. use-after-free or double-free's)
        // that might crash the jvm.
        FreeList.freeGlobal();


        SDL_Quit();
    }

    /// @sdlAPI SDL_SetAppMetadata
    public static void setAppMetaData(@Nullable String name, @Nullable String version, @Nullable String identifier)
            throws SDLException {
        try (var arena = Arena.ofConfined()) {
            check(SDL_SetAppMetadata(
                    allocNullString(arena, name),
                    allocNullString(arena, version),
                    allocNullString(arena, identifier)
            ));
        }
    }

    /// @sdlAPI SDL_SetAppMetadataProperty
    public static void setAppMetadataProperty(PropertyKey<String> name, @Nullable String value)
            throws SDLException {
        try (var arena = Arena.ofConfined()) {
            check(SDL_SetAppMetadataProperty(
                    name.nameSegment(),
                    allocNullString(arena, value)
            ));
        }
    }


}
