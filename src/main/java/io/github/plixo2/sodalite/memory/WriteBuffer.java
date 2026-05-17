package io.github.plixo2.sodalite.memory;

import io.github.plixo2.sodalite.resource.ResourceObject;
import org.joml.*;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public abstract class WriteBuffer extends ResourceObject implements GPUWriteStream {

    protected long size;

    protected abstract void ensureCapacity(long requiredCapacity);
    protected abstract MemorySegment currentSegmentUnchecked();


    public abstract MemorySegment memory();
    public abstract void clear();
    public abstract long capacity();

    public long size() {
        return this.size;
    }


    @Override
    public synchronized void writeFloat(float value) {
        ensureCapacity(this.size + Float.BYTES);
        currentSegmentUnchecked().set(ValueLayout.JAVA_FLOAT, this.size, value);
        this.size += Float.BYTES;
    }

    @Override
    public synchronized void writeFloats(float[] values) {
        long bytes = (long) values.length * Float.BYTES;
        ensureCapacity(this.size + bytes);
        MemorySegment.copy(values, 0, currentSegmentUnchecked(), ValueLayout.JAVA_FLOAT, this.size, values.length);
        this.size += bytes;
    }

    @Override
    public synchronized void writeInt(int value) {
        ensureCapacity(this.size + Integer.BYTES);
        currentSegmentUnchecked().set(ValueLayout.JAVA_INT, this.size, value);
        this.size += Integer.BYTES;
    }

    @Override
    public synchronized void writeInts(int[] values) {
        long bytes = (long) values.length * Integer.BYTES;
        ensureCapacity(this.size + bytes);
        MemorySegment.copy(values, 0, currentSegmentUnchecked(), ValueLayout.JAVA_INT, this.size, values.length);
        this.size += bytes;
    }

    @Override
    public synchronized void writeByte(byte value) {
        ensureCapacity(this.size + Byte.BYTES);
        currentSegmentUnchecked().set(ValueLayout.JAVA_BYTE, this.size, value);
        this.size += Byte.BYTES;
    }

    @Override
    public synchronized void writeBytes(byte[] values) {
        long bytes = values.length;
        ensureCapacity(this.size + bytes);
        MemorySegment.copy(values, 0, currentSegmentUnchecked(), ValueLayout.JAVA_BYTE, this.size, values.length);
        this.size += bytes;
    }

    @Override
    public synchronized void writeShort(short value) {
        ensureCapacity(this.size + Short.BYTES);
        currentSegmentUnchecked().set(ValueLayout.JAVA_SHORT, this.size, value);
        this.size += Short.BYTES;
    }

    @Override
    public synchronized void writeShorts(short[] values) {
        long bytes = (long) values.length * Short.BYTES;
        ensureCapacity(this.size + bytes);
        MemorySegment.copy(values, 0, currentSegmentUnchecked(), ValueLayout.JAVA_SHORT, this.size, values.length);
        this.size += bytes;
    }

    @Override
    public synchronized void writeMatrix4f(Matrix4f matrix) {
        long bytes = Float.BYTES * 16L;
        ensureCapacity(this.size + bytes);
        long offset = this.size;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_FLOAT, offset, matrix.m00());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES, matrix.m01());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2, matrix.m02());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 3, matrix.m03());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 4, matrix.m10());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 5, matrix.m11());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 6, matrix.m12());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 7, matrix.m13());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 8, matrix.m20());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 9, matrix.m21());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 10, matrix.m22());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 11, matrix.m23());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 12, matrix.m30());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 13, matrix.m31());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 14, matrix.m32());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 15, matrix.m33());
        this.size += bytes;
    }

    @Override
    public synchronized void writeMatrix3f(Matrix3f matrix) {
        long bytes = Float.BYTES * 9L;
        ensureCapacity(this.size + bytes);
        long offset = this.size;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_FLOAT, offset, matrix.m00());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES, matrix.m01());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2, matrix.m02());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 3, matrix.m10());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 4, matrix.m11());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 5, matrix.m12());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 6, matrix.m20());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 7, matrix.m21());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 8, matrix.m22());
        this.size += bytes;
    }

    @Override
    public synchronized void writeMatrix2f(Matrix2f matrix) {
        long bytes = Float.BYTES * 4L;
        ensureCapacity(this.size + bytes);
        long offset = this.size;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_FLOAT, offset, matrix.m00());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES, matrix.m01());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2, matrix.m10());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 3, matrix.m11());
        this.size += bytes;
    }

    @Override
    public synchronized void writeVector4f(Vector4f vector) {
        long bytes = Float.BYTES * 4L;
        ensureCapacity(this.size + bytes);
        long offset = this.size;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_FLOAT, offset, vector.x);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES, vector.y);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2, vector.z);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 3, vector.w);
        this.size += bytes;
    }

    @Override
    public synchronized void writeVector3f(Vector3f vector) {
        long bytes = Float.BYTES * 3L;
        ensureCapacity(this.size + bytes);
        long offset = this.size;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_FLOAT, offset, vector.x);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES, vector.y);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2, vector.z);
        this.size += bytes;
    }

    @Override
    public synchronized void writeVector2f(Vector2f vector) {
        long bytes = Float.BYTES * 2L;
        ensureCapacity(this.size + bytes);
        long offset = this.size;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_FLOAT, offset, vector.x);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES, vector.y);
        this.size += bytes;
    }

    @Override
    public synchronized void writeVector4i(Vector4i vector) {
        long bytes = Integer.BYTES * 4L;
        ensureCapacity(this.size + bytes);
        long offset = this.size;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_INT, offset, vector.x);
        segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES, vector.y);
        segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES * 2, vector.z);
        segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES * 3, vector.w);
        this.size += bytes;
    }

    @Override
    public synchronized void writeVector3i(Vector3i vector) {
        long bytes = Integer.BYTES * 3L;
        ensureCapacity(this.size + bytes);
        long offset = this.size;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_INT, offset, vector.x);
        segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES, vector.y);
        segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES * 2, vector.z);
        this.size += bytes;
    }

    @Override
    public synchronized void writeVector2i(Vector2i vector) {
        long bytes = Integer.BYTES * 2L;
        ensureCapacity(this.size + bytes);
        long offset = this.size;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_INT, offset, vector.x);
        segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES, vector.y);
        this.size += bytes;
    }

    @Override
    public synchronized void writeQuaternionf(Quaternionf quaternion) {
        long bytes = Float.BYTES * 4L;
        ensureCapacity(this.size + bytes);
        long offset = this.size;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_FLOAT, offset, quaternion.x);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES, quaternion.y);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2, quaternion.z);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 3, quaternion.w);
        this.size += bytes;
    }
}
