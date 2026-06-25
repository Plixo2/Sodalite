package io.github.plixo2.sodalite.io.image;

public sealed interface ImageResult {
    record Ok(
            ImageData data
    ) implements ImageResult {}

    record Error(
            ImageIOException exception
    ) implements ImageResult {}

    default ImageData orThrow() throws ImageIOException {
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
        T map(ImageIOException exception);
    }
}