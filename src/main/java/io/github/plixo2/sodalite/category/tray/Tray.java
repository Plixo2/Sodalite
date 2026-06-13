package io.github.plixo2.sodalite.category.tray;

import io.github.plixo2.sodalite.category.surface.SurfaceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlCategory CategoryTray
public class Tray {
    private Tray() {}

    /// @sdlAPI SDL_CreateTray
    public static TrayObject createTray(
            ResourceSet resources,
            @Nullable SurfaceObject icon,
            @Nullable String tooltip
    ) {
        try (var arena = Arena.ofConfined()) {
            var surfaceSegment = icon != null ? icon.segment() : MemorySegment.NULL;
            var tooltipSegment = tooltip != null ? arena.allocateFrom(tooltip) : MemorySegment.NULL;

            var traySegment = SDL_CreateTray(surfaceSegment, tooltipSegment);
            return new TrayObject(resources, traySegment);
        }

    }

    /// @sdlAPI SDL_SetTrayIcon
    static void setTrayIcon(
            TrayObject tray,
            @Nullable SurfaceObject icon
    ) {
        var surfaceSegment = icon != null ? icon.segment() : MemorySegment.NULL;
        SDL_SetTrayIcon(tray.segment(), surfaceSegment);
    }

    /// @sdlAPI SDL_SetTrayTooltip
    static void setTrayTooltip(
            TrayObject tray,
            @Nullable String tooltip
    ) {
        try (var arena = Arena.ofConfined()) {
            var tooltipSegment = tooltip != null ? arena.allocateFrom(tooltip) : MemorySegment.NULL;
            SDL_SetTrayTooltip(tray.segment(), tooltipSegment);
        }
    }

    /// @sdlAPI SDL_CreateTrayMenu
    static TrayMenu createTrayMenu(
            TrayObject tray
    ) {
        var menuSegment = SDL_CreateTrayMenu(tray.segment());
        return new TrayMenu(tray, menuSegment);
    }

    /// @sdlAPI SDL_InsertTrayEntryAt
    static TrayEntry insertTrayEntryAt(
            TrayMenu menu,
            int index,
            @Nullable String label,
            @TrayEntryFlags int flags
    ) {
        try (var arena = Arena.ofConfined()) {
            var labelSegment = label != null ? arena.allocateFrom(label) : MemorySegment.NULL;

            var entrySegment = SDL_InsertTrayEntryAt(
                    menu.segment(),
                    index,
                    labelSegment,
                    flags
            );
            if (entrySegment.address() == 0) {
                entrySegment = SDL_InsertTrayEntryAt(
                        menu.segment(),
                        -1,
                        labelSegment,
                        flags
                );
            }

            return new TrayEntry(menu.object, entrySegment);
        }
    }

    /// @sdlAPI SDL_CreateTraySubmenu
    static TrayMenu createTraySubmenu(
            TrayEntry entry
    ) {
        var submenuSegment = assertNotNull(SDL_CreateTraySubmenu(entry.segment()));
        return new TrayMenu(entry.object, submenuSegment);
    }

    /// @sdlAPI SDL_SetTrayEntryEnabled
    static void setTrayEntryEnabled(
            TrayEntry entry,
            boolean enabled
    ) {
        SDL_SetTrayEntryEnabled(entry.segment(), enabled);
    }

    /// @sdlAPI SDL_SetTrayEntryLabel
    static void setTrayEntryLabel(
            TrayEntry entry,
            String label
    ) {
        try (var arena = Arena.ofConfined()) {
            SDL_SetTrayEntryLabel(entry.segment(),  arena.allocateFrom(label));
        }
    }

    /// @sdlAPI SDL_SetTrayEntryChecked
    static void setTrayEntryChecked(
            TrayEntry entry,
            boolean checked
    ) {
        SDL_SetTrayEntryChecked(entry.segment(), checked);
    }

    /// @sdlAPI SDL_GetTrayEntryEnabled
    static boolean isTrayEntryEnabled(
            TrayEntry entry
    ) {
        return SDL_GetTrayEntryEnabled(entry.segment());
    }

    /// @sdlAPI SDL_GetTrayEntryLabel
    static String getTrayEntryLabel(
            TrayEntry entry
    ) {
        var labelSegment = SDL_GetTrayEntryLabel(entry.segment());
        if (labelSegment.address() == 0) {
            return "";  // seperator
        }
        return labelSegment.getString(0);
    }

    /// @sdlAPI SDL_GetTrayEntryChecked
    static boolean isTrayEntryChecked(
            TrayEntry entry
    ) {
        return SDL_GetTrayEntryChecked(entry.segment());
    }

    /// @sdlAPI SDL_RemoveTrayEntry
    static void removeTrayEntry(
            TrayEntry entry
    ) {
        SDL_RemoveTrayEntry(entry.segment());
    }

    /// @sdlAPI SDL_SetTrayEntryCallback
    static void setTrayEntryCallback(
            TrayEntry entry,
            TrayCallback callback
    ) {
        var callbackSegment = entry.object.registerCallback(
                () -> callback.apply(entry)
        );
        SDL_SetTrayEntryCallback(
                entry.segment(),
                callbackSegment,
                MemorySegment.NULL
        );
    }

    /// @sdlAPI SDL_DestroyTray
    static void destroyTray(
        MemorySegment tray
    ) {
        SDL_DestroyTray(tray);
    }

    /// @sdlAPI SDL_UpdateTrays
    static void updateTrays() {
        SDL_UpdateTrays();
    }

}
