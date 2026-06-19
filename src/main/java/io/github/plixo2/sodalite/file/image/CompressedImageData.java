package io.github.plixo2.sodalite.file.image;

import java.lang.foreign.MemorySegment;

public record CompressedImageData<T>(
        int width,
        int height,
        T format,
        MemorySegment data
) {


}