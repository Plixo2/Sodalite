package io.github.plixo2.sodalite.memory;

import static io.github.plixo2.sodalite.Internal.U32_MAX;

/// The strategy to grow a [GrowableWriteBuffer] when the current capacity is insufficient.
public sealed interface GrowthStrategy {

    /// Calculates the next capacity based on the current capacity and the required capacity.
    long next(long currentCapacity, long requiredCapacity);

    /// The buffer will grow by `Buffer.capacity * factor`.
    /// A value of 2 means the buffer will double in size.
    ///
    /// This is the default behavior with `factor` of 2.
    /// @param factor the factor to grow by, must not be less than 2 or greater than 256
    record Factor(int factor) implements GrowthStrategy {

        /// @throws IllegalArgumentException if `factor` is less than 2 or greater than 256
        public Factor {
            if (factor < 2) {
                throw new IllegalArgumentException("factor must be at least 2");
            } else if (factor > 256) {
                throw new IllegalArgumentException("factor must be at most 256");
            }
        }

        @Override
        public long next(long currentCapacity, long requiredCapacity) {

            var capacity = Math.max(1, currentCapacity);
            var factor = (long) this.factor;

            while (capacity < requiredCapacity) {
                try {
                    capacity = Math.multiplyExact(capacity, factor);
                } catch (ArithmeticException e) {
                    return U32_MAX; // max out
                }
            }

            return Math.min(capacity, U32_MAX);
        }
    }

    /// The buffer will grow by `Buffer.capacity + bytes`.
    /// @param bytes the amount of bytes to grow, must be greater than 0 and not greater than 2^32 - 1 bytes (4 GiB)
    record Constant(long bytes) implements GrowthStrategy {

        /// @throws IllegalArgumentException if `bytes` is less than 1 or greater than 2^32 - 1 bytes (4 GiB)
        public Constant {
            if (bytes < 1) {
                throw new IllegalArgumentException("'bytes' must be at least 1");
            } else if (bytes > U32_MAX) {
                throw new IllegalArgumentException("'bytes' must be at most 2^32 - 1 bytes (4 GiB)");
            }
        }

        @Override
        public long next(long currentCapacity, long requiredCapacity) {

            if (requiredCapacity <= currentCapacity) {
                return currentCapacity;
            }

            var missing = requiredCapacity - currentCapacity;
            var steps = Math.ceilDiv(missing, this.bytes);

            try {
                var growth = Math.multiplyExact(steps, this.bytes);
                var capacity = Math.addExact(currentCapacity, growth);
                return Math.min(capacity, U32_MAX);
            } catch (ArithmeticException e) {
                return U32_MAX;
            }
        }
    }

    /// The buffer will grow to the minimum required capacity
    record Minimum() implements GrowthStrategy {
        @Override
        public long next(long currentCapacity, long requiredCapacity) {
            return requiredCapacity;
        }
    }

    @FunctionalInterface
    non-sealed interface Custom extends GrowthStrategy {
        /// Calculates the next capacity based on the current capacity and the required capacity.
        long next(long currentCapacity, long requiredCapacity);

    }

}