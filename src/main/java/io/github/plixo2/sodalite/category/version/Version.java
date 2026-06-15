package io.github.plixo2.sodalite.category.version;


import static org.libsdl.sdl.SDL3_h.*;

/// @sdlCategory CategoryVersion
public class Version {
    private Version() {}


    /// @sdlAPI SDL_GetVersion
    /// @sdlOther SDL_VERSION
    public static VersionNumber getVersion(VersionTarget target) {
        var version = switch (target) {
            case COMPILED -> SDL_VERSION;
            case LINKED -> SDL_GetVersion();
        };
        return new VersionNumber(version);
    }



    /// @sdlAPI SDL_GetRevision
    /// @sdlOther SDL_REVISION
    public static String getRevision(VersionTarget target) {
        var segment = switch (target) {
            case COMPILED -> SDL_REVISION();
            case LINKED -> SDL_GetRevision();
        };
        if (segment.address() == 0) {
            return "";
        }
        return segment.getString(0);
    }

}
