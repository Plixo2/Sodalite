package io.github.plixo2.sodalite.io.image;

import io.github.plixo2.sodalite.Internal;

public enum ImageChannels {
    UNKNOWN,
    GRAY,
    GRAY_ALPHA,
    RGB,
    RGBA,

    ;

    public static ImageChannels fromCount(int count) {
        return Internal.enumFromCode(ImageChannels.class, count, UNKNOWN);
    }

    public int count() {
        return this.ordinal();
    }
}
