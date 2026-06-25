package io.github.plixo2.sodalite.io.image;

import lombok.Getter;

/// Arguments for [ImageWriter]
public sealed interface ImageFormat {

    static Png PNG() {
        return Png.INSTANCE;
    }

    static Bmp BMP() {
        return Bmp.INSTANCE;
    }

    static Hdr HDR() {
        return Hdr.INSTANCE;
    }

    static Jpg JPG(int quality) {
        return new Jpg(quality);
    }

    static Tga TGA(boolean rle) {
        return rle ? Tga.TRUE : Tga.FALSE;
    }

    final class Png implements ImageFormat {
        private static final Png INSTANCE = new Png();
        private Png() {}
    }

    final class Bmp implements ImageFormat {
        private static final Bmp INSTANCE = new Bmp();
        private Bmp() {}
    }

    final class Hdr implements ImageFormat {
        private static final Hdr INSTANCE = new Hdr();
        private Hdr() {}
    }

    final class Jpg implements ImageFormat {
        @Getter
        private final int quality;

        private Jpg(int quality) {
            this.quality = quality;
        }
    }

    final class Tga implements ImageFormat {
        private static final Tga TRUE = new Tga(true);
        private static final Tga FALSE = new Tga(false);
        @Getter
        private final boolean rle;

        private Tga(boolean rle) {
            this.rle = rle;
        }
    }
}
