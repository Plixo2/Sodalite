package io.github.plixo2.sodalite.category.dialog;

/// @sdlAPI SDL_FileDialogType
public enum FileDialogType {
    OPEN_FILE,
    SAVE_FILE,
    OPEN_FOLDER,

    ;


    public int code() {
        return ordinal();
    }
}
