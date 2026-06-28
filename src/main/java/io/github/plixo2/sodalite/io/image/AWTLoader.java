package io.github.plixo2.sodalite.io.image;

import java.awt.image.BufferedImage;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public class AWTLoader {


    static ImageData load(
            Arena arena,
            BufferedImage source,
            ImageDynamicRange dynamicRange,
            ImageChannels desiredChannels,
            boolean flipVertically
    ) {
        var width = source.getWidth();
        var height = source.getHeight();
        var channels = desiredChannels == ImageChannels.UNKNOWN
                ? resolveChannels(source)
                : desiredChannels;

        var bytesPerPixel = channels.count() * dynamicRange.byteSize();

        var size = width * height * bytesPerPixel;
        var data = arena.allocate(size, dynamicRange.byteSize());

        var pixels = source.getRGB(0, 0, width, height, null, 0, width);

        writePackedARGB(
                pixels,
                width,
                height,
                flipVertically,
                data,
                channels,
                dynamicRange
        );

        var imageData = new ImageData(
                width,
                height,
                channels,
                data
        );
        return imageData;
    }

    private static ImageChannels resolveChannels(
            BufferedImage source
    ) {
        var colorModel = source.getColorModel();
        var colorComponents = colorModel.getNumColorComponents();
        var hasAlpha = colorModel.hasAlpha();

        if (colorComponents == 1) {
            return hasAlpha ? ImageChannels.GRAY_ALPHA : ImageChannels.GRAY;
        }

        return hasAlpha ? ImageChannels.RGBA : ImageChannels.RGB;
    }

    private static void writePackedARGB(
            int[] pixels,
            int width,
            int height,
            boolean flipVertically,
            MemorySegment data,
            ImageChannels channels,
            ImageDynamicRange dynamicRange
    ) {
        var bytesPerPixel = channels.count() * dynamicRange.byteSize();

        for (var y = 0; y < height; y++) {
            var sourceY = flipVertically ? height - 1 - y : y;
            for (var x = 0; x < width; x++) {
                var argb = pixels[sourceY * width + x];
                var offset = (long) y * width + x;
                var dataOffset = offset * bytesPerPixel;
                switch (dynamicRange) {
                    case SDR -> {
                        writeARGBPixelSDR(data, dataOffset, channels, argb);
                    }
                    case HDR -> {
                        writeARGBPixelHDR(data, dataOffset, channels, argb);
                    }
                }
            }
        }
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private static void writeARGBPixelSDR(
            MemorySegment data,
            long offset,
            ImageChannels channels,
            int argb
    ) {
        var a = (argb >>> 24) & 0xFF;
        var r = (argb >>> 16) & 0xff;
        var g = (argb >>> 8) & 0xff;
        var b = (argb) & 0xff;

        switch (channels) {
            case GRAY -> {
                data.set(ValueLayout.JAVA_BYTE, offset + 0, (byte) gray(r, g, b));
            }
            case GRAY_ALPHA -> {
                data.set(ValueLayout.JAVA_BYTE, offset + 0, (byte) gray(r, g, b));
                data.set(ValueLayout.JAVA_BYTE, offset + 1, (byte) a);
            }
            case RGB -> {
                data.set(ValueLayout.JAVA_BYTE, offset + 0, (byte) r);
                data.set(ValueLayout.JAVA_BYTE, offset + 1, (byte) g);
                data.set(ValueLayout.JAVA_BYTE, offset + 2, (byte) b);
            }
            case RGBA -> {
                data.set(ValueLayout.JAVA_BYTE, offset + 0, (byte) r);
                data.set(ValueLayout.JAVA_BYTE, offset + 1, (byte) g);
                data.set(ValueLayout.JAVA_BYTE, offset + 2, (byte) b);
                data.set(ValueLayout.JAVA_BYTE, offset + 3, (byte) a);
            }
            case UNKNOWN -> throw new IllegalStateException();
        }
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private static void writeARGBPixelHDR(
            MemorySegment data,
            long offset,
            ImageChannels channels,
            int argb
    ) {
        var a = (argb >>> 24) & 0xFF;
        var r = (argb >>> 16) & 0xff;
        var g = (argb >>> 8) & 0xff;
        var b = (argb) & 0xff;

        switch (channels) {
            case GRAY -> data.set(ValueLayout.JAVA_FLOAT, offset, toFloat(gray(r, g, b)));
            case GRAY_ALPHA -> {
                data.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 0L, toFloat(gray(r, g, b)));
                data.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 1L, toFloat(a));
            }
            case RGB -> {
                data.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 0L, toFloat(r));
                data.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 1L, toFloat(g));
                data.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2L, toFloat(b));
            }
            case RGBA -> {
                data.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 0L, toFloat(r));
                data.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 1L, toFloat(g));
                data.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2L, toFloat(b));
                data.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 3L, toFloat(a));
            }
            case UNKNOWN -> throw new IllegalStateException();
        }
    }

    /// [`stbi__compute_y`](https://github.com/nothings/stb/blob/31c1ad37456438565541f4919958214b6e762fb4/stb_image.h#L1746)
    private static int gray(int r, int g, int b) {
        return (r * 77 + g * 150 + b * 29) >>> 8;
    }

    private static float toFloat(int value) {
        return value / 255f;
    }

}
