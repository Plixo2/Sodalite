package io.github.plixo2.sodalite.log;

import org.jetbrains.annotations.Range;

import static org.libsdl.sdl.SDL3_h.*;

public class Log {

    /// @apiNote SDL_SetLogPriority
    public static void setLogPriority(
            LogCategory category,
            LogPriority priority
    ) {
        SDL_SetLogPriority(category.code(), priority.code());
    }

    /// Set the priority of a custom log category. \
    /// Equivalent to `SDL_SetLogPriority(SDL_LOG_CATEGORY_CUSTOM + customCategory, priority)`.
    ///
    /// @apiNote SDL_SetLogPriority
    public static void setLogPriorityCustom(
            @Range(from = 0, to = Integer.MAX_VALUE) int customCategory,
            LogPriority priority
    ) {
        SDL_SetLogPriority(LogCategory.CUSTOM_0.code() + customCategory, priority.code());
    }

}
