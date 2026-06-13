package io.github.plixo2.sodalite.category.init;

import lombok.RequiredArgsConstructor;

import java.lang.foreign.MemorySegment;

import static org.libsdl.sdl.SDL3_h.*;

/// Keys for `SDL_SetAppMetadataProperty`
/// - SDL_PROP_APP_METADATA_NAME_STRING
/// - SDL_PROP_APP_METADATA_VERSION_STRING
/// - SDL_PROP_APP_METADATA_IDENTIFIER_STRING
/// - SDL_PROP_APP_METADATA_CREATOR_STRING
/// - SDL_PROP_APP_METADATA_COPYRIGHT_STRING
/// - SDL_PROP_APP_METADATA_URL_STRING
/// - SDL_PROP_APP_METADATA_TYPE_STRING
@RequiredArgsConstructor
public enum AppMetadataKey {
    NAME(SDL_PROP_APP_METADATA_NAME_STRING()),
    VERSION(SDL_PROP_APP_METADATA_VERSION_STRING()),
    IDENTIFIER(SDL_PROP_APP_METADATA_IDENTIFIER_STRING()),
    CREATOR(SDL_PROP_APP_METADATA_CREATOR_STRING()),
    COPYRIGHT(SDL_PROP_APP_METADATA_COPYRIGHT_STRING()),
    URL(SDL_PROP_APP_METADATA_URL_STRING()),
    TYPE(SDL_PROP_APP_METADATA_TYPE_STRING()),

    ;

    private final MemorySegment stringSegment;

    public MemorySegment stringSegment() {
        return this.stringSegment;
    }
}
