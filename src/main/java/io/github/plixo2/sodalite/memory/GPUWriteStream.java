package io.github.plixo2.sodalite.memory;

import org.joml.*;

public interface GPUWriteStream {

    void writeFloat(float value);
    void writeFloats(float[] values);
    void writeInt(int value);
    void writeInts(int[] values);
    void writeByte(byte value);
    void writeBytes(byte[] values);
    void writeShort(short value);
    void writeShorts(short[] values);
    void writeMatrix4f(Matrix4f matrix);
    void writeMatrix3f(Matrix3f matrix);
    void writeMatrix2f(Matrix2f matrix);

    void writeVector4f(Vector4f vector);
    void writeVector3f(Vector3f vector);
    void writeVector2f(Vector2f vector);

    void writeVector4i(Vector4i vector);
    void writeVector3i(Vector3i vector);
    void writeVector2i(Vector2i vector);

    void writeQuaternionf(Quaternionf quaternion);

}
