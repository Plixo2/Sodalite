package io.github.plixo2.sodalite.category.cpuinfo;


/// Wrapper for `SDL_GetSystemPageSize`
public sealed interface PageSize {
    record Value(int value) implements PageSize {}
    record Unknown() implements PageSize {
        private static final Unknown INSTANCE = new Unknown();
    }

    static PageSize fromValue(int value) {
        if (value == 0) {
            return Unknown.INSTANCE;
        } else {
            return new Value(value);
        }
    }

    default int or(int defaultValue) {
        return switch (this) {
            case Value(var v) -> v;
            case Unknown() -> defaultValue;
        };
    }
    default int or4kb() {
        return or(4096);
    }

}
