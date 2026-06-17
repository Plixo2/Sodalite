package io.github.plixo2.sodalite;

import io.github.plixo2.sodalite.category.error.Error;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.IntConsumer;

public final class Internal {
    private static final long U32_MAX = 0xFFFFFFFFL;

    /// Checks are for validating external SDL calls
    private final static boolean CHECKS_ENABLED;

    public final static boolean ASSERTIONS_ENABLED;

    static {
        CHECKS_ENABLED = !Boolean.getBoolean("sodalite.disableChecks");
        ASSERTIONS_ENABLED = !Boolean.getBoolean("sodalite.disableAssertions");
    }

    private Internal() {}


    public static int assertU32(long value, String name) {
        if (ASSERTIONS_ENABLED && !isU32(value)) {
            throw new AssertionError("Assertion failed " +
                    "(0 <= " + value + " <= " + U32_MAX + ")" +
                    ": '" + name + "' must fit into a unsigned 32-bit integer"
            );
        }
        return (int) value;
    }
    public static boolean isU32(long value) {
        return value >= 0 && value <= U32_MAX;
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
    public static void forEachFlag(int bitset, IntConsumer consumer) {

        int current = bitset;
        while (current != 0) {
            int lowestBit = Integer.lowestOneBit(current);
            consumer.accept(lowestBit);
            current &= ~lowestBit; // remove bit
        }

    }

    public static Iterable<Integer> extractFlags(int bitset, int mask) {
        return extractFlags(bitset & mask);
    }

    public static Iterable<Integer> extractFlags(int bitset) {
        return () -> new Iterator<>() {
            int remaining = bitset;

            @Override
            public boolean hasNext() {
                return this.remaining != 0;
            }

            @Override
            public Integer next() {
                if (this.remaining == 0) {
                    throw new NoSuchElementException();
                }
                int lowestBit = Integer.lowestOneBit(this.remaining);
                this.remaining &= ~lowestBit; // remove bit
                return lowestBit;
            }
        };
    }

    public static int flagMask(Class<?> mask) {
        if (!mask.isAnnotation()) {
            throw new IllegalArgumentException(
                    "Expected an annotation type, got " + mask.getName()
            );
        }
        var retention = mask.getAnnotation(Retention.class);
        var target = mask.getAnnotation(Target.class);
        if (retention == null || retention.value() != RetentionPolicy.SOURCE) {
            throw new IllegalArgumentException(
                    "Expected an annotation with @Retention(SOURCE), on MagicConstant class "
                    + "'" + mask.getName() + "'"
            );
        }
        if (target == null) {
            throw new IllegalArgumentException(
                    "Expected an annotation with @Target, on MagicConstant class "
                            + "'" + mask.getName() + "'"
            );
        }

        return getConstantMask(mask);
    }

    private static int getConstantMask(Class<?> mask) {
        int result = 0;
        for (var field : mask.getDeclaredFields()) {
            if (field.getName().equals("MASK")) {
                continue;
            }

            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers)) {
                continue;
            }
            if (field.getType() != int.class) {
                continue;
            }
            try {
                result |= field.getInt(null);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Could not read field " + field, e);
            }
        }
        return result;
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
