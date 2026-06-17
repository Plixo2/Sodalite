package io.github.plixo2.sodalite.memory;


import org.libsdl.sdl.SDL_GPUIndexedIndirectDrawCommand;
import org.libsdl.sdl.SDL_GPUIndirectDispatchCommand;
import org.libsdl.sdl.SDL_GPUIndirectDrawCommand;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.ValueLayout;

public class Layouts {

    public static MemoryLayout FLOAT = ValueLayout.JAVA_FLOAT;
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

    /// @sdlAPI SDL_GPUIndirectDrawCommand
    public static MemoryLayout PRIMITIVES_INDIRECT_DRAW_COMMAND = SDL_GPUIndirectDrawCommand.layout();
    /// @sdlAPI SDL_GPUIndexedIndirectDrawCommand
    public static MemoryLayout INDEXED_INDIRECT_DRAW_COMMAND = SDL_GPUIndexedIndirectDrawCommand.layout();
    /// @sdlAPI SDL_GPUIndirectDispatchCommand
    public static MemoryLayout INDIRECT_DISPATCH_COMMAND = SDL_GPUIndirectDispatchCommand.layout();

    private static MemoryLayout floatVector(int count) {
        return MemoryLayout.sequenceLayout(count, ValueLayout.JAVA_FLOAT);
    }
    private static MemoryLayout intVector(int count) {
        return MemoryLayout.sequenceLayout(count, ValueLayout.JAVA_INT);
    }

}
