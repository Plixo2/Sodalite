package io.github.plixo2.sodalite.category.clipboard;

import io.github.plixo2.sodalite.category.error.Error;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.libsdl.sdl.SDL_ClipboardCleanupCallback;
import org.libsdl.sdl.SDL_ClipboardDataCallback;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.ArrayList;
import java.util.List;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlCategory CategoryClipboard
public class Clipboard {

    /// @sdlAPI SDL_GetClipboardText
    public static String getText() {
        var result = assertNotNull(SDL_GetClipboardText());
        try {
            var asString = result.getString(0);
            if (asString.isEmpty()) {
                Error.clearError(); // ignore the empty string error
            }
            return asString;
        } finally {
            SDL_free(result);
        }
    }

    /// @sdlAPI SDL_SetClipboardText
    public static void setText(String text) {
        try (var arena = Arena.ofConfined()) {
            var textSegment = arena.allocateFrom(text);
            check(SDL_SetClipboardText(textSegment));
        }
    }

    /// @sdlAPI SDL_HasClipboardText
    public static boolean hasText() {
        return SDL_HasClipboardText();
    }

    /// @sdlAPI SDL_GetClipboardMimeTypes
    public static List<String> getMimeTypes() {
        try (var arena = Arena.ofConfined()) {
            var numMimeTypes = arena.allocate(ValueLayout.JAVA_LONG);
            var ptr = check(SDL_GetClipboardMimeTypes(numMimeTypes));

            var count = numMimeTypes.get(ValueLayout.JAVA_LONG, 0);
            try {
                var result = new ArrayList<String>(Math.toIntExact(count));
                for (long i = 0; i < count; i++) {
                    var entry = ptr.get(ValueLayout.ADDRESS, i * ValueLayout.ADDRESS.byteSize());
                    result.add(entry.reinterpret(Long.MAX_VALUE).getString(0));
                }
                return result;
            } finally {
                SDL_free(ptr);
            }
        }
    }

    /// @sdlAPI SDL_HasClipboardData
    public static boolean hasData(String mimeType) {
        try (var arena = Arena.ofConfined()) {
            return SDL_HasClipboardData(arena.allocateFrom(mimeType));
        }
    }

    /// @sdlAPI SDL_GetClipboardData
    public static ClipboardData getData(
            ResourceSet resourceSet,
            String mimeType
    ) {
        var errorMessage = Error.getError();
        try (var arena = Arena.ofConfined()) {
            if (!errorMessage.isEmpty()) {
                Error.clearError();
            }
            var sizeSegment = arena.allocate(ValueLayout.JAVA_LONG);
            var ptr = SDL_GetClipboardData(arena.allocateFrom(mimeType), sizeSegment);
            if (ptr.address() == 0) {
                if (Error.getError().isEmpty()) {
                    errorMessage = "SDL_GetClipboardData failed without setting an error message, " +
                                   "see https://github.com/libsdl-org/SDL/issues/14941";
                    Error.setError(errorMessage);
                }
                check(false);
            }

            var size = sizeSegment.get(ValueLayout.JAVA_LONG, 0);
            ptr = ptr.reinterpret(size);
            return new ClipboardData(resourceSet, ptr);
        } finally {
            Error.setError(errorMessage);
        }
    }

    /// @sdlAPI SDL_free
    static void freeClipboardData(
            MemorySegment segment
    ) {
        if (segment.address() != 0 && segment.byteSize() > 0) {
            SDL_free(segment);
        }
    }

    /// @sdlAPI SDL_SetClipboardData
    public static void setData(
            ClipboardDataCallback callback,
            String... mimeTypes
    ) {
        var arena = Arena.ofShared();
        var openData = new OpenClipboardData(arena, callback);

        try {
            var callbackHandle = SDL_ClipboardDataCallback.allocate(openData, arena);
            var freeHandle = SDL_ClipboardCleanupCallback.allocate(openData, arena);

            // cannot use a temporary arena when SDL_SetClipboardData crashes...
            var mimeTypesSegment = arena.allocate(ValueLayout.ADDRESS, mimeTypes.length);
            for (int i = 0; i < mimeTypes.length; i++) {
                var stringSegment = arena.allocateFrom(mimeTypes[i]);
                mimeTypesSegment.setAtIndex(ValueLayout.ADDRESS, i, stringSegment);
            }

            check(SDL_SetClipboardData(
                    callbackHandle,
                    freeHandle,
                    MemorySegment.NULL,
                    mimeTypesSegment,
                    mimeTypes.length
            ));
        } catch(Throwable e) {
            if (!(e instanceof SDL3Exception)) {
                openData.close();
            }
            throw e;
        }

    }

    /// @sdlAPI SDL_ClearClipboardData
    public static void clearData() {
        check(SDL_ClearClipboardData());
    }

    /// @sdlAPI SDL_GetPrimarySelectionText
    public static String getPrimarySelectionText() {
        var ptr = check(SDL_GetPrimarySelectionText());
        try {
            return ptr.reinterpret(Long.MAX_VALUE).getString(0);
        } finally {
            SDL_free(ptr);
        }
    }

    /// @sdlAPI SDL_HasPrimarySelectionText
    public static boolean hasPrimarySelectionText() {
        return SDL_HasPrimarySelectionText();
    }

    /// @sdlAPI SDL_SetPrimarySelectionText
    public static void setPrimarySelectionText(String text) {
        try (var arena = Arena.ofConfined()) {
            check(SDL_SetPrimarySelectionText(arena.allocateFrom(text)));
        }
    }

}
