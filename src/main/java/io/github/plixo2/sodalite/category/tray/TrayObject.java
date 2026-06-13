package io.github.plixo2.sodalite.category.tray;


import io.github.plixo2.sodalite.category.surface.SurfaceObject;
import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.jetbrains.annotations.Nullable;
import org.libsdl.sdl.SDL_TrayCallback;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_Tray
public class TrayObject extends ResourceObject {
    private final MemorySegment segment;
    private final Arena callbackArena;

    TrayObject(
            ResourceSet resources,
            MemorySegment segment
    ) {
        var callbackArena = this.callbackArena = Arena.ofShared();
        resources.register(this, () -> {
            Tray.destroyTray(segment);
            callbackArena.close();
        });
        this.segment = segment;
    }

    public void setIcon(@Nullable SurfaceObject icon) {
        Tray.setTrayIcon(this, icon);
    }

    public void setTooltip(@Nullable String tooltip) {
        Tray.setTrayTooltip(this, tooltip);
    }

    public TrayMenu createMenu() {
        return Tray.createTrayMenu(this);
    }

    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }

    MemorySegment registerCallback(
            Runnable runnable
    ) {
        return SDL_TrayCallback.allocate((_, _) -> runnable.run(), this.callbackArena);
    }

}
