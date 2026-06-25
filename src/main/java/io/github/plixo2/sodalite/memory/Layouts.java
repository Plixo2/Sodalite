package io.github.plixo2.sodalite.memory;


import org.libsdl.sdl.SDL_GPUIndexedIndirectDrawCommand;
import org.libsdl.sdl.SDL_GPUIndirectDispatchCommand;
import org.libsdl.sdl.SDL_GPUIndirectDrawCommand;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.SequenceLayout;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;

public class Layouts {

    public static MemoryLayout FLOAT = ValueLayout.JAVA_FLOAT;
    public static MemoryLayout INT = ValueLayout.JAVA_INT;
    public static MemoryLayout UINT = ValueLayout.JAVA_INT;
    public static MemoryLayout UINT8 = ValueLayout.JAVA_BYTE;
    public static MemoryLayout INT8 = ValueLayout.JAVA_BYTE;
    public static MemoryLayout INT16 = ValueLayout.JAVA_SHORT;
    public static MemoryLayout UINT16 = ValueLayout.JAVA_SHORT;

    public static MemoryLayout FLOAT_2 = floatArray(2);
    public static MemoryLayout FLOAT_3 = floatArray(3);
    public static MemoryLayout FLOAT_4 = floatArray(4);

    public static MemoryLayout INT_2 = intArray(2);
    public static MemoryLayout INT_3 = intArray(3);
    public static MemoryLayout INT_4 = intArray(4);

    public static MemoryLayout FLOAT_2X2 = floatArray(4);
    public static MemoryLayout FLOAT_3X3 = floatArray(9);
    public static MemoryLayout FLOAT_4X4 = floatArray(16);

    public static MemoryLayout UINT8_2 = byteArray(2);
    public static MemoryLayout UINT8_3 = byteArray(3);
    public static MemoryLayout UINT8_4 = byteArray(4);

    /// @sdlAPI SDL_GPUIndirectDrawCommand
    public static StructLayout PRIMITIVES_INDIRECT_DRAW_COMMAND = (StructLayout) SDL_GPUIndirectDrawCommand.layout();
    /// @sdlAPI SDL_GPUIndexedIndirectDrawCommand
    public static StructLayout INDEXED_INDIRECT_DRAW_COMMAND = (StructLayout) SDL_GPUIndexedIndirectDrawCommand.layout();
    /// @sdlAPI SDL_GPUIndirectDispatchCommand
    public static StructLayout INDIRECT_DISPATCH_COMMAND = (StructLayout) SDL_GPUIndirectDispatchCommand.layout();

    public static SequenceLayout floatArray(int count) {
        return MemoryLayout.sequenceLayout(count, ValueLayout.JAVA_FLOAT);
    }
    public static SequenceLayout intArray(int count) {
        return MemoryLayout.sequenceLayout(count, ValueLayout.JAVA_INT);
    }
    public static SequenceLayout byteArray(int count) {
        return MemoryLayout.sequenceLayout(count, ValueLayout.JAVA_BYTE);
    }

}
