package io.github.plixo2.sodalite;

import io.github.plixo2.sodalite.category.error.Error;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Objects;

public final class Internal {
    public static final long U32_MAX = 0xFFFFFFFFL;
    public static final long U16_MAX = 0xFFFF;
    public static final long U8_MAX = 0xFF;

    /// Checks are for validating external SDL calls
    private final static boolean CHECKS_ENABLED;

    static {
        CHECKS_ENABLED = !Boolean.getBoolean("sodalite.disableChecks");
    }

    private Internal() {}


    /// @throws IllegalArgumentException if the value is not a valid unsigned 32-bit integer
    public static int assertU32(long value, String name) {
        checkUnsigned(name, value, U32_MAX, 32);
        return (int) value;
    }

    public static boolean isU32(long value) {
        return value >= 0 && value <= U32_MAX;
    }

    /// @throws IllegalArgumentException if the value is not a valid unsigned 16-bit integer
    public static byte assertU8(long value, String name) {
        checkUnsigned(name, value, U8_MAX, 8);
        return (byte) value;
    }

    public static boolean isU8(long value) {
        return value >= 0 && value <= U8_MAX;
    }

    /// @throws IllegalArgumentException if the value is not a valid unsigned 16-bit integer
    public static short assertU16(long value, String name) {
        checkUnsigned(name, value, U16_MAX, 16);
        return (short) value;
    }

    public static boolean isU16(long value) {
        return value >= 0 && value <= U16_MAX;
    }



    public static MemorySegment assertNotNull(MemorySegment segment, String message) {
        Objects.requireNonNull(segment, "MemorySegment itself must not be null");
        if (segment.address() == 0) {
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
    public static long check(long address) {
        check(address != 0);
        return address;
    }
    /// Only use for validating SDL calls
    public static int check(int value) {
        check(value != 0);
        return value;
    }

    /// Only use for validating SDL calls
    public static void check(boolean success) {
        if (success || !CHECKS_ENABLED) {
            return;
        }

        var error = Error.getError();
        throw new SDL3Exception(error);
    }


    @Contract("_, _, !null -> !null")
    public static <T extends Enum<T>> T enumFromCode(Class<T> enumClass, int ordinal, @Nullable T defaultValue) {
        var values = enumClass.getEnumConstants();
        if (ordinal < 0 || ordinal >= values.length) {
            return defaultValue;
        }
        return values[ordinal];
    }

    public static MemorySegment allocNullString(Arena arena, @Nullable String str) {
        if (str == null) {
            return MemorySegment.NULL;
        } else {
            return arena.allocateFrom(str);
        }
    }
    public static @Nullable String getNullString(MemorySegment segment) {
        if (segment.address() == 0) {
            return null;
        } else {
            return segment.getString(0);
        }
    }
    public static String getNullString(MemorySegment segment, String defaultValue) {
        if (segment.address() == 0) {
            return defaultValue;
        } else {
            return segment.getString(0);
        }
    }

    private static void checkUnsigned(String name, long value, long max, int bit) {
        if (value >= 0 && value <= max) {
            return;
        }

        var exceptionMessage =
                "'" + name + "'"
                + " does not fit into a unsigned "
                + bit
                + "-bit integer: ";

        if (value < 0) {
            exceptionMessage += value + " < 0";
        } else {
            exceptionMessage += value + " > " + max;
        }
        throw new IllegalArgumentException(exceptionMessage);
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
