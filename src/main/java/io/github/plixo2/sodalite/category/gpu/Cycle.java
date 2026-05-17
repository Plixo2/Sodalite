package io.github.plixo2.sodalite.category.gpu;


/// [A Note On Cycling](https://wiki.libsdl.org/SDL3/CategoryGPU#a-note-on-cycling)
///
/// Enums used for better readability
public enum Cycle {
    TRUE,
    FALSE,

    ;

    public boolean value() {
        return this == TRUE;
    }
}
