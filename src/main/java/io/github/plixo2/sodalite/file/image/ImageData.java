package io.github.plixo2.sodalite.file.image;

import java.lang.foreign.MemorySegment;

public record ImageData(
        int width,
        int height,
        ImageChannels channels,
        MemorySegment data
) {

    public static ImageData of(
            int width,
            int height,
            ImageChannels channels,
            MemorySegment data
    ) {
        return new ImageData(width, height, channels, data);
    }

}