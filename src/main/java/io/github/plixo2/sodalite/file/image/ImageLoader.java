package io.github.plixo2.sodalite.file.image;

import io.github.plixo2.sodalite.category.gpu.TextureFormat;
import org.lwjgl.stb.STBImage;

import java.io.IOException;
import java.lang.foreign.*;

public class ImageLoader {


    public static ImageResult load(
            Arena arena,
            ImageSource source,
            ImageDynamicRange dynamicRange,
            ImageChannels desiredChannels
    ) {
        return load(
                arena,
                source,
                dynamicRange,
                desiredChannels,
                true
        );
    }

    public static ImageResult load(
            Arena arena,
            ImageSource source,
            ImageDynamicRange dynamicRange,
            ImageChannels desiredChannels,
            boolean flipVertically
    ) {
        STBImage.stbi_set_flip_vertically_on_load(flipVertically);

        try (var tempArena = Arena.ofConfined()) {
            var widthSegment    = tempArena.allocate(ValueLayout.JAVA_INT);
            var heightSegment   = tempArena.allocate(ValueLayout.JAVA_INT);
            var channelsSegment = tempArena.allocate(ValueLayout.JAVA_INT);

            var desiredChannelCount = desiredChannels.count();
            MemorySegment sourceSegment;
            try {
                sourceSegment = source.toSegment(tempArena);
            } catch (IOException e) {
                return new ImageResult.Error(new ImageIOException("Failed to read file", e));
            }

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


    public static CompressedImageResult<ASTCFormat> loadASTC(
            Arena arena,
            ImageSource source
    ) {
        try (var fileArena = Arena.ofConfined()) {
            MemorySegment segment;
            try {
                segment = source.toSegment(fileArena);
            } catch (IOException e) {
                return new CompressedImageResult.Error<>(new ImageIOException("Failed to read file", e));
            }

            return ASTCLoader.load(arena, segment);
        }
    }

    public static CompressedImageResult<TextureFormat> loadDDS(
            Arena arena,
            ImageSource source
    ) {
        try (var fileArena = Arena.ofConfined()) {
            MemorySegment segment;
            try {
                segment = source.toSegment(fileArena);
            } catch (IOException e) {
                return new CompressedImageResult.Error<>(new ImageIOException("Failed to read file", e));
            }

            return DDSLoader.load(arena, segment);
        }
    }


}
