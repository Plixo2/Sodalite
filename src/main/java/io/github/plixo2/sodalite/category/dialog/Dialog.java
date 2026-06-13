package io.github.plixo2.sodalite.category.dialog;


import io.github.plixo2.sodalite.category.video.Window;
import org.jetbrains.annotations.Nullable;
import org.libsdl.sdl.SDL_DialogFileCallback;
import org.libsdl.sdl.SDL_DialogFileFilter;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.List;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlCategory CategoryDialog
public class Dialog {
    private Dialog() {}

    /// @sdlAPI SDL_ShowOpenFileDialog
    public static void showOpenFileDialog(
            FileCallback callback,
            @Nullable Window window,
            List<FileFilter> filters,
            @Nullable String defaultLocation,
            boolean allowMany
    ) {
        var arena = Arena.ofShared();
        var openDialog = new OpenDialog(arena, callback, filters);

        try {
            var handle = SDL_DialogFileCallback.allocate(openDialog, arena);

            var filterSegment = MemorySegment.NULL;
            if (!filters.isEmpty()) {
                filterSegment = SDL_DialogFileFilter.allocateArray(filters.size(), arena);
                for (int i = 0; i < filters.size(); i++) {
                    var filter = filters.get(i);
                    var filterStruct = SDL_DialogFileFilter.asSlice(filterSegment, i);
                    filter.put(filterStruct, arena);
                }
            }

            SDL_ShowOpenFileDialog(
                    handle,
                    MemorySegment.NULL,
                    window == null ? MemorySegment.NULL : window.segment(),
                    filterSegment,
                    filters.size(),
                    defaultLocation == null ? MemorySegment.NULL : arena.allocateFrom(defaultLocation),
                    allowMany
            );

        } catch (Throwable e) {
            arena.close();
            throw e;
        }
    }
    public static void showOpenFileDialog(
            FileCallback callback,
            List<FileFilter> filters,
            @Nullable String defaultLocation,
            boolean allowMany
    ) {
        showOpenFileDialog(callback, null, filters, defaultLocation, allowMany);
    }
    public static void showOpenFileDialog(
            FileCallback callback,
            @Nullable String defaultLocation,
            boolean allowMany
    ) {
        showOpenFileDialog(callback, List.of(), defaultLocation, allowMany);
    }

    /// @sdlAPI SDL_ShowOpenFolderDialog
    public static void showOpenFolderDialog(
            FileCallback callback,
            @Nullable Window window,
            @Nullable String defaultLocation,
            boolean allowMany
    ) {
        var arena = Arena.ofShared();
        var openDialog = new OpenDialog(arena, callback, List.of());

        try {
            var handle = SDL_DialogFileCallback.allocate(openDialog, arena);
            SDL_ShowOpenFolderDialog(
                    handle,
                    MemorySegment.NULL,
                    window == null ? MemorySegment.NULL : window.segment(),
                    defaultLocation == null ? MemorySegment.NULL : arena.allocateFrom(defaultLocation),
                    allowMany
            );

        } catch (Throwable e) {
            arena.close();
            throw e;
        }
    }
    public static void showOpenFolderDialog(
            FileCallback callback,
            @Nullable String defaultLocation,
            boolean allowMany
    ) {
        showOpenFolderDialog(callback, null, defaultLocation, allowMany);
    }


    /// @sdlAPI SDL_ShowSaveFileDialog
    public static void showSaveFileDialog(
            FileCallback callback,
            @Nullable Window window,
            List<FileFilter> filters,
            @Nullable String defaultLocation
    ) {
        var arena = Arena.ofShared();
        var openDialog = new OpenDialog(arena, callback, filters);

        try {
            var handle = SDL_DialogFileCallback.allocate(openDialog, arena);

            var filterSegment = MemorySegment.NULL;
            if (!filters.isEmpty()) {
                filterSegment = SDL_DialogFileFilter.allocateArray(filters.size(), arena);
                for (int i = 0; i < filters.size(); i++) {
                    var filter = filters.get(i);
                    var filterStruct = SDL_DialogFileFilter.asSlice(filterSegment, i);
                    filter.put(filterStruct, arena);
                }
            }

            SDL_ShowSaveFileDialog(
                    handle,
                    MemorySegment.NULL,
                    window == null ? MemorySegment.NULL : window.segment(),
                    filterSegment,
                    filters.size(),
                    defaultLocation == null ? MemorySegment.NULL : arena.allocateFrom(defaultLocation)
            );

        } catch (Throwable e) {
            arena.close();
            throw e;
        }
    }
    public static void showSaveFileDialog(
            FileCallback callback,
            List<FileFilter> filters,
            @Nullable String defaultLocation
    ) {
        showSaveFileDialog(callback, null, filters, defaultLocation);
    }
    public static void showSaveFileDialog(
            FileCallback callback,
            @Nullable String defaultLocation
    ) {
        showSaveFileDialog(callback, List.of(), defaultLocation);
    }



}
