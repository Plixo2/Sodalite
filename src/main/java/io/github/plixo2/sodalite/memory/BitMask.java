package io.github.plixo2.sodalite.memory;

import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiFunction;

public sealed abstract class BitMask<T extends Number> {

    @Getter
    protected final T value;

    protected final Map<Number, String> names;

    private BitMask(Map<Number, String> names, T identity, BiFunction<T, T, T> fold) {
        this.value = maskOf(names, identity, fold);
        this.names = names;
    }

    public abstract Iterable<T> bits();

    @Override
    public String toString() {
        return BitMask.toString(this.names);
    }


    public static final class Int extends BitMask<Integer> {

        public Int(Map<Number, String> names) {
            super(names, 0, (a, b) -> a | b);
        }

        public String toString(int mask) {
            return BitMask.toString(this.names, bits(mask));
        }

        @Override
        public Iterable<Integer> bits() {
            return bits(this.value);
        }

    }

    public static final class Long extends BitMask<java.lang.Long> {
        public Long(Map<Number, String> names) {
            super(names, 0L, (a, b) -> a | b);
        }
        public String toString(long mask) {
            return BitMask.toString(this.names, bits(mask));
        }

        @Override
        public Iterable<java.lang.Long> bits() {
            return bits(this.value);
        }

    }

    public static BitMask.Int ofInt(Class<?> flagClass) {
        var constants = constantFields(flagClass);
        checkConstants(flagClass, constants, int.class);
        var mask = values(constants, int.class);
        return new BitMask.Int(mask);
    }

    public static BitMask.Long ofLong(Class<?> flagClass) {
        var constants = constantFields(flagClass);
        checkConstants(flagClass, constants, long.class);
        var mask = values(constants, long.class);
        return new BitMask.Long(mask);
    }

    public static Iterable<Integer> bits(int bitset, BitMask.Int mask) {
        return bits(bitset & mask.value);
    }

    public static Iterable<Integer> bits(int bitset) {
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

    public static Iterable<java.lang.Long> bits(long bitset, BitMask.Long mask) {
        return bits(bitset & mask.value);
    }

    public static Iterable<java.lang.Long> bits(long bitset) {
        return () -> new Iterator<>() {
            long remaining = bitset;

            @Override
            public boolean hasNext() {
                return this.remaining != 0L;
            }

            @Override
            public java.lang.Long next() {
                if (this.remaining == 0L) {
                    throw new NoSuchElementException();
                }
                long lowestBit = java.lang.Long.lowestOneBit(this.remaining);
                this.remaining &= ~lowestBit; // remove bit
                return lowestBit;
            }
        };
    }





    private static String toString(Map<Number, String> names, Iterable<? extends Number> flags) {
        var sb = new StringBuilder();
        sb.append("[");
        var first = true;
        for (var flag : flags) {
            var name = names.get(flag);
            if (name == null) {
                continue;
            }
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(name);
        }
        sb.append("]");
        return sb.toString();
    }

    private static String toString(Map<Number, String> names) {
        var sb = new StringBuilder();

        sb.append("[");
        var first = true;
        for (var entry : names.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(entry.getValue());
            sb.append("=");
            var key = entry.getKey();
            var str = switch (key) {
                case Integer i -> Integer.toBinaryString(i);
                case java.lang.Long l -> java.lang.Long.toBinaryString(l);
                default -> "";
            };
            sb.append(str);
        }
        sb.append("]");
        return sb.toString();
    }


    private static List<Field> constantFields(Class<?> flagClass) {
        return Arrays.stream(flagClass.getDeclaredFields())
              .filter(ref -> !ref.getName().equals("MASK"))
              .filter(ref -> Modifier.isStatic(ref.getModifiers()))
              .filter(ref -> Modifier.isPublic(ref.getModifiers()))
              .filter(ref -> Modifier.isFinal(ref.getModifiers()))
              .toList();
    }

    private static void checkConstants(Class<?> flagClass, List<Field> constants, Class<?> primitive) {
        var hasPrimitive = has(constants, primitive);
        var hasOtherPrimitives = hasOthers(constants, primitive);
        if (hasOtherPrimitives) {
            throw new IllegalArgumentException(
                    "Expected class '"
                    + flagClass.getName()
                    + "' to have no other primitive constants besides "
                    + primitive.getName()
            );
        }
        if (!hasPrimitive) {
            throw new IllegalArgumentException(
                    "Expected class '"
                    + flagClass.getName()
                    + "' to have at least one constant of type "
                    + primitive.getName()
            );
        }
    }

    private static Map<Number, String> values(
            List<Field> constants,
            Class<?> primitive
    ) {
        var names = new LinkedHashMap<Number, String>();
        for (var field : constants) {
            var fieldType = field.getType();
            if (fieldType != primitive) {
                continue;
            }
            try {
                var value = (Number) Objects.requireNonNull(field.get(null));
                names.put(value, field.getName());
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Could not read field " + field.getName(), e);
            }
        }

        return names;
    }

    private static <T extends Number> T maskOf(
            Map<Number, String> constants,
            T identity,
            BiFunction<T, T, T> fold
    ) {
        var result = identity;
        for (var value : constants.keySet()) {
            @SuppressWarnings("unchecked")
            var typedValue = (T) value;
            result = fold.apply(result, typedValue);
        }

        return result;
    }


    private static boolean has(List<Field> constants, Class<?> primitive) {
        for (var field : constants) {
            if (field.getType() == primitive) {
                return true;
            }
        }
        return false;
    }
    private static boolean hasOthers(List<Field> constants, Class<?> primitive) {
        for (var field : constants) {
            var fieldType = field.getType();
            if (fieldType != primitive && isPrimitiveNumber(fieldType)) {
                return true;
            }
        }
        return false;
    }
    private static boolean isPrimitiveNumber(Class<?> primitive) {
        return primitive == byte.class
                || primitive == short.class
                || primitive == int.class
                || primitive == long.class
                || primitive == float.class
                || primitive == double.class;
    }
}
