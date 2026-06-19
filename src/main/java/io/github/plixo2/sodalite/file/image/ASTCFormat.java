package io.github.plixo2.sodalite.file.image;

import io.github.plixo2.sodalite.category.gpu.TextureFormat;

public record ASTCFormat(int x, int y) {

    public TextureFormat asUNORM() {
        var key = (this.x << 8) | this.y;
        return switch (key) {
            case (4  << 8) | 4  -> TextureFormat.ASTC_4x4_UNORM;
            case (5  << 8) | 4  -> TextureFormat.ASTC_5x4_UNORM;
            case (5  << 8) | 5  -> TextureFormat.ASTC_5x5_UNORM;
            case (6  << 8) | 5  -> TextureFormat.ASTC_6x5_UNORM;
            case (6  << 8) | 6  -> TextureFormat.ASTC_6x6_UNORM;
            case (8  << 8) | 5  -> TextureFormat.ASTC_8x5_UNORM;
            case (8  << 8) | 6  -> TextureFormat.ASTC_8x6_UNORM;
            case (8  << 8) | 8  -> TextureFormat.ASTC_8x8_UNORM;
            case (10 << 8) | 5  -> TextureFormat.ASTC_10x5_UNORM;
            case (10 << 8) | 6  -> TextureFormat.ASTC_10x6_UNORM;
            case (10 << 8) | 8  -> TextureFormat.ASTC_10x8_UNORM;
            case (10 << 8) | 10 -> TextureFormat.ASTC_10x10_UNORM;
            case (12 << 8) | 10 -> TextureFormat.ASTC_12x10_UNORM;
            case (12 << 8) | 12 -> TextureFormat.ASTC_12x12_UNORM;
            default -> throw new IllegalStateException();
        };
    }

    public TextureFormat asSRGB() {
        var key = (this.x << 8) | this.y;
        return switch (key) {
            case (4  << 8) | 4  -> TextureFormat.ASTC_4x4_UNORM_SRGB;
            case (5  << 8) | 4  -> TextureFormat.ASTC_5x4_UNORM_SRGB;
            case (5  << 8) | 5  -> TextureFormat.ASTC_5x5_UNORM_SRGB;
            case (6  << 8) | 5  -> TextureFormat.ASTC_6x5_UNORM_SRGB;
            case (6  << 8) | 6  -> TextureFormat.ASTC_6x6_UNORM_SRGB;
            case (8  << 8) | 5  -> TextureFormat.ASTC_8x5_UNORM_SRGB;
            case (8  << 8) | 6  -> TextureFormat.ASTC_8x6_UNORM_SRGB;
            case (8  << 8) | 8  -> TextureFormat.ASTC_8x8_UNORM_SRGB;
            case (10 << 8) | 5  -> TextureFormat.ASTC_10x5_UNORM_SRGB;
            case (10 << 8) | 6  -> TextureFormat.ASTC_10x6_UNORM_SRGB;
            case (10 << 8) | 8  -> TextureFormat.ASTC_10x8_UNORM_SRGB;
            case (10 << 8) | 10 -> TextureFormat.ASTC_10x10_UNORM_SRGB;
            case (12 << 8) | 10 -> TextureFormat.ASTC_12x10_UNORM_SRGB;
            case (12 << 8) | 12 -> TextureFormat.ASTC_12x12_UNORM_SRGB;
            default -> throw new IllegalStateException();
        };
    }

    public TextureFormat asFLOAT() {
        var key = (this.x << 8) | this.y;
        return switch (key) {
            case (4  << 8) | 4  -> TextureFormat.ASTC_4x4_FLOAT;
            case (5  << 8) | 4  -> TextureFormat.ASTC_5x4_FLOAT;
            case (5  << 8) | 5  -> TextureFormat.ASTC_5x5_FLOAT;
            case (6  << 8) | 5  -> TextureFormat.ASTC_6x5_FLOAT;
            case (6  << 8) | 6  -> TextureFormat.ASTC_6x6_FLOAT;
            case (8  << 8) | 5  -> TextureFormat.ASTC_8x5_FLOAT;
            case (8  << 8) | 6  -> TextureFormat.ASTC_8x6_FLOAT;
            case (8  << 8) | 8  -> TextureFormat.ASTC_8x8_FLOAT;
            case (10 << 8) | 5  -> TextureFormat.ASTC_10x5_FLOAT;
            case (10 << 8) | 6  -> TextureFormat.ASTC_10x6_FLOAT;
            case (10 << 8) | 8  -> TextureFormat.ASTC_10x8_FLOAT;
            case (10 << 8) | 10 -> TextureFormat.ASTC_10x10_FLOAT;
            case (12 << 8) | 10 -> TextureFormat.ASTC_12x10_FLOAT;
            case (12 << 8) | 12 -> TextureFormat.ASTC_12x12_FLOAT;
            default -> throw new IllegalStateException();
        };
    }

    static boolean isValid(int x, int y) {
        var key = (x << 8) | y;
        return switch (key) {
            case (4  << 8) | 4 ,
                 (5  << 8) | 4 ,
                 (5  << 8) | 5 ,
                 (6  << 8) | 5 ,
                 (6  << 8) | 6 ,
                 (8  << 8) | 5 ,
                 (8  << 8) | 6 ,
                 (8  << 8) | 8 ,
                 (10 << 8) | 5 ,
                 (10 << 8) | 6 ,
                 (10 << 8) | 8 ,
                 (10 << 8) | 10,
                 (12 << 8) | 10,
                 (12 << 8) | 12 -> true;
            default -> false;
        };
    }

}
