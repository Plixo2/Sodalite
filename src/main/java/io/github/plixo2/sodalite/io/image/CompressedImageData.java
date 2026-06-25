package io.github.plixo2.sodalite.io.image;

import java.lang.foreign.MemorySegment;

public record CompressedImageData<T>(
        int width,
        int height,
        T format,
        MemorySegment data
) {


}