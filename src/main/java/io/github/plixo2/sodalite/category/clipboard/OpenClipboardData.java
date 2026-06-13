package io.github.plixo2.sodalite.category.clipboard;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.libsdl.sdl.SDL_ClipboardCleanupCallback;
import org.libsdl.sdl.SDL_ClipboardDataCallback;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.plixo2.sodalite.Internal.*;

/// Helper for ClipboardDataCallback
/// @sdlAPI SDL_ClipboardCleanupCallback
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class OpenClipboardData implements
        SDL_ClipboardDataCallback.Function,
        SDL_ClipboardCleanupCallback.Function
{
    private final Arena arena;
    private final ClipboardDataCallback callback;
    private final List<Arena> perCallArenas = new ArrayList<>();

    private boolean closed = false;

    /// `SDL_ClipboardDataCallback`
    @Override
    public MemorySegment apply(
            MemorySegment userData,
            MemorySegment mime_type,
            MemorySegment size
    ) {
        if (mime_type.address() == 0) {
            return MemorySegment.NULL;  // SDL_ClipboardCleanupCallback is used for cleanup
        }
        if (this.closed) {
            throw new IllegalStateException("Clipboard data callback is already closed");
        }

        var asString = mime_type.reinterpret(Long.MAX_VALUE).getString(0);

        var result = Objects.requireNonNull(this.callback.get(this.arena, asString));

        if (result.address() == 0) {
            size.set(ValueLayout.JAVA_LONG, 0, 0);
            return MemorySegment.NULL;
        }

        size.set(ValueLayout.JAVA_LONG, 0, result.byteSize());
        return result;
    }

    /// `SDL_ClipboardCleanupCallback`
    @Override
    public void apply(MemorySegment userData) {
        close();
    }

    synchronized void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        this.arena.close();
    }
}
