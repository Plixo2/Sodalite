package io.github.plixo2.sodalite.category.log;

/// @sdlAPI SDL_LogCategory
public enum LogCategory {
    APPLICATION,
    ERROR,
    ASSERT,
    SYSTEM,
    AUDIO,
    VIDEO,
    RENDER,
    INPUT,
    TEST,
    GPU,

    RESERVED2,
    RESERVED3,
    RESERVED4,
    RESERVED5,
    RESERVED6,
    RESERVED7,
    RESERVED8,
    RESERVED9,
    RESERVED10,


    // Beyond this point is reserved for application use
    CUSTOM_0,

    ;

    public int code() {
        return this.ordinal();
    }
}
