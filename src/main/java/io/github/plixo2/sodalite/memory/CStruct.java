package io.github.plixo2.sodalite.memory;

import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.Getter;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.StructLayout;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class CStruct extends ConstantWriteBufferImpl<CStruct> {

    @Getter
    private final StructLayout layout;

    private final Map<String, Long> offsets = new ConcurrentHashMap<>();

    CStruct(
            ResourceSet resources,
            StructLayout layout
    ) {
        super(resources, layout.byteSize());
        this.layout = layout;
    }

    public static CStruct allocate(
            ResourceSet resources,
            StructLayout layout
    ) {
        return new CStruct(resources, layout);
    }

    public CStruct at(String name) {
        this.seek(offsetOf(name));
        return this;
    }

    public CStruct at(int position) {
        this.seek(position);
        return this;
    }

    public long offsetOf(String name) {
        return this.offsets.computeIfAbsent(name, ref -> {
            return this.layout.byteOffset(MemoryLayout.PathElement.groupElement(ref));
        });
    }

    public long offsetOf(MemoryLayout layout) {
        var name = layout.name().orElse(null);
        if (name == null) {
            throw new IllegalArgumentException("Layout must have a name to get its offset");
        }
        var getElement = this.layout.select(MemoryLayout.PathElement.groupElement(name));
        if (!Objects.equals(getElement, layout)) {
            throw new IllegalArgumentException(
                    "Layout of field '" + name + "' does not match expected layout. " +
                            "Expected: " + layout + ", actual: " + getElement
            );
        }

        return offsetOf(name);
    }



     /*

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
    */

}
