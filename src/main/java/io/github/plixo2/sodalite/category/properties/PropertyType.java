package io.github.plixo2.sodalite.category.properties;


import io.github.plixo2.sodalite.Internal;
import org.jetbrains.annotations.Nullable;

/// @sdlAPI SDL_PropertyType
public enum PropertyType {
//    INVALID,
    POINTER,
    STRING,
    NUMBER,
    FLOAT,
    BOOLEAN,

    ;

    public static @Nullable PropertyType fromCode(int code) {
        return Internal.enumFromCode(PropertyType.class, code - 1, null);
    }

    public int code() {
        return this.ordinal() + 1;
    }

}
