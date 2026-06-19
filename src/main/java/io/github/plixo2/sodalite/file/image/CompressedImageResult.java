package io.github.plixo2.sodalite.file.image;

public sealed interface CompressedImageResult<F> {
    record Ok<F>(
            CompressedImageData<F> data
    ) implements CompressedImageResult<F> {}

    record Error<T>(
            ImageIOException exception
    ) implements CompressedImageResult<T> {}

    default CompressedImageData<F> orThrow() throws ImageIOException {
        return switch (this) {
            case Error(var exception) -> throw exception;
            case Ok(var data) -> data;
        };
    }

    default <T extends Throwable> CompressedImageData<F> orThrow(
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