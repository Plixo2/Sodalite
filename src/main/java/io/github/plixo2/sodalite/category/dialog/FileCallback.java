package io.github.plixo2.sodalite.category.dialog;


import org.jetbrains.annotations.Nullable;

import java.util.List;

/// @sdlAPI SDL_DialogFileCallback
public interface FileCallback {
    void accept(List<String> files, @Nullable FileFilter filter);
}
