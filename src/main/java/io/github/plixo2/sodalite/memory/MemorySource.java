package io.github.plixo2.sodalite.memory;


import io.github.plixo2.sodalite.io.FileIO;

import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Objects;

import static java.lang.foreign.ValueLayout.*;

public sealed interface MemorySource<T extends Exception> {

    MemorySegment load(Arena arena) throws T;

    static MemorySource<IOException> of(Path path) {
        return new MemorySource.File(path);
    }
    static MemorySource<IOException> of(InputStream inputStream) {
        return new MemorySource.Stream(inputStream);
    }
    static MemorySource<IOException> of(Class<?> clazz, String resourceName) {
        return new MemorySource.Resource(clazz, resourceName);
    }
    static MemorySource<RuntimeException> of(MemorySegment segment) {
        return new MemorySource.Memory(segment);
    }
    static MemorySource<RuntimeException> of(MemorySegment segment, long offset, long length) {
        return new MemorySource.Memory(segment.asSlice(offset, length));
    }
    static MemorySource<RuntimeException> of(ByteBuffer byteBuffer) {
        return new MemorySource.Buffer(byteBuffer);
    }
    static MemorySource<RuntimeException> of(byte[] bytes) {
        return new ArraySource.ByteArray(bytes, 0, bytes.length);
    }
    static MemorySource<RuntimeException> of(byte[] bytes, int offset, int length) {
        return new ArraySource.ByteArray(bytes, offset, length);
    }
    static MemorySource<RuntimeException> of(char[] chars) {
        return new ArraySource.CharArray(chars, 0, chars.length);
    }
    static MemorySource<RuntimeException> of(char[] chars, int offset, int length) {
        return new ArraySource.CharArray(chars, offset, length);
    }
    static MemorySource<RuntimeException> of(short[] shorts) {
        return new ArraySource.ShortArray(shorts, 0, shorts.length);
    }
    static MemorySource<RuntimeException> of(short[] shorts, int offset, int length) {
        return new ArraySource.ShortArray(shorts, offset, length);
    }
    static MemorySource<RuntimeException> of(int[] ints) {
        return new ArraySource.IntArray(ints, 0, ints.length);
    }
    static MemorySource<RuntimeException> of(int[] ints, int offset, int length) {
        return new ArraySource.IntArray(ints, offset, length);
    }
    static MemorySource<RuntimeException> of(long[] longs) {
        return new ArraySource.LongArray(longs, 0, longs.length);
    }
    static MemorySource<RuntimeException> of(long[] longs, int offset, int length) {
        return new ArraySource.LongArray(longs, offset, length);
    }
    static MemorySource<RuntimeException> of(float[] floats) {
        return new ArraySource.FloatArray(floats, 0, floats.length);
    }
    static MemorySource<RuntimeException> of(float[] floats, int offset, int length) {
        return new ArraySource.FloatArray(floats, offset, length);
    }
    static MemorySource<RuntimeException> of(double[] doubles) {
        return new ArraySource.DoubleArray(doubles, 0, doubles.length);
    }
    static MemorySource<RuntimeException> of(double[] doubles, int offset, int length) {
        return new ArraySource.DoubleArray(doubles, offset, length);
    }


    record File(Path path) implements MemorySource<IOException> {
        @Override
        public MemorySegment load(Arena arena) throws IOException {
            return FileIO.load(arena, this.path);
        }
    }

    record Stream(InputStream inputStream) implements MemorySource<IOException> {
        @Override
        public MemorySegment load(Arena arena) throws IOException {
            var bytes = this.inputStream.readAllBytes();
            return arena.allocateFrom(JAVA_BYTE, bytes);
        }
    }

    record Resource(Class<?> clazz, String resourceName) implements MemorySource<IOException> {
        @Override
        public MemorySegment load(Arena arena) throws IOException {
            try (var stream = this.clazz.getResourceAsStream(this.resourceName)) {
                if (stream == null) {
                    var absoluteName = resolveName(this.clazz, this.resourceName);
                    throw new IOException("Resource '" + this.resourceName + "' ('" + absoluteName + "') not found");
                }
                return FileIO.load(arena, stream);
            }
        }

        /// Accessible version of `Class.resolveName(String)` to geth the absolute resource name.
        private static String resolveName(Class<?> clazz, String name) {
            if (!name.startsWith("/")) {
                String baseName = clazz.getPackageName();
                if (!baseName.isEmpty()) {
                    int len = baseName.length() + 1 + name.length();
                    StringBuilder sb = new StringBuilder(len);
                    name = sb.append(baseName.replace('.', '/'))
                             .append('/')
                             .append(name)
                             .toString();
                }
            } else {
                name = name.substring(1);
            }
            return name;
        }

    }

    record Memory(MemorySegment segment) implements MemorySource<RuntimeException> {
        @Override
        public MemorySegment load(Arena arena) {
            return this.segment;
        }
    }

    record Buffer(ByteBuffer byteBuffer) implements MemorySource<RuntimeException> {
        @Override
        public MemorySegment load(Arena arena) {
            return MemorySegment.ofBuffer(this.byteBuffer);
        }
    }



    abstract sealed class ArraySource<T> implements MemorySource<RuntimeException> {

        protected final T array;
        private final int offset;
        private final int length;
        private final int arrayLength;
        private final ValueLayout layout;

        private ArraySource(
                T array,
                int offset,
                int length,
                int arrayLength,
                ValueLayout layout
        ) {
            Objects.checkFromIndexSize(offset, length, arrayLength);
            this.array = array;
            this.offset = offset;
            this.length = length;
            this.arrayLength = arrayLength;
            this.layout = layout;
        }

        protected abstract MemorySegment allocateDirect(Arena arena);

        @Override
        public MemorySegment load(Arena arena) {
            if (this.length == 0) {
                return arena.allocate(0);
            } else if (this.offset == 0 && this.length == this.arrayLength) {
                return allocateDirect(arena);
            }

            var segment = arena.allocate(this.layout.byteSize() * this.length, this.layout.byteAlignment());
            MemorySegment.copy(this.array, this.offset, segment, this.layout, 0, this.length);
            return segment;
        }


        public static final class ByteArray extends ArraySource<byte[]> {
            public ByteArray(byte[] bytes, int offset, int length) {
                super(bytes, offset, length, bytes.length, JAVA_BYTE);
            }

            @Override
            protected MemorySegment allocateDirect(Arena arena) {
                return arena.allocateFrom(JAVA_BYTE, this.array);
            }
        }

        public static final class CharArray extends ArraySource<char[]> {
            public CharArray(char[] chars, int offset, int length) {
                super(chars, offset, length, chars.length, JAVA_CHAR);
            }

            @Override
            protected MemorySegment allocateDirect(Arena arena) {
                return arena.allocateFrom(JAVA_CHAR, this.array);
            }
        }

        public static final class ShortArray extends ArraySource<short[]> {
            public ShortArray(short[] shorts, int offset, int length) {
                super(shorts, offset, length, shorts.length, JAVA_SHORT);
            }

            @Override
            protected MemorySegment allocateDirect(Arena arena) {
                return arena.allocateFrom(JAVA_SHORT, this.array);
            }
        }

        public static final class IntArray extends ArraySource<int[]> {
            public IntArray(int[] ints, int offset, int length) {
                super(ints, offset, length, ints.length, JAVA_INT);
            }

            @Override
            protected MemorySegment allocateDirect(Arena arena) {
                return arena.allocateFrom(JAVA_INT, this.array);
            }
        }

        public static final class LongArray extends ArraySource<long[]> {
            public LongArray(long[] longs, int offset, int length) {
                super(longs, offset, length, longs.length, JAVA_LONG);
            }

            @Override
            protected MemorySegment allocateDirect(Arena arena) {
                return arena.allocateFrom(JAVA_LONG, this.array);
            }
        }

        public static final class FloatArray extends ArraySource<float[]> {
            public FloatArray(float[] floats, int offset, int length) {
                super(floats, offset, length, floats.length, JAVA_FLOAT);
            }

            @Override
            protected MemorySegment allocateDirect(Arena arena) {
                return arena.allocateFrom(JAVA_FLOAT, this.array);
            }
        }

        public static final class DoubleArray extends ArraySource<double[]> {
            public DoubleArray(double[] doubles, int offset, int length) {
                super(doubles, offset, length, doubles.length, JAVA_DOUBLE);
            }

            @Override
            protected MemorySegment allocateDirect(Arena arena) {
                return arena.allocateFrom(JAVA_DOUBLE, this.array);
            }
        }

    }

}
