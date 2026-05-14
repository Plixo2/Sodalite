package io.github.plixo2.sodalite;

import io.github.plixo2.sodalite.error.Error;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.lang.foreign.MemorySegment;
import java.util.Objects;

public final class Internal {
    private final static boolean ASSERTIONS_ENABLED;
    private final static boolean CHECKS_ENABLED;
    private final static boolean CHECKS_PANIC;

    static {
        ASSERTIONS_ENABLED = Boolean.getBoolean("sodalite.assertions");
        CHECKS_ENABLED = Boolean.getBoolean("sodalite.checks");
        CHECKS_PANIC = Boolean.getBoolean("sodalite.checks.panic");
    }

    private static @Nullable PrintStream out = System.err;

    private Internal() {}

    public static void setDebugOutput(@Nullable PrintStream out) {
        Internal.out = out;
    }

    public static void assertTrue(boolean condition, String message) {
        if (ASSERTIONS_ENABLED && !condition) {
            throw new AssertionError(message);
        }
    }
    public static void assertTrue(boolean condition) {
        assertTrue(condition, "Assertion failed");
    }

    public static MemorySegment assertNotNull(MemorySegment segment, String message) {
        Objects.requireNonNull(segment, "MemorySegment itself must not be null");
        if (ASSERTIONS_ENABLED && segment.address() == 0) {
            throw new NullPointerException(message);
        }
        return segment;
    }

    public static MemorySegment assertNotNull(MemorySegment segment) {
        return assertNotNull(segment, "Invalid Address");
    }

    public static void check(MemorySegment segment) {
        Objects.requireNonNull(segment, "MemorySegment itself must not be null");
        check(segment.address());
    }

    public static void check(long address) {
        if (!CHECKS_ENABLED || address != 0) {
            return;
        }

        var error = Error.getError();
        if (CHECKS_PANIC) {
            throw new SDL3Exception(error);
        } else if (out != null) {
            out.println("SDL3 Error: " + error);
        }
    }

    public static void checkDestroyed(SDLResource resource) {
        if (ASSERTIONS_ENABLED && resource.destroyed) {
            var resourceClass = resource.getClass().getSimpleName();
            throw new AssertionError("Resource of type " + resourceClass + " has already been destroyed");
        }
    }



    public static class SDL3Exception extends RuntimeException {

        public SDL3Exception() {
            super();
        }

        public SDL3Exception(
                String message
        ) {
            super(message);
        }

        public SDL3Exception(
                String message,
                Throwable cause
        ) {
            super(message, cause);
        }

        public SDL3Exception(
                Throwable cause
        ) {
            super(cause);
        }

        public SDL3Exception(
                String message,
                Throwable cause,
                boolean enableSuppression,
                boolean writableStackTrace
        ) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

}
