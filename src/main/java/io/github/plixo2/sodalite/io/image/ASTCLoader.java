package io.github.plixo2.sodalite.io.image;

import io.github.plixo2.sodalite.memory.Layouts;

import java.lang.foreign.*;

public class ASTCLoader {

    /// typedef struct ASTCHeader
    /// {
    ///     Uint8 magic[4];
    ///     Uint8 blockX;
    ///     Uint8 blockY;
    ///     Uint8 blockZ;
    ///     Uint8 dimX[3];
    ///     Uint8 dimY[3];
    ///     Uint8 dimZ[3];
    /// } ASTCHeader;
    private static final StructLayout ASTC_HEADER = MemoryLayout.structLayout(
            Layouts.UINT8_4.withName("magic"),
            Layouts.UINT8.withName("blockX"),
            Layouts.UINT8.withName("blockY"),
            Layouts.UINT8.withName("blockZ"),
            Layouts.UINT8_3.withName("dimX"),
            Layouts.UINT8_3.withName("dimY"),
            Layouts.UINT8_3.withName("dimZ")
    );

    static CompressedImageResult<ASTCFormat> load(
            Arena arena,
            MemorySegment data
    ) {
        var fileLength = data.byteSize();
        var headerSize = ASTC_HEADER.byteSize();

        if (fileLength < headerSize) {
            return new CompressedImageResult.Error<>(new ImageIOException("Too small for astc image"));
        }
        var header = data.asSlice(0, headerSize).toArray(ValueLayout.JAVA_BYTE);
        int m0 = header[0] & 0xFF;
        int m1 = header[1] & 0xFF;
        int m2 = header[2] & 0xFF;
        int m3 = header[3] & 0xFF;

        if (m0 != 0x13 || m1 != 0xAB || m2 != 0xA1 || m3 != 0x5C) {
            return new CompressedImageResult.Error<>(new ImageIOException("Invalid magic"));
        }
        var width = uint24(header[7], header[8], header[9]);
        var height = uint24(header[10], header[11], header[12]);

        int blockX = header[4] & 0xFF;
        int blockY = header[5] & 0xFF;
        int blockZ = header[6] & 0xFF;

        if (blockX == 0 || blockY == 0) {
            return new CompressedImageResult.Error<>(new ImageIOException("Invalid block dimensions"));
        }
        if (blockZ != 1) {
            return new CompressedImageResult.Error<>(new ImageIOException("3D ASTC textures (blockZ=" + blockZ + ") are not supported"));
        }

        long blockCountX = ((long) width  + blockX - 1) / blockX;
        long blockCountY = ((long) height + blockY - 1) / blockY;

        var dataLength = blockCountX * blockCountY * 16L;

        var requiredSize = headerSize + dataLength;

        if (fileLength < requiredSize) {
            return new CompressedImageResult.Error<>(new ImageIOException("Too small for astc data"));
        }
        if (!ASTCFormat.isValid(blockX, blockY)) {
            return new CompressedImageResult.Error<>(new ImageIOException("Unsupported block dimensions: " + blockX + "x" + blockY));
        }

        var format = new ASTCFormat(blockX, blockY);

        MemorySegment imageData = arena.allocate(dataLength);
        MemorySegment.copy(data, headerSize, imageData, 0, dataLength);

        var dataObject = new CompressedImageData<>(
                width,
                height,
                format,
                imageData
        );
        return new CompressedImageResult.Ok<>(dataObject);
    }

    private static int uint24(byte b0, byte b1, byte b2) {
        return (b0 & 0xFF) | ((b1 & 0xFF) << 8) | ((b2 & 0xFF) << 16);
    }


}
