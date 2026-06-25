package io.github.plixo2.sodalite.memory;

import org.joml.*;

import java.lang.foreign.MemorySegment;

public interface GPUWriteStream<Self extends GPUWriteStream<Self>> {



    Self writeFloat(float value);
    Self writeFloats(float... values);
    Self writeInt(int value);
    Self writeInts(int... values);
    Self writeLong(long value);
    Self writeLongs(long... values);
    Self writeByte(byte value);
    Self writeBytes(byte... values);
    Self writeShort(short value);
    Self writeShorts(short... values);

    Self writeMatrix4f(Matrix4f matrix);
    Self writeMatrix3f(Matrix3f matrix);
    Self writeMatrix2f(Matrix2f matrix);

    Self writeVector4f(Vector4f vector);
    Self writeVector4f(float x, float y, float z, float w);

    Self writeVector3f(Vector3f vector);
    Self writeVector3f(float x, float y, float z);

    Self writeVector2f(Vector2f vector);
    Self writeVector2f(float x, float y);

    Self writeVector4i(Vector4i vector);
    Self writeVector4i(int x, int y, int z, int w);
    Self writeVector3i(Vector3i vector);
    Self writeVector3i(int x, int y, int z);
    Self writeVector2i(Vector2i vector);
    Self writeVector2i(int x, int y);

    Self writeQuaternionf(Quaternionf quaternion);

    Self write(MemorySegment segment, long offset, long length);

    default Self write(MemorySegment segment) {
        return write(segment, 0, segment.byteSize());
    }

}
