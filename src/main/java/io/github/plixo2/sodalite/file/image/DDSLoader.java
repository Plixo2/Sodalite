package io.github.plixo2.sodalite.file.image;

import io.github.plixo2.sodalite.category.gpu.TextureFormat;
import io.github.plixo2.sodalite.file.FileIO;
import io.github.plixo2.sodalite.memory.Layouts;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.nio.file.Path;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

public class DDSLoader {

    /// typedef struct DDS_PIXELFORMAT {
    ///     int dwSize;
    ///     int dwFlags;
    ///     int dwFourCC;
    ///     int dwRGBBitCount;
    ///     int dwRBitMask;
    ///     int dwGBitMask;
    ///     int dwBBitMask;
    ///     int dwABitMask;
    /// } DDS_PIXELFORMAT;
    private static final StructLayout DDS_PIXELFORMAT = MemoryLayout.structLayout(
            Layouts.INT.withName("dwSize"),
            Layouts.INT.withName("dwFlags"),
            Layouts.INT.withName("dwFourCC"),
            Layouts.INT.withName("dwRGBBitCount"),
            Layouts.INT.withName("dwRBitMask"),
            Layouts.INT.withName("dwGBitMask"),
            Layouts.INT.withName("dwBBitMask"),
            Layouts.INT.withName("dwABitMask")
    );

    /// typedef struct DDS_HEADER {
    ///     int dwMagic;
    ///     int dwSize;
    ///     int dwFlags;
    ///     int dwHeight;
    ///     int dwWidth;
    ///     int dwPitchOrLinearSize;
    ///     int dwDepth;
    ///     int dwMipMapCount;
    ///     int dwReserved1[11];
    ///     DDS_PIXELFORMAT ddspf;
    ///     int dwCaps;
    ///     int dwCaps2;
    ///     int dwCaps3;
    ///     int dwCaps4;
    ///     int dwReserved2;
    /// } DDS_HEADER;
    private static final StructLayout DDS_HEADER = MemoryLayout.structLayout(
            Layouts.INT.withName("dwMagic"),
            Layouts.INT.withName("dwSize"),
            Layouts.INT.withName("dwFlags"),
            Layouts.INT.withName("dwHeight"),
            Layouts.INT.withName("dwWidth"),
            Layouts.INT.withName("dwPitchOrLinearSize"),
            Layouts.INT.withName("dwDepth"),
            Layouts.INT.withName("dwMipMapCount"),
            Layouts.intArray(11).withName("dwReserved1"),
            DDS_PIXELFORMAT.withName("ddspf"),
            Layouts.INT.withName("dwCaps"),
            Layouts.INT.withName("dwCaps2"),
            Layouts.INT.withName("dwCaps3"),
            Layouts.INT.withName("dwCaps4"),
            Layouts.INT.withName("dwReserved2")
    );

    /// typedef struct DDS_HEADER_DXT10 {
    ///     int dxgiFormat;
    ///     int resourceDimension;
    ///     unsigned int miscFlag;
    ///     unsigned int arraySize;
    ///     unsigned int miscFlags2;
    /// } DDS_HEADER_DXT10;
    private static final StructLayout DDS_HEADER_DXT10 = MemoryLayout.structLayout(
            Layouts.INT.withName("dxgiFormat"),
            Layouts.INT.withName("resourceDimension"),
            Layouts.UINT.withName("miscFlag"),
            Layouts.UINT.withName("arraySize"),
            Layouts.UINT.withName("miscFlags2")
    );


    private static final long HEADER_SIZE       = DDS_HEADER.byteSize();
    private static final long DXT10_HEADER_SIZE = DDS_HEADER_DXT10.byteSize();

    private static final long MAGIC_OFFSET                = DDS_HEADER.byteOffset(groupElement("dwMagic"));
    private static final long WIDTH_OFFSET                = DDS_HEADER.byteOffset(groupElement("dwWidth"));
    private static final long HEIGHT_OFFSET               = DDS_HEADER.byteOffset(groupElement("dwHeight"));
    private static final long PITCH_OR_LINEAR_SIZE_OFFSET = DDS_HEADER.byteOffset(groupElement("dwPitchOrLinearSize"));
    private static final long PIXELFORMAT_FLAGS_OFFSET    = DDS_HEADER.byteOffset(groupElement("ddspf"), groupElement("dwFlags"));
    private static final long PIXELFORMAT_FOURCC_OFFSET   = DDS_HEADER.byteOffset(groupElement("ddspf"), groupElement("dwFourCC"));
    private static final long DXGI_FORMAT_OFFSET = DDS_HEADER_DXT10.byteOffset(groupElement("dxgiFormat"));

    private static final int DDS_MAGIC   = 0x20534444; // "DDS "
    private static final int DDPF_FOURCC = 0x4;
    private static final int FOURCC_DX10 = 0x30315844; // "DX10"


    static CompressedImageResult<TextureFormat> load(Arena arena, MemorySegment data) {
        long fileLength = data.byteSize();

        if (fileLength < HEADER_SIZE) {
            return new CompressedImageResult.Error<>(new ImageIOException("Too small for DDS header"));
        }

        byte[] header = new byte[Math.toIntExact(HEADER_SIZE)];
        MemorySegment.ofArray(header).copyFrom(data.asSlice(0, HEADER_SIZE));

        int magic = readInt32LE(header, (int) MAGIC_OFFSET);
        if (magic != DDS_MAGIC) {
            return new CompressedImageResult.Error<>(new ImageIOException("Invalid magic"));
        }

        int pixelFormatFlags  = readInt32LE(header, (int) PIXELFORMAT_FLAGS_OFFSET);
        int pixelFormatFourCC = readInt32LE(header, (int) PIXELFORMAT_FOURCC_OFFSET);
        boolean hasDX10Header = pixelFormatFlags == DDPF_FOURCC && pixelFormatFourCC == FOURCC_DX10;

        long dataOffset = HEADER_SIZE;
        TextureFormat format;

        if (hasDX10Header) {
            if (fileLength < HEADER_SIZE + DXT10_HEADER_SIZE) {
                return new CompressedImageResult.Error<>(new ImageIOException("Too small for DDS DX10 header"));
            }
            byte[] dxt10Header = new byte[Math.toIntExact(DXT10_HEADER_SIZE)];
            MemorySegment.ofArray(dxt10Header).copyFrom(data.asSlice(HEADER_SIZE, DXT10_HEADER_SIZE));

            int dxgiFormat = readInt32LE(dxt10Header, (int) DXGI_FORMAT_OFFSET);
            format = bcFormatFromDxgiFormat(dxgiFormat);
            if (format == null) {
                return new CompressedImageResult.Error<>(new ImageIOException("Unsupported DXGI format " + dxgiFormat));
            }
            dataOffset += DXT10_HEADER_SIZE;
        } else {
            format = bcFormatFromFourCC(pixelFormatFourCC);
            if (format == null) {
                return new CompressedImageResult.Error<>(new ImageIOException(
                        "Unsupported or non-BCn FourCC 0x" + Integer.toHexString(pixelFormatFourCC)));
            }
        }

        int width  = readInt32LE(header, (int) WIDTH_OFFSET);
        int height = readInt32LE(header, (int) HEIGHT_OFFSET);
        long imageDataLength = readInt32LE(header, (int) PITCH_OR_LINEAR_SIZE_OFFSET) & 0xFFFFFFFFL;

        long requiredSize = dataOffset + imageDataLength;
        if (fileLength < requiredSize) {
            return new CompressedImageResult.Error<>(new ImageIOException("Too small for DDS data"));
        }

        MemorySegment imageData = arena.allocate(imageDataLength);
        MemorySegment.copy(data, dataOffset, imageData, 0, imageDataLength);

        var dataObject = new CompressedImageData<>(
                width,
                height,
                format,
                imageData
        );
        return new CompressedImageResult.Ok<>(dataObject);
    }

    private static int readInt32LE(byte[] bytes, int offset) {
        return (bytes[offset] & 0xFF)
                | ((bytes[offset + 1] & 0xFF) << 8)
                | ((bytes[offset + 2] & 0xFF) << 16)
                | ((bytes[offset + 3] & 0xFF) << 24);
    }


    private static int fourCC(String code) {
        return (code.charAt(0) & 0xFF)
                | ((code.charAt(1) & 0xFF) << 8)
                | ((code.charAt(2) & 0xFF) << 16)
                | ((code.charAt(3) & 0xFF) << 24);
    }

    private static final int FOURCC_DXT1 = fourCC("DXT1");
    private static final int FOURCC_DXT2 = fourCC("DXT2");
    private static final int FOURCC_DXT3 = fourCC("DXT3");
    private static final int FOURCC_DXT4 = fourCC("DXT4");
    private static final int FOURCC_DXT5 = fourCC("DXT5");
    private static final int FOURCC_ATI1 = fourCC("ATI1"); // BC4, AMD convention
    private static final int FOURCC_BC4U = fourCC("BC4U"); // BC4, alternate convention
    private static final int FOURCC_ATI2 = fourCC("ATI2"); // BC5, AMD convention
    private static final int FOURCC_BC5U = fourCC("BC5U"); // BC5, alternate convention

    private static TextureFormat bcFormatFromFourCC(int fourCC) {
        if (fourCC == FOURCC_DXT1) return TextureFormat.BC1_RGBA_UNORM;
        if (fourCC == FOURCC_DXT2 || fourCC == FOURCC_DXT3) return TextureFormat.BC2_RGBA_UNORM;
        if (fourCC == FOURCC_DXT4 || fourCC == FOURCC_DXT5) return TextureFormat.BC3_RGBA_UNORM;
        if (fourCC == FOURCC_ATI1 || fourCC == FOURCC_BC4U) return TextureFormat.BC4_R_UNORM;
        if (fourCC == FOURCC_ATI2 || fourCC == FOURCC_BC5U) return TextureFormat.BC5_RG_UNORM;
        return null;
    }

    // values straight from Microsoft's DXGI_FORMAT enum (dxgiformat.h), stable since D3D10/11
    private static final int DXGI_FORMAT_BC1_UNORM      = 71;
    private static final int DXGI_FORMAT_BC1_UNORM_SRGB = 72;
    private static final int DXGI_FORMAT_BC2_UNORM      = 74;
    private static final int DXGI_FORMAT_BC2_UNORM_SRGB = 75;
    private static final int DXGI_FORMAT_BC3_UNORM      = 77;
    private static final int DXGI_FORMAT_BC3_UNORM_SRGB = 78;
    private static final int DXGI_FORMAT_BC4_UNORM      = 80;
    private static final int DXGI_FORMAT_BC5_UNORM      = 83;
    private static final int DXGI_FORMAT_BC6H_UF16       = 95;
    private static final int DXGI_FORMAT_BC6H_SF16       = 96;
    private static final int DXGI_FORMAT_BC7_UNORM       = 98;
    private static final int DXGI_FORMAT_BC7_UNORM_SRGB  = 99;

    private static TextureFormat bcFormatFromDxgiFormat(int dxgiFormat) {
        return switch (dxgiFormat) {
            case DXGI_FORMAT_BC1_UNORM      -> TextureFormat.BC1_RGBA_UNORM;
            case DXGI_FORMAT_BC1_UNORM_SRGB -> TextureFormat.BC1_RGBA_UNORM_SRGB;
            case DXGI_FORMAT_BC2_UNORM      -> TextureFormat.BC2_RGBA_UNORM;
            case DXGI_FORMAT_BC2_UNORM_SRGB -> TextureFormat.BC2_RGBA_UNORM_SRGB;
            case DXGI_FORMAT_BC3_UNORM      -> TextureFormat.BC3_RGBA_UNORM;
            case DXGI_FORMAT_BC3_UNORM_SRGB -> TextureFormat.BC3_RGBA_UNORM_SRGB;
            case DXGI_FORMAT_BC4_UNORM      -> TextureFormat.BC4_R_UNORM;
            case DXGI_FORMAT_BC5_UNORM      -> TextureFormat.BC5_RG_UNORM;
            case DXGI_FORMAT_BC6H_UF16      -> TextureFormat.BC6H_RGB_UFLOAT;
            case DXGI_FORMAT_BC6H_SF16      -> TextureFormat.BC6H_RGB_FLOAT;
            case DXGI_FORMAT_BC7_UNORM      -> TextureFormat.BC7_RGBA_UNORM;
            case DXGI_FORMAT_BC7_UNORM_SRGB -> TextureFormat.BC7_RGBA_UNORM_SRGB;
            default -> null;
        };
    }

}
