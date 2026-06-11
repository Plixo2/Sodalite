package io.github.plixo2.sodalite.file;

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
