package io.github.plixo2.sodalite.category.cpuinfo;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlCategory CategoryCPUInfo
public class CPUInfo {
    private CPUInfo() {}


    /// @sdlAPI SDL_GetCPUCacheLineSize
    public static int cpuCacheLineSize() {
        return SDL_GetCPUCacheLineSize();
    }

    /// @sdlAPI SDL_GetNumLogicalCPUCores
    public static int numLogicalCores() {
        return SDL_GetNumLogicalCPUCores();
    }

    /// @sdlAPI SDL_GetSystemPageSize
    public static PageSize systemPageSize() {
        var size = SDL_GetSystemPageSize();
        return PageSize.fromValue(size);
    }

    /// @sdlAPI SDL_GetSystemRAM
    public static int systemRAM() {
        return SDL_GetSystemRAM();
    }

}
