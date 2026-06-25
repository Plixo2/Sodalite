package io.github.plixo2.sodalite.io.image;

import io.github.plixo2.sodalite.category.gpu.TextureFormat;
import io.github.plixo2.sodalite.memory.MemorySource;
import org.lwjgl.stb.STBImage;

import java.lang.foreign.*;

public class ImageLoader {


    public static <T extends Exception> ImageResult load(
            Arena arena,
            MemorySource<T> source,
            ImageDynamicRange dynamicRange,
            ImageChannels desiredChannels
    ) throws T {
        return load(
                arena,
                source,
                dynamicRange,
                desiredChannels,
                false
        );
    }

    public static <T extends Exception> ImageResult load(
            Arena arena,
            MemorySource<T> source,
            ImageDynamicRange dynamicRange,
            ImageChannels desiredChannels,
            boolean flipVertically
    ) throws T {
        STBImage.stbi_set_flip_vertically_on_load(flipVertically);

        try (var tempArena = Arena.ofConfined()) {
            var widthSegment    = tempArena.allocate(ValueLayout.JAVA_INT);
            var heightSegment   = tempArena.allocate(ValueLayout.JAVA_INT);
            var channelsSegment = tempArena.allocate(ValueLayout.JAVA_INT);

            var desiredChannelCount = desiredChannels.count();
            MemorySegment sourceSegment = source.load(tempArena);

            var ptr = stbi_load(
                    sourceSegment,
                    dynamicRange,
                    desiredChannelCount,
                    widthSegment,
                    heightSegment,
                    channelsSegment
            );

            if (ptr == 0) {
                var reason = STBImage.stbi_failure_reason();
                return new ImageResult.Error(new ImageIOException(reason));
            }

            var width            = widthSegment   .get(ValueLayout.JAVA_INT, 0);
            var height           = heightSegment  .get(ValueLayout.JAVA_INT, 0);
            var channels_in_file = channelsSegment.get(ValueLayout.JAVA_INT, 0);

            // according to `STBImage.stbi_load_from_memory`
            var actualChannelsCount = desiredChannelCount != 0 ? desiredChannelCount : channels_in_file;
            var size = width * height * actualChannelsCount * dynamicRange.byteSize();

            var actualChannels = ImageChannels.fromCount(actualChannelsCount);

            var data = MemorySegment
                    .ofAddress(ptr)
                    .reinterpret(
                            size,
                            arena,
                            ref -> STBImage.nstbi_image_free(ref.address())
                    );

            var imageData = new ImageData(
                    width,
                    height,
                    actualChannels,
                    data
            );
            return new ImageResult.Ok(imageData);
        }
    }

    private static long stbi_load(
            MemorySegment memory,
            ImageDynamicRange dynamicRange,
            int desiredChannels,

            MemorySegment widthOut,
            MemorySegment heightOut,
            MemorySegment channelsOut
    ) {

        if (dynamicRange == ImageDynamicRange.HDR) {
            return STBImage.nstbi_loadf_from_memory(
                    memory.address(),
                    (int) memory.byteSize(),
                    widthOut.address(),
                    heightOut.address(),
                    channelsOut.address(),
                    desiredChannels
            );
        } else {
            return STBImage.nstbi_load_from_memory(
                    memory.address(),
                    (int) memory.byteSize(),
                    widthOut.address(),
                    heightOut.address(),
                    channelsOut.address(),
                    desiredChannels
            );
        }
    }


    public static <T extends Exception> CompressedImageResult<ASTCFormat> loadASTC(
            Arena arena,
            MemorySource<T> source
    ) throws T {
        try (var fileArena = Arena.ofConfined()) {
            MemorySegment segment = source.load(fileArena);
            return ASTCLoader.load(arena, segment);
        }
    }

    public static <T extends Exception> CompressedImageResult<TextureFormat> loadDDS(
            Arena arena,
            MemorySource<T> source
    ) throws T {
        try (var fileArena = Arena.ofConfined()) {
            MemorySegment segment = source.load(fileArena);
            return DDSLoader.load(arena, segment);
        }
    }


}
