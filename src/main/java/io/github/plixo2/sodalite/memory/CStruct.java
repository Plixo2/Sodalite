package io.github.plixo2.sodalite.memory;

import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.Getter;
import org.joml.*;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Objects;

import static io.github.plixo2.sodalite.Internal.assertU32;

public final class CStruct extends ResourceObject {

    @Getter
    private final MemoryLayout layout;

    @Getter
    private final long size;

    private final MemorySegment segment;

    CStruct(
            ResourceSet resources,
            MemoryLayout layout
    ) {
        var size = layout.byteSize();
        assertU32(size);
        resources.register(this);
        this.size = size;
        this.segment = resources.allocate(size);
        this.layout = layout;
    }

    public MemorySegment memory() {
        ensureNotReleased();
        return this.segment;
    }

    public static CStruct allocate(
            ResourceSet resources,
            MemoryLayout layout
    ) {
        return new CStruct(resources, layout);
    }


//    public VarHandle handleOfVariable(String name) {
//        VarHandle handle = this.layout.varHandle(MemoryLayout.PathElement.groupElement(name));
//        return MethodHandles.insertCoordinates(handle, 0, this.segment, 0L);
//    }
//    public VarHandle handleOfArrayElement(long index) {
//        VarHandle handle = this.layout.varHandle(MemoryLayout.PathElement.sequenceElement());
//        return MethodHandles.insertCoordinates(handle, 0, this.segment, 0L, index);
//    }
//    public VarHandle handleOfArrayElement(String name, long index) {
//        VarHandle handle = this.layout.varHandle(MemoryLayout.PathElement.groupElement(name), MemoryLayout.PathElement.sequenceElement());
//        return MethodHandles.insertCoordinates(handle, 0, this.segment, 0L, index);
//    }
//    public VarHandle handleOfArray() {
//        VarHandle handle = this.layout.varHandle(MemoryLayout.PathElement.sequenceElement());
//        return MethodHandles.insertCoordinates(handle, 0, this.segment, 0L);
//    }
//    public VarHandle handleOfArray(String name) {
//        VarHandle handle = this.layout.varHandle(MemoryLayout.PathElement.groupElement(name), MemoryLayout.PathElement.sequenceElement());
//        return MethodHandles.insertCoordinates(handle, 0, this.segment, 0L);
//    }
//
    private long offset(String name, MemoryLayout layout) {
        var getElement = this.layout.select(MemoryLayout.PathElement.groupElement(name));
        if (!Objects.equals(getElement.withoutName(), layout.withoutName())) {
            throw new IllegalArgumentException(
                    "Layout of field '" + name + "' does not match expected layout. " +
                            "Expected: " + layout + ", actual: " + getElement
            );
        }

        return this.layout.byteOffset(MemoryLayout.PathElement.groupElement(name));
    }

    public FloatSetter Float(String name) {
        var offset = offset(name, Layouts.FLOAT);
        return value -> this.segment.set(ValueLayout.JAVA_FLOAT, offset, value);
    }

    public IntSetter Integer(String name) {
        var offset = offset(name, Layouts.INT);
        return value -> this.segment.set(ValueLayout.JAVA_INT, offset, value);
    }

    public ByteSetter Byte(String name) {
        var offset = offset(name, Layouts.BYTE);
        return value -> this.segment.set(ValueLayout.JAVA_BYTE, offset, value);
    }

    public ShortSetter Short(String name) {
        var offset = offset(name, Layouts.SHORT);
        return value -> this.segment.set(ValueLayout.JAVA_SHORT, offset, value);
    }

    public Vec2Setter vec2(String name) {
        var offset = offset(name, Layouts.VECTOR_2F);
        return (x, y) -> {
            this.segment.set(ValueLayout.JAVA_FLOAT, offset, x);
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES, y);
        };
    }

    public Vec3Setter vec3(String name) {
        var offset = offset(name, Layouts.VECTOR_3F);
        return (x, y, z) -> {
            this.segment.set(ValueLayout.JAVA_FLOAT, offset, x);
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES, y);
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2, z);
        };
    }

    public Vec4Setter vec4(String name) {
        var offset = offset(name, Layouts.VECTOR_4F);
        return (x, y, z, w) -> {
            this.segment.set(ValueLayout.JAVA_FLOAT, offset, x);
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES, y);
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2, z);
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 3, w);
        };
    }

    public QuaternionFSetter quaternion(String name) {
        var offset = offset(name, Layouts.QUATERNION_F);
        return (x, y, z, w) -> {
            this.segment.set(ValueLayout.JAVA_FLOAT, offset, x);
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES, y);
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2, z);
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 3, w);
        };
    }

    public Vec2iSetter vec2i(String name) {
        var offset = offset(name, Layouts.VECTOR_2I);
        return (x, y) -> {
            this.segment.set(ValueLayout.JAVA_INT, offset, x);
            this.segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES, y);
        };
    }

    public Vec3iSetter vec3i(String name) {
        var offset = offset(name, Layouts.VECTOR_3I);
        return (x, y, z) -> {
            this.segment.set(ValueLayout.JAVA_INT, offset, x);
            this.segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES, y);
            this.segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES * 2, z);
        };
    }

    public Vec4iSetter vec4i(String name) {
        var offset = offset(name, Layouts.VECTOR_4I);
        return (x, y, z, w) -> {
            this.segment.set(ValueLayout.JAVA_INT, offset, x);
            this.segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES, y);
            this.segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES * 2, z);
            this.segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES * 3, w);
        };
    }

    public Mat2Setter mat2(String name) {
        var offset = offset(name, Layouts.MAT_2F);
        return mat -> {
            this.segment.set(ValueLayout.JAVA_FLOAT, offset,                  mat.m00());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES,    mat.m01());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2, mat.m10());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 3, mat.m11());
        };
    }

    public Mat3Setter mat3(String name) {
        var offset = offset(name, Layouts.MAT_3F);
        return mat -> {
            this.segment.set(ValueLayout.JAVA_FLOAT, offset,                   mat.m00());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES,     mat.m01());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2, mat.m02());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 3, mat.m10());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 4, mat.m11());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 5, mat.m12());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 6, mat.m20());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 7, mat.m21());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 8, mat.m22());
        };
    }

    public Mat4Setter mat4(String name) {
        var offset = offset(name, Layouts.MAT_4F);
        return mat -> {
            this.segment.set(ValueLayout.JAVA_FLOAT, offset,                    mat.m00());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES,      mat.m01());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 2,  mat.m02());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 3,  mat.m03());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 4,  mat.m10());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 5,  mat.m11());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 6,  mat.m12());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 7,  mat.m13());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 8,  mat.m20());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 9,  mat.m21());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 10, mat.m22());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 11, mat.m23());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 12, mat.m30());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 13, mat.m31());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 14, mat.m32());
            this.segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES * 15, mat.m33());
        };
    }

    public interface FloatSetter {
        void set(float value);
    }
    public interface IntSetter {
        void set(int value);
    }
    public interface ByteSetter {
        void set(byte value);
    }
    public interface ShortSetter {
        void set(short value);
    }

    public interface Vec2iSetter {
        void set(int x, int y);
        default void set(Vector2i vec) {
            set(vec.x, vec.y);
        }
    }

    public interface Vec3iSetter {
        void set(int x, int y, int z);
        default void set(Vector3i vec) {
            set(vec.x, vec.y, vec.z);
        }
    }
    public interface Vec4iSetter {
        void set(int x, int y, int z, int w);
        default void set(Vector4i vec) {
            set(vec.x, vec.y, vec.z, vec.w);
        }
    }

    public interface Vec2Setter {
        void set(float x, float y);
        default void set(Vector2f vec) {
            set(vec.x, vec.y);
        }
    }

    public interface Vec3Setter {
        void set(float x, float y, float z);
        default void set(Vector3f vec) {
            set(vec.x, vec.y, vec.z);
        }
    }

    public interface Vec4Setter {
        void set(float x, float y, float z, float w);
        default void set(Vector4f vec) {
            set(vec.x, vec.y, vec.z, vec.w);
        }
    }

    public interface QuaternionFSetter {
        void set(float x, float y, float z, float w);
        default void set(Quaternionf quat) {
            set(quat.x, quat.y, quat.z, quat.w);
        }
    }

    public interface Mat2Setter {
        void set(Matrix2f mat);
    }
    public interface Mat3Setter {
        void set(Matrix3f mat);
    }
    public interface Mat4Setter {
        void set(Matrix4f mat);
    }

}
