package io.github.plixo2.sodalite.file.image;

import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageWrite;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.file.Path;

public class ImageWriter {

    public static void write(
            Path path,
            ImageFormat format,
            ImageData data
    ) throws IOException {
        write(path, format, data, false);
    }

    public static void write(
            Path path,
            ImageFormat format,
            ImageData data,
            boolean flipVertically
    ) throws IOException {
        STBImage.stbi_set_flip_vertically_on_load(flipVertically);

        try (var arena = Arena.ofConfined()) {
            var absPath = path.toAbsolutePath();
            var strSegment = arena.allocateFrom(absPath.toString());
            var status = write(format, data, strSegment);
            if (status == 0) {
                throw new IOException("Failed to write image to " + absPath);
            }
        }

    }

    private static int write(
            ImageFormat format,
            ImageData data,
            MemorySegment strSegment
    ) {
        return switch (format) {
            case ImageFormat.Png _ -> {
                yield STBImageWrite.nstbi_write_png(
                        strSegment.address(),
                        data.width(),
                        data.height(),
                        data.channels().count(),
                        data.data().address(),
                        0
                );
            }
            case ImageFormat.Bmp _ -> {
                yield STBImageWrite.nstbi_write_bmp(
                        strSegment.address(),
                        data.width(),
                        data.height(),
                        data.channels().count(),
                        data.data().address()
                );
            }
            case ImageFormat.Tga _ -> {
                yield STBImageWrite.nstbi_write_tga(
                        strSegment.address(),
                        data.width(),
                        data.height(),
                        data.channels().count(),
                        data.data().address()
                );
            }
            case ImageFormat.Jpg _ -> {
                yield STBImageWrite.nstbi_write_jpg(
                        strSegment.address(),
                        data.width(),
                        data.height(),
                        data.channels().count(),
                        data.data().address(),
                        0
                );
            }
            case ImageFormat.Hdr _ -> {
                yield STBImageWrite.nstbi_write_hdr(
                        strSegment.address(),
                        data.width(),
                        data.height(),
                        data.channels().count(),
                        data.data().address()
                );
            }
        };

    }
}
