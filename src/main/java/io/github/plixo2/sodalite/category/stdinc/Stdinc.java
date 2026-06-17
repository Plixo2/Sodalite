package io.github.plixo2.sodalite.category.stdinc;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlCategory CategoryStdinc
public class Stdinc {
    private Stdinc() {}

    /// @sdlAPI SDL_FLT_EPSILON
    public static final float FLOAT_EPSILON = 1.1920928955078125e-07F;

    /// @sdlAPI SDL_PI_D
    public static final double PI_D = 3.141592653589793238462643383279502884;

    /// @sdlAPI SDL_PI_F
    public static final float PI_F = 3.141592653589793238462643383279502884F;

    /// @sdlAPI SDL_max
    public static int max(int a, int b) {
        return Math.max(a, b);
    }
    /// @sdlAPI SDL_max
    public static double max(double a, double b) {
        return Math.max(a, b);
    }
    /// @sdlAPI SDL_max
    public static long max(long a, long b) {
        return Math.max(a, b);
    }
    /// @sdlAPI SDL_max
    public static float max(float a, float b) {
        return Math.max(a, b);
    }
    /// @sdlAPI SDL_max
    public static short max(short a, short b) {
        return (short) Math.max(a, b);
    }
    /// @sdlAPI SDL_max
    public static byte max(byte a, byte b) {
        return (byte) Math.max(a, b);
    }

    /// @sdlAPI SDL_min
    public static int min(int a, int b) {
        return Math.min(a, b);
    }
    /// @sdlAPI SDL_min
    public static double min(double a, double b) {
        return Math.min(a, b);
    }
    /// @sdlAPI SDL_min
    public static long min(long a, long b) {
        return Math.min(a, b);
    }
    /// @sdlAPI SDL_min
    public static float min(float a, float b) {
        return Math.min(a, b);
    }
    /// @sdlAPI SDL_min
    public static short min(short a, short b) {
        return (short) Math.min(a, b);
    }
    /// @sdlAPI SDL_min
    public static byte min(byte a, byte b) {
        return (byte) Math.min(a, b);
    }

    /// @sdlAPI SDL_abs



    /// @sdlAPI SDL_clamp
    public static int clamp(int x, int a, int b) {
        return Math.max(a, Math.min(x, b));
    }
    /// @sdlAPI SDL_clamp
    public static double clamp(double x, double a, double b) {
        return Math.max(a, Math.min(x, b));
    }
    /// @sdlAPI SDL_clamp
    public static long clamp(long x, long a, long b) {
        return Math.max(a, Math.min(x, b));
    }
    /// @sdlAPI SDL_clamp
    public static float clamp(float x, float a, float b) {
        return Math.max(a, Math.min(x, b));
    }
    /// @sdlAPI SDL_clamp
    public static short clamp(short x, short a, short b) {
        return (short) Math.max(a, Math.min(x, b));
    }
    /// @sdlAPI SDL_clamp
    public static byte clamp(byte x, byte a, byte b) {
        return (byte) Math.max(a, Math.min(x, b));
    }



}
