package io.github.plixo2.sodalite.memory;


import java.lang.foreign.MemoryLayout;
import java.lang.foreign.ValueLayout;

public class Layouts {

    public static MemoryLayout FLOAT = ValueLayout.JAVA_FLOAT;
    public static MemoryLayout HALF = ValueLayout.JAVA_SHORT;
    public static MemoryLayout INT = ValueLayout.JAVA_INT;
    public static MemoryLayout UINT = ValueLayout.JAVA_INT;
    public static MemoryLayout BYTE = ValueLayout.JAVA_BYTE;
    public static MemoryLayout UBYTE = ValueLayout.JAVA_BYTE;
    public static MemoryLayout SHORT = ValueLayout.JAVA_SHORT;
    public static MemoryLayout USHORT = ValueLayout.JAVA_SHORT;

    public static MemoryLayout VECTOR_2F = floatVector(2);
    public static MemoryLayout VECTOR_3F = floatVector(3);
    public static MemoryLayout VECTOR_4F = floatVector(4);

    public static MemoryLayout VECTOR_2I = intVector(2);
    public static MemoryLayout VECTOR_3I = intVector(3);
    public static MemoryLayout VECTOR_4I = intVector(4);

    public static MemoryLayout MAT_2F = floatVector(4);
    public static MemoryLayout MAT_3F = floatVector(9);
    public static MemoryLayout MAT_4F = floatVector(16);

    public static MemoryLayout QUATERNION_F = floatVector(4);




    private static MemoryLayout floatVector(int count) {
        return MemoryLayout.sequenceLayout(count, ValueLayout.JAVA_FLOAT);
    }
    private static MemoryLayout intVector(int count) {
        return MemoryLayout.sequenceLayout(count, ValueLayout.JAVA_INT);
    }

}
