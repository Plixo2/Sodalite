package io.github.plixo2.sodalite.category.main;

import io.github.plixo2.sodalite.category.init.AppResult;
import io.github.plixo2.sodalite.category.init.Init;
import org.jetbrains.annotations.Nullable;

/// See
/// [Main callbacks in SDL3](https://wiki.libsdl.org/SDL3/README-main-functions#main-callbacks-in-sdl3)
///
/// Events are handled via the [EventCallbacks] methods,
/// thus there is no separate event function like `SDL_AppEvent`.
/// You can throw exceptions to exit, the [#quit] method will always be called.
///
/// Implement this interface on your application's main class. Thanks to
/// [Instance Main Methods](https://openjdk.org/jeps/445) (finalized in Java 23),
/// the [#main] method is invoked automatically as
/// your program's `public static void main(String[] args)` entry point, no
/// separate static main method is needed.
///
/// You dont have to use this interface and just start calling methods directly,
/// but dont forget to call [Init#quit()] when your application is done.
///
/// This does not use SDL's main callbacks system, but rather simulates it.
///
public interface Callbacks extends EventCallbacks {

    /// @throws Exception to exit with [AppResult#FAILURE]
    /// @sdlAPI SDL_AppInit
    AppResult init(String[] args) throws Exception;

    /// @throws Exception to exit with [AppResult#FAILURE]
    /// @sdlAPI SDL_AppIterate
    AppResult iterate() throws Exception;


    /// @param result the result that terminated the application:
    ///               [AppResult#SUCCESS] or [AppResult#FAILURE]
    /// @sdlAPI SDL_AppQuit
    void quit(AppResult result);


    /// @param result the result that terminated the application:
    ///               [AppResult#SUCCESS] or [AppResult#FAILURE]
    /// @param failure An optional exception that caused the application to terminate.
    ///                A present exception implies [AppResult#FAILURE],
    ///                but [AppResult#FAILURE] does not imply a present exception
    /// @sdlAPI SDL_AppQuit
    default void quit(AppResult result, @Nullable Exception failure) {
        quit(result);
    }

    /// Called automatically by the Java runtime as your program's entry point.
    /// Will call [System#exit] on exit, so dont call this method manually unless
    /// you want to exit the program afterwards.
    ///
    /// @throws Exception a exception from any of the callbacks
    /// @see Main#enterAppMainCallbacks
    /// @sdlAPI SDL_EnterAppMainCallbacks
    default void main(String[] args) throws Exception {
        var result = Main.enterAppMainCallbacks(this, args);
        var code = result ? 0 : 1;
        System.exit(code);
    }

}
