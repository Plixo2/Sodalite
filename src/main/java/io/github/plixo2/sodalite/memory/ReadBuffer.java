package io.github.plixo2.sodalite.memory;

import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.ByteBuffer;

public class ReadBuffer extends ResourceObject implements GPUReadStream {

    private final MemorySegment segment;
    private final long size;

    private long position;

    private ReadBuffer(
            @Nullable ResourceSet resourceSet,
            MemorySegment segment
    ) {
        if (resourceSet != null) {
            resourceSet.register(this);
        }
        this.size = segment.byteSize();
        this.segment = segment;
        this.position = 0;
    }

    public static ReadBuffer of(
            ResourceSet resourceSet,
            MemorySegment segment
    ) {
        return new ReadBuffer(resourceSet, segment);
    }

    public static ReadBuffer of(MemorySegment segment) {
        return new ReadBuffer(null, segment);
    }

    public long size() {
        return this.size;
    }

    public long position() {
        return this.position;
    }

    public long remaining() {
        return this.size - this.position;
    }

    public ReadBuffer reset() {
        this.position = 0;
        return this;
    }

    public ReadBuffer seek(long position) {
        if (position < 0 || position > this.size) {
            throw new IndexOutOfBoundsException("Position out of bounds: " + position);
        }
        this.position = position;
        return this;
    }

    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }

    private void ensureCapacity(long additionalBytes) {
        ensureNotReleased();
        if (this.position + additionalBytes > this.size) {
            var message = "Not enough remaining bytes in buffer: "
                    + additionalBytes
                    + " requested, "
                    + remaining()
                    + " remaining";
            throw new IndexOutOfBoundsException(message);
        }
    }

    @Override
    public float readFloat() {
        ensureCapacity(Float.BYTES);
        var value = this.segment.get(ValueLayout.JAVA_FLOAT, this.position);
        this.position += Float.BYTES;
        return value;
    }

    @Override
    public float[] readFloats(float[] destination) {
        long bytes = (long) destination.length * Float.BYTES;
        ensureCapacity(bytes);
        MemorySegment.copy(this.segment, ValueLayout.JAVA_FLOAT, this.position, destination, 0, destination.length);
        this.position += bytes;
        return destination;
    }

    @Override
    public int readInt() {
        ensureCapacity(Integer.BYTES);
        var value = this.segment.get(ValueLayout.JAVA_INT, this.position);
        this.position += Integer.BYTES;
        return value;
    }

    @Override
    public int[] readInts(int[] destination) {
        long bytes = (long) destination.length * Integer.BYTES;
        ensureCapacity(bytes);
        MemorySegment.copy(this.segment, ValueLayout.JAVA_INT, this.position, destination, 0, destination.length);
        this.position += bytes;
        return destination;
    }

    @Override
    public long readLong() {
        ensureCapacity(Long.BYTES);
        var value = this.segment.get(ValueLayout.JAVA_LONG, this.position);
        this.position += Long.BYTES;
        return value;
    }

    @Override
    public long[] readLongs(long[] destination) {
        long bytes = (long) destination.length * Long.BYTES;
        ensureCapacity(bytes);
        MemorySegment.copy(this.segment, ValueLayout.JAVA_LONG, this.position, destination, 0, destination.length);
        this.position += bytes;
        return destination;
    }

    @Override
    public byte readByte() {
        ensureCapacity(Byte.BYTES);
        var value = this.segment.get(ValueLayout.JAVA_BYTE, this.position);
        this.position += Byte.BYTES;
        return value;
    }

    @Override
    public byte[] readBytes(byte[] destination) {
        long bytes = destination.length;
        ensureCapacity(bytes);
        MemorySegment.copy(this.segment, ValueLayout.JAVA_BYTE, this.position, destination, 0, destination.length);
        this.position += bytes;
        return destination;
    }

    @Override
    public short readShort() {
        ensureCapacity(Short.BYTES);
        var value = this.segment.get(ValueLayout.JAVA_SHORT, this.position);
        this.position += Short.BYTES;
        return value;
    }

    @Override
    public short[] readShorts(short[] destination) {
        long bytes = (long) destination.length * Short.BYTES;
        ensureCapacity(bytes);
        MemorySegment.copy(this.segment, ValueLayout.JAVA_SHORT, this.position, destination, 0, destination.length);
        this.position += bytes;
        return destination;
    }

    @Override
    public Matrix4f readMatrix4f(Matrix4f destination) {
        long bytes = Float.BYTES * 16L;
        ensureCapacity(bytes);
        long offset = this.position;
        destination.set(
                this.segment.get(ValueLayout.JAVA_FLOAT, offset),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 3),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 4),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 5),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 6),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 7),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 8),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 9),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 10),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 11),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 12),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 13),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 14),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 15)
        );
        this.position += bytes;
        return destination;
    }

    @Override
    public Matrix3f readMatrix3f(Matrix3f destination) {
        long bytes = Float.BYTES * 9L;
        ensureCapacity(bytes);
        long offset = this.position;
        destination.set(
                this.segment.get(ValueLayout.JAVA_FLOAT, offset),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 3),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 4),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 5),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 6),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 7),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 8)
        );
        this.position += bytes;
        return destination;
    }

    @Override
    public Matrix2f readMatrix2f(Matrix2f destination) {
        long bytes = Float.BYTES * 4L;
        ensureCapacity(bytes);
        long offset = this.position;
        destination.set(
                this.segment.get(ValueLayout.JAVA_FLOAT, offset),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 3)
        );
        this.position += bytes;
        return destination;
    }

    @Override
    public Vector4f readVector4f(Vector4f destination) {
        long bytes = Float.BYTES * 4L;
        ensureCapacity(bytes);
        long offset = this.position;
        destination.set(
                this.segment.get(ValueLayout.JAVA_FLOAT, offset),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 3)
        );
        this.position += bytes;
        return destination;
    }

    @Override
    public Vector3f readVector3f(Vector3f destination) {
        long bytes = Float.BYTES * 3L;
        ensureCapacity(bytes);
        long offset = this.position;
        destination.set(
                this.segment.get(ValueLayout.JAVA_FLOAT, offset),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2)
        );
        this.position += bytes;
        return destination;
    }

    @Override
    public Vector2f readVector2f(Vector2f destination) {
        long bytes = Float.BYTES * 2L;
        ensureCapacity(bytes);
        long offset = this.position;
        destination.set(
                this.segment.get(ValueLayout.JAVA_FLOAT, offset),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES)
        );
        this.position += bytes;
        return destination;
    }

    @Override
    public Vector4i readVector4i(Vector4i destination) {
        long bytes = Integer.BYTES * 4L;
        ensureCapacity(bytes);
        long offset = this.position;
        destination.set(
                this.segment.get(ValueLayout.JAVA_INT, offset),
                this.segment.get(ValueLayout.JAVA_INT, offset + Integer.BYTES),
                this.segment.get(ValueLayout.JAVA_INT, offset + Integer.BYTES * 2),
                this.segment.get(ValueLayout.JAVA_INT, offset + Integer.BYTES * 3)
        );
        this.position += bytes;
        return destination;
    }

    @Override
    public Vector3i readVector3i(Vector3i destination) {
        long bytes = Integer.BYTES * 3L;
        ensureCapacity(bytes);
        long offset = this.position;
        destination.set(
                this.segment.get(ValueLayout.JAVA_INT, offset),
                this.segment.get(ValueLayout.JAVA_INT, offset + Integer.BYTES),
                this.segment.get(ValueLayout.JAVA_INT, offset + Integer.BYTES * 2)
        );
        this.position += bytes;
        return destination;
    }

    @Override
    public Vector2i readVector2i(Vector2i destination) {
        long bytes = Integer.BYTES * 2L;
        ensureCapacity(bytes);
        long offset = this.position;
        destination.set(
                this.segment.get(ValueLayout.JAVA_INT, offset),
                this.segment.get(ValueLayout.JAVA_INT, offset + Integer.BYTES)
        );
        this.position += bytes;
        return destination;
    }

    @Override
    public Quaternionf readQuaternionf(Quaternionf destination) {
        long bytes = Float.BYTES * 4L;
        ensureCapacity(bytes);
        long offset = this.position;
        destination.set(
                this.segment.get(ValueLayout.JAVA_FLOAT, offset),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2),
                this.segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 3)
        );
        this.position += bytes;
        return destination;
    }

    @Override
    public MemorySegment slice(long length) {
        if (length < 0) {
            throw new IllegalArgumentException("Length must be non-negative");
        }
        ensureCapacity(length);
        var slice = this.segment.asSlice(this.position, length);
        this.position += length;
        return slice;
    }

    @Override
    public MemorySegment read(MemorySegment destination, long offset, long length) {
        if (length < 0 || offset < 0) {
            throw new IllegalArgumentException("Length and offset must be non-negative");
        }
        ensureCapacity(length);
        MemorySegment.copy(this.segment, this.position, destination, offset, length);
        this.position += length;
        return destination;
    }


    @Override
    public MemorySegment readAllRemaining() {
        return slice(remaining());
    }

}
