package io.github.plixo2.sodalite.io.image;

public enum ImageDynamicRange {
    SDR,
    HDR,

    ;

    public int byteSize() {
        return switch (this) {
            case SDR -> Byte.BYTES;
            case HDR -> Integer.BYTES;
        };
    }

}
