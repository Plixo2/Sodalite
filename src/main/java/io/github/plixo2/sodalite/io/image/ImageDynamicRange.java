package io.github.plixo2.sodalite.io.image;

public enum ImageDynamicRange {
    SDR,
    HDR,

    ;

    public long byteSize() {
        return switch (this) {
            case SDR -> Byte.BYTES;
            case HDR -> Float.BYTES;
        };
    }

}
