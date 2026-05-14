package io.github.plixo2.sodalite.log;

/// @apiNote SDL_LogPriority
public enum LogPriority {
    INVALID,
    TRACE,
    VERBOSE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    CRITICAL,

    ;


    public int code() {
        return this.ordinal();
    }

}
