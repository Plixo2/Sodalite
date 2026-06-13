package io.github.plixo2.sodalite.memory;

import io.github.plixo2.sodalite.resource.ResourceObject;
import org.joml.*;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import static io.github.plixo2.sodalite.Internal.*;

public abstract class WriteBuffer<Self extends WriteBuffer<Self>>
        extends ResourceObject
        implements GPUWriteStream<Self>
{

    protected long position = 0;
    protected long capacity = 0;

    protected abstract void ensureCapacity(long requiredCapacity);

    /// faster version to avoid overhead
    protected abstract MemorySegment currentSegmentUnchecked();

    /// - [GrowableWriteBuffer] returns a segment capped to `this.position`
    /// - [CStruct]/[ConstantWriteBuffer] return the full memory segment, not capped to `this.position` \
    public abstract MemorySegment memory();

    public long capacity() {
        ensureNotReleased();
        return this.capacity;
    }

    public long position() {
        return this.position;
    }
    public long remaining() {
        return this.capacity - this.position;
    }

    public Self reset() {
        ensureNotReleased();
        this.position = 0;
        return castThis();
    }

    public Self seek(long newPosition) {
        if (newPosition < 0 || newPosition > capacity()) {
            throw new IllegalArgumentException("Position must be between 0 and capacity");
        }
        assertU32(newPosition, "newPosition");
        this.position = newPosition;
        return castThis();
    }


    @Override
    public Self writeFloat(float value) {
        ensureCapacity(this.position + Float.BYTES);
        currentSegmentUnchecked().set(ValueLayout.JAVA_FLOAT, this.position, value);
        this.position += Float.BYTES;
        return castThis();
    }

    @Override
    public Self writeFloats(float... values) {
        long bytes = (long) values.length * Float.BYTES;
        ensureCapacity(this.position + bytes);
        MemorySegment.copy(values, 0, currentSegmentUnchecked(), ValueLayout.JAVA_FLOAT, this.position, values.length);
        this.position += bytes;
        return castThis();
    }

    @Override
    public Self writeInt(int value) {
        ensureCapacity(this.position + Integer.BYTES);
        currentSegmentUnchecked().set(ValueLayout.JAVA_INT, this.position, value);
        this.position += Integer.BYTES;
        return castThis();
    }

    @Override
    public Self writeInts(int... values) {
        long bytes = (long) values.length * Integer.BYTES;
        ensureCapacity(this.position + bytes);
        MemorySegment.copy(values, 0, currentSegmentUnchecked(), ValueLayout.JAVA_INT, this.position, values.length);
        this.position += bytes;
        return castThis();
    }

    @Override
    public Self writeByte(byte value) {
        ensureCapacity(this.position + Byte.BYTES);
        currentSegmentUnchecked().set(ValueLayout.JAVA_BYTE, this.position, value);
        this.position += Byte.BYTES;
        return castThis();
    }

    @Override
    public Self writeBytes(byte... values) {
        long bytes = values.length;
        ensureCapacity(this.position + bytes);
        MemorySegment.copy(values, 0, currentSegmentUnchecked(), ValueLayout.JAVA_BYTE, this.position, values.length);
        this.position += bytes;
        return castThis();
    }

    @Override
    public Self writeShort(short value) {
        ensureCapacity(this.position + Short.BYTES);
        currentSegmentUnchecked().set(ValueLayout.JAVA_SHORT, this.position, value);
        this.position += Short.BYTES;
        return castThis();
    }

    @Override
    public Self writeShorts(short... values) {
        long bytes = (long) values.length * Short.BYTES;
        ensureCapacity(this.position + bytes);
        MemorySegment.copy(values, 0, currentSegmentUnchecked(), ValueLayout.JAVA_SHORT, this.position, values.length);
        this.position += bytes;
        return castThis();
    }

    @Override
    public Self writeMatrix4f(Matrix4f matrix) {
        long bytes = Float.BYTES * 16L;
        ensureCapacity(this.position + bytes);
        long offset = this.position;
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
        this.position += bytes;
        return castThis();
    }

    @Override
    public Self writeMatrix3f(Matrix3f matrix) {
        long bytes = Float.BYTES * 9L;
        ensureCapacity(this.position + bytes);
        long offset = this.position;
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
        this.position += bytes;
        return castThis();
    }

    @Override
    public Self writeMatrix2f(Matrix2f matrix) {
        long bytes = Float.BYTES * 4L;
        ensureCapacity(this.position + bytes);
        long offset = this.position;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_FLOAT, offset, matrix.m00());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES, matrix.m01());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2, matrix.m10());
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 3, matrix.m11());
        this.position += bytes;
        return castThis();
    }

    @Override
    public Self writeVector4f(Vector4f vector) {
        long bytes = Float.BYTES * 4L;
        ensureCapacity(this.position + bytes);
        long offset = this.position;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_FLOAT, offset, vector.x);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES, vector.y);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2, vector.z);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 3, vector.w);
        this.position += bytes;
        return castThis();
    }

    @Override
    public Self writeVector4f(float x, float y, float z, float w) {
        long bytes = Float.BYTES * 4L;
        ensureCapacity(this.position + bytes);
        long offset = this.position;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_FLOAT, offset, x);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES, y);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2, z);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 3, w);
        this.position += bytes;
        return castThis();
    }

    @Override
    public Self writeVector3f(Vector3f vector) {
        long bytes = Float.BYTES * 3L;
        ensureCapacity(this.position + bytes);
        long offset = this.position;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_FLOAT, offset, vector.x);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES, vector.y);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2, vector.z);
        this.position += bytes;
        return castThis();
    }

    @Override
    public Self writeVector3f(float x, float y, float z) {
        long bytes = Float.BYTES * 3L;
        ensureCapacity(this.position + bytes);
        long offset = this.position;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_FLOAT, offset, x);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES, y);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2, z);
        this.position += bytes;
        return castThis();
    }


    @Override
    public Self writeVector2f(Vector2f vector) {
        long bytes = Float.BYTES * 2L;
        ensureCapacity(this.position + bytes);
        long offset = this.position;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_FLOAT, offset, vector.x);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES, vector.y);
        this.position += bytes;
        return castThis();
    }

    @Override
    public Self writeVector2f(float x, float y) {
        long bytes = Float.BYTES * 2L;
        ensureCapacity(this.position + bytes);
        long offset = this.position;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_FLOAT, offset, x);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES, y);
        this.position += bytes;
        return castThis();
    }

    @Override
    public Self writeVector4i(Vector4i vector) {
        long bytes = Integer.BYTES * 4L;
        ensureCapacity(this.position + bytes);
        long offset = this.position;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_INT, offset, vector.x);
        segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES, vector.y);
        segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES * 2, vector.z);
        segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES * 3, vector.w);
        this.position += bytes;
        return castThis();
    }

    @Override
    public Self writeVector4i(int x, int y, int z, int w) {
        long bytes = Integer.BYTES * 4L;
        ensureCapacity(this.position + bytes);
        long offset = this.position;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_INT, offset, x);
        segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES, y);
        segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES * 2, z);
        segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES * 3, w);
        this.position += bytes;
        return castThis();
    }

    @Override
    public Self writeVector3i(Vector3i vector) {
        long bytes = Integer.BYTES * 3L;
        ensureCapacity(this.position + bytes);
        long offset = this.position;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_INT, offset, vector.x);
        segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES, vector.y);
        segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES * 2, vector.z);
        this.position += bytes;
        return castThis();
    }

    @Override
    public Self writeVector3i(int x, int y, int z) {
        long bytes = Integer.BYTES * 3L;
        ensureCapacity(this.position + bytes);
        long offset = this.position;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_INT, offset, x);
        segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES, y);
        segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES * 2, z);
        this.position += bytes;
        return castThis();
    }

    @Override
    public Self writeVector2i(Vector2i vector) {
        long bytes = Integer.BYTES * 2L;
        ensureCapacity(this.position + bytes);
        long offset = this.position;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_INT, offset, vector.x);
        segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES, vector.y);
        this.position += bytes;
        return castThis();
    }

    @Override
    public Self writeVector2i(int x, int y) {
        long bytes = Integer.BYTES * 2L;
        ensureCapacity(this.position + bytes);
        long offset = this.position;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_INT, offset, x);
        segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES, y);
        this.position += bytes;
        return castThis();
    }

    @Override
    public Self writeQuaternionf(Quaternionf quaternion) {
        long bytes = Float.BYTES * 4L;
        ensureCapacity(this.position + bytes);
        long offset = this.position;
        var segment = currentSegmentUnchecked();
        segment.set(ValueLayout.JAVA_FLOAT, offset, quaternion.x);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES, quaternion.y);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2, quaternion.z);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 3, quaternion.w);
        this.position += bytes;
        return castThis();
    }


    @Override
    public Self write(MemorySegment segment, long offset, long length) {
        ensureCapacity(this.position + length);
        MemorySegment.copy(segment, offset, currentSegmentUnchecked(), this.position, length);
        this.position += length;
        return castThis();
    }

    @SuppressWarnings("unchecked")
    private Self castThis() {
        return (Self) this;
    }
}
