package io.github.plixo2.sodalite.category.gpu;


/// @apiNote SDL_GPUSampleCount
public enum SampleCount {
    COUNT_1,
    COUNT_2,
    COUNT_4,
    COUNT_8,

    ;

    public int code() {
        return this.ordinal();
    }
}