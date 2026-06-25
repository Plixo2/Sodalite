package io.github.plixo2.sodalite.memory;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiFunction;

public class BitMask {


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

    /// @param flagClass the annotation class containing the constants
    /// The class must be an annotation with @Retention and @Target annotations,
    /// and must have at least one constant of type int and no other primitive constants.
    ///
    /// @throws IllegalArgumentException if the class is not an annotation
    /// @throws IllegalArgumentException if the class does not have @Retention and @Target annotations
    /// @throws IllegalArgumentException if the class does not have any constants of type int
    /// @throws IllegalArgumentException if the class has other primitives constants besides int
    /// @throws IllegalStateException if the reflective access to the constant field fails
    public static int flagMaskInt(Class<?> flagClass) {
        var constants = constantFields(flagClass);
        checkConstants(flagClass, constants, int.class);
        return getConstantMask(constants, int.class, 0, (a, b) -> a | b);
    }

    /// Returns a mask of all the constants defined in the class.
    /// The class must be an annotation with @Retention and @Target annotations,
    /// and must have at least one constant of type long and no other primitive constants.
    ///
    /// @param flagClass the annotation class containing the constants
    ///
    /// @throws IllegalArgumentException if the class is not an annotation
    /// @throws IllegalArgumentException if the class does not have @Retention and @Target annotations
    /// @throws IllegalArgumentException if the class does not have any constants of type long
    /// @throws IllegalArgumentException if the class has other primitives constants besides long
    /// @throws IllegalStateException if the reflective access to the constant field fails
    public static long flagMaskLong(Class<?> flagClass) {
        var constants = constantFields(flagClass);
        checkConstants(flagClass, constants, long.class);
        return getConstantMask(constants, long.class, 0L, (a, b) -> a | b);
    }

    private static List<Field> constantFields(Class<?> flagClass) {

        if (!flagClass.isAnnotation()) {
            throw new IllegalArgumentException(
                    "Expected '" + flagClass.getName() +  "' to be an annotation class"
            );
        }

        var retention = flagClass.getAnnotation(Retention.class);
        var target = flagClass.getAnnotation(Target.class);
        if (retention == null || target == null) {
            throw new IllegalArgumentException(
                    "Expected @Retention and @Target annotation on class "
                    + "'" + flagClass.getName() + "'"
            );
        }

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

    private static <T extends Number> T getConstantMask(List<Field> constants, Class<?> primitive, T identity, BiFunction<T, T, T> reduce) {
        var result = identity;
        for (var field : constants) {
            var fieldType = field.getType();
            if (fieldType != primitive) {
                continue;
            }

            try {
                @SuppressWarnings("unchecked")
                var value = (T) Objects.requireNonNull(field.get(null));
                result = reduce.apply(result, value);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Could not read field " + field.getName(), e);
            }
        }

        return result;
    }

}
