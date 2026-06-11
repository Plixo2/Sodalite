package io.github.plixo2.sodalite;

import io.github.plixo2.sodalite.category.error.Error;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.MemorySegment;
import java.util.Objects;
import java.util.function.Consumer;

public final class Internal {
    private static final long U32_MAX = 0xFFFFFFFFL;

    /// Checks are for validating external SDL calls
    private final static boolean CHECKS_ENABLED;
    private final static boolean CHECKS_PANIC;

    public final static boolean ASSERTIONS_ENABLED;

    static {
        CHECKS_ENABLED = !Boolean.getBoolean("sodalite.disable.checks");
        CHECKS_PANIC = !Boolean.getBoolean("sodalite.disable.checks.panic");

        ASSERTIONS_ENABLED = !Boolean.getBoolean("sodalite.disable.assertions");
    }

    private static Consumer<String> onError =
            CHECKS_PANIC
            ? error -> { throw new SDL3Exception(error); }
            : error -> { System.err.println("SDL3 Error: " + error); };


    private Internal() {}


    public static void assertU32(long value, String name) {
        if (ASSERTIONS_ENABLED && !isU32(value)) {
            throw new AssertionError("Assertion failed " +
                    "(0 <= " + value + " <= " + U32_MAX + ")" +
                    ": '" + name + "' must fit into a unsigned 32-bit integer"
            );
        }
    }
    public static boolean isU32(long value) {
        return value >= 0 && value <= U32_MAX;
    }

    public static void setSDLCheckErrorFunction(Consumer<String> onError) {
        Internal.onError = Objects.requireNonNull(onError, "Error function");
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

    /// Only use for validating SDL calls
    public static MemorySegment check(MemorySegment segment) {
        Objects.requireNonNull(segment, "MemorySegment itself must not be null");
        check(segment.address());
        return segment;
    }
    /// Only use for validating SDL calls
    public static void check(long address) {
        check(address != 0);
    }
    /// Only use for validating SDL calls
    public static void check(boolean success) {
        if (!CHECKS_ENABLED || success) {
            return;
        }

        var error = Error.getError();
        onError.accept(error);
    }


    @Contract("_, _, !null -> !null; _, _, null -> null")
    public static <T extends Enum<T>> T enumFromCode(Class<T> enumClass, int ordinal, @Nullable T defaultValue) {
        var values = enumClass.getEnumConstants();
        if (ordinal < 0 || ordinal >= values.length) {
            return defaultValue;
        }
        return values[ordinal];
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
