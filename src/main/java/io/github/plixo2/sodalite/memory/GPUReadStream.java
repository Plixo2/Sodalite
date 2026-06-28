package io.github.plixo2.sodalite.memory;

import org.joml.*;

import java.lang.foreign.MemorySegment;

public interface GPUReadStream {

    float readFloat();
    float[] readFloats(float[] destination);
    default float[] readFloats(int length) {
        return readFloats(new float[length]);
    }

    int readInt();
    int[] readInts(int[] destination);
    default int[] readInts(int length) {
        return readInts(new int[length]);
    }

    long readLong();
    long[] readLongs(long[] destination);
    default long[] readLongs(int length) {
        return readLongs(new long[length]);
    }

    byte readByte();
    byte[] readBytes(byte[] destination);
    default byte[] readBytes(int length) {
        return readBytes(new byte[length]);
    }

    short readShort();
    short[] readShorts(short[] destination);
    default short[] readShorts(int length) {
        return readShorts(new short[length]);
    }

    Matrix4f readMatrix4f(Matrix4f destination);
    default Matrix4f readMatrix4f() {
        return readMatrix4f(new Matrix4f());
    }

    Matrix3f readMatrix3f(Matrix3f destination);
    default Matrix3f readMatrix3f() {
        return readMatrix3f(new Matrix3f());
    }

    Matrix2f readMatrix2f(Matrix2f destination);
    default Matrix2f readMatrix2f() {
        return readMatrix2f(new Matrix2f());
    }

    Vector4f readVector4f(Vector4f destination);
    default Vector4f readVector4f() {
        return readVector4f(new Vector4f());
    }

    Vector3f readVector3f(Vector3f destination);
    default Vector3f readVector3f() {
        return readVector3f(new Vector3f());
    }

    Vector2f readVector2f(Vector2f destination);
    default Vector2f readVector2f() {
        return readVector2f(new Vector2f());
    }

    Vector4i readVector4i(Vector4i destination);
    default Vector4i readVector4i() {
        return readVector4i(new Vector4i());
    }

    Vector3i readVector3i(Vector3i destination);
    default Vector3i readVector3i() {
        return readVector3i(new Vector3i());
    }

    Vector2i readVector2i(Vector2i destination);
    default Vector2i readVector2i() {
        return readVector2i(new Vector2i());
    }

    Quaternionf readQuaternionf(Quaternionf destination);
    default Quaternionf readQuaternionf() {
        return readQuaternionf(new Quaternionf());
    }

    MemorySegment slice(long length);

    MemorySegment read(MemorySegment destination, long offset, long length);

    default MemorySegment read(MemorySegment destination) {
        return read(destination, 0, destination.byteSize());
    }


}
