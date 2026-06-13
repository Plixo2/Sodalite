package io.github.plixo2.sodalite.category.messagebox;

import org.joml.Math;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.libsdl.sdl.SDL_MessageBoxColor;

import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_MessageBoxColor
public record MessageBoxColor(
        int r,
        int g,
        int b
) {

    public MessageBoxColor(int r, int g, int b) {
        this.r = Math.clamp(r, 0, 255);
        this.g = Math.clamp(g, 0, 255);
        this.b = Math.clamp(b, 0, 255);
    }

    public static MessageBoxColor of(int r, int g, int b) {
        return new MessageBoxColor(r, g, b);
    }
    public static MessageBoxColor of(int rgb) {
        return MessageBoxColor.of((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
    }
    public static MessageBoxColor of(Vector3i color) {
        return MessageBoxColor.of(color.x, color.y, color.z);
    }
    public static MessageBoxColor of(int[] rgb) {
        if (rgb.length != 3) throw new IllegalArgumentException("Array must have length 3");
        return MessageBoxColor.of(rgb[0], rgb[1], rgb[2]);
    }
    public static MessageBoxColor of(float r, float g, float b) {
        return new MessageBoxColor((int) (r * 255), (int) (g * 255), (int) (b * 255));
    }
    public static MessageBoxColor of(Vector3f color) {
        return MessageBoxColor.of(color.x, color.y, color.z);
    }
    public static MessageBoxColor of(float[] rgb) {
        if (rgb.length != 3) throw new IllegalArgumentException("Array must have length 3");
        return MessageBoxColor.of(rgb[0], rgb[1], rgb[2]);
    }

    void put(MemorySegment segment) {
        SDL_MessageBoxColor.initialize(segment, (byte) this.r, (byte) this.g, (byte) this.b);
    }
}