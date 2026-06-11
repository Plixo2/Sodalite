package io.github.plixo2.sodalite.file;

import lombok.Getter;
import org.lwjgl.stb.STBImage;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.file.Path;

public class ImageLoader {

    public sealed interface ImageResult {
        record Ok(
                ImageData data
        ) implements ImageResult {}

        record Error(
                ImageLoadException exception
        ) implements ImageResult {}

        default ImageData orThrow() throws ImageLoadException {
            return switch (this) {
                case Error(var exception) -> throw exception;
                case Ok(var data) -> data;
            };
        }

        default <T extends Throwable> ImageData orThrow(
            ThrowingMap<T> map
        ) throws T {
            return switch (this) {
                case Error(var exception) -> throw map.map(exception);
                case Ok(var data) -> data;
            };
        }

        interface ThrowingMap<T extends Throwable> {
            T map(ImageLoadException exception);
        }
    }

    public record ImageData(
            int width,
            int height,
            ImageChannels channels,
            MemorySegment data
    ) {}

    public static ImageResult load(
            Arena arena,
            Path file,
            ImageDynamicRange dynamicRange,
            ImageChannels desiredChannels
    ) {
        return load(
                arena,
                ImageSource.of(file),
                dynamicRange,
                desiredChannels
        );
    }

    public static ImageResult load(
            Arena arena,
            MemorySegment data,
            ImageDynamicRange dynamicRange,
            ImageChannels desiredChannels
    ) {
        return load(
                arena,
                ImageSource.of(data),
                dynamicRange,
                desiredChannels
        );
    }

    public static ImageResult load(
            Arena arena,
            ImageSource source,
            ImageDynamicRange dynamicRange,
            ImageChannels desiredChannels
    ) {
        try (var tempArena = Arena.ofConfined()) {
            var widthSegment    = tempArena.allocate(ValueLayout.JAVA_INT);
            var heightSegment   = tempArena.allocate(ValueLayout.JAVA_INT);
            var channelsSegment = tempArena.allocate(ValueLayout.JAVA_INT);

            var desiredChannelCount = desiredChannels.count();
            var ptr = stbi_load(
                    source,
                    dynamicRange,
                    desiredChannelCount,
                    widthSegment,
                    heightSegment,
                    channelsSegment
            );

            if (ptr == 0) {
                var reason = STBImage.stbi_failure_reason();
                return new ImageResult.Error(new ImageLoadException(reason));
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

    public sealed interface ImageSource {
        record FilePath(Path path) implements ImageSource {}
        record Memory(MemorySegment data) implements ImageSource {}

        static ImageSource of(Path path) {
            return new FilePath(path);
        }

        static ImageSource of(MemorySegment data) {
            return new Memory(data);
        }
    }


    private static long stbi_load(
            ImageSource source,
            ImageDynamicRange dynamicRange,
            int desiredChannels,

            MemorySegment widthOut,
            MemorySegment heightOut,
            MemorySegment channelsOut
    ) {
        return switch (source) {
            case ImageSource.FilePath(var path) -> {
                var absPath = path.toAbsolutePath();
                try (var arena = Arena.ofConfined()) {
                    var strSegment = arena.allocateFrom(absPath.toString());
                    if (dynamicRange == ImageDynamicRange.HDR) {
                        yield STBImage.nstbi_loadf(
                                strSegment.address(),
                                widthOut.address(),
                                heightOut.address(),
                                channelsOut.address(),
                                desiredChannels
                        );
                    } else {
                        yield STBImage.nstbi_load(
                                strSegment.address(),
                                widthOut.address(),
                                heightOut.address(),
                                channelsOut.address(),
                                desiredChannels
                        );
                    }
                }
            }
            case ImageSource.Memory(var memory) -> {
                if (dynamicRange == ImageDynamicRange.HDR) {
                    yield STBImage.nstbi_loadf_from_memory(
                            memory.address(),
                            (int) memory.byteSize(),
                            widthOut.address(),
                            heightOut.address(),
                            channelsOut.address(),
                            desiredChannels
                    );
                } else {
                    yield STBImage.nstbi_load_from_memory(
                            memory.address(),
                            (int) memory.byteSize(),
                            widthOut.address(),
                            heightOut.address(),
                            channelsOut.address(),
                            desiredChannels
                    );
                }
            }
        };
    }


    public static final class ImageLoadException extends Exception {
        @Getter
        private final String stbiFailureReason;

        private ImageLoadException(String stbiFailureReason) {
            super("Failed to load image: " + stbiFailureReason);
            this.stbiFailureReason = stbiFailureReason;
        }
    }


}
