package io.github.plixo2.sodalite.category.dialog;


import io.github.plixo2.sodalite.Internal;
import io.github.plixo2.sodalite.SDLException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.libsdl.sdl.SDL_DialogFileCallback;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.ArrayList;
import java.util.List;

import static io.github.plixo2.sodalite.Internal.*;

/// Helper for FileCallback (`SDL_DialogFileCallback`)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class OpenDialog implements SDL_DialogFileCallback.Function {
    private final Arena arena;
    private final FileCallback callback;
    private final List<FileFilter> filters;

    @Override
    public void apply(MemorySegment userdata, MemorySegment filelist, int filter) {
        try {
            check(filelist.address());


            var files = new ArrayList<String>();

            long offset = 0;
            MemorySegment entry = filelist.get(ValueLayout.ADDRESS, offset);
            while (entry.address() != 0) {
                files.add(entry.reinterpret(Long.MAX_VALUE).getString(0));
                offset += ValueLayout.ADDRESS.byteSize();
                entry = filelist.get(ValueLayout.ADDRESS, offset);
            }

            var filterObj = filter >= 0 && filter < this.filters.size()
                    ? this.filters.get(filter)
                    : null;

            this.callback.accept(files, filterObj);
        } finally {
            this.arena.close();
        }
    }
}
