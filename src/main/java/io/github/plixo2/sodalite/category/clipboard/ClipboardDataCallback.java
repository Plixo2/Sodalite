package io.github.plixo2.sodalite.category.clipboard;



import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_ClipboardDataCallback
public interface ClipboardDataCallback {
    /// @param clipBoardArena the arena to allocate the returned data in, when needed.
    ///                       Will be closed when the clipboard is cleared or new data is set.
    /// @param mimeType the MIME type of the data to retrieve
    /// @return The data pointer with a valid length, for the given MIME type.
    ///         MemorySegment.NULL or a zero-length segment will still be send to the "receiver",
    ///         but is mostly not supported! [Issue](https://github.com/libsdl-org/SDL/issues/15843)
    MemorySegment get(Arena clipBoardArena, String mimeType);
}
