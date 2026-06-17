package io.github.plixo2.sodalite.category.main;

import io.github.plixo2.sodalite.category.init.AppResult;

/// See
/// [Main callbacks in SDL3](https://wiki.libsdl.org/SDL3/README-main-functions#main-callbacks-in-sdl3)
///
/// Events are handled via the [EventCallbacks] methods,
/// thus there is no seperate event function like `SDL_AppEvent`.
/// You can throw exceptions to exit, the [#quit] method will always be called.
///
/// Implement this interface on your application's main class. Thanks to
/// [Instance Main Methods](https://openjdk.org/jeps/445) (finalized in Java 23),
/// the [#main] method is invoked automatically as
/// your program's `public static void main(String[] args)` entry point, no
/// separate static main method is needed.
///
/// This does not use the SDL's main callbacks system, but rather simulates it.
///
public interface Callbacks extends EventCallbacks {

    /// @throws Exception to exit with [AppResult#FAILURE]
    /// @sdlAPI SDL_AppInit
    AppResult init(String[] args) throws Exception;

    /// @throws Exception to exit with [AppResult#FAILURE]
    /// @sdlAPI SDL_AppIterate
    AppResult iterate() throws Exception;

    /// @sdlAPI SDL_AppQuit
    void quit(AppResult result);

    /// @sdlAPI SDL_EnterAppMainCallbacks
    default void main(String[] args) throws Exception {
        var wrapper = new CallbackWrapper(this);
        var result = wrapper.run(args);
        if (!result) {
            System.exit(1);
        }
    }

}
