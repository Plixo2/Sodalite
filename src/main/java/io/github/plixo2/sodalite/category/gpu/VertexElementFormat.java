package io.github.plixo2.sodalite.category.gpu;

/// @apiNote SDL_GPUVertexElementFormat
public enum VertexElementFormat {
    INVALID,

    /* 32-bit Signed Integers */
    INT,
    INT2,
    INT3,
    INT4,

    /* 32-bit Unsigned Integers */
    UINT,
    UINT2,
    UINT3,
    UINT4,

    /* 32-bit Floats */
    FLOAT,
    FLOAT2,
    FLOAT3,
    FLOAT4,

    /* 8-bit Signed Integers */
    BYTE2,
    BYTE4,

    /* 8-bit Unsigned Integers */
    UBYTE2,
    UBYTE4,

    /* 8-bit Signed Normalized */
    BYTE2_NORM,
    BYTE4_NORM,

    /* 8-bit Unsigned Normalized */
    UBYTE2_NORM,
    UBYTE4_NORM,

    /* 16-bit Signed Integers */
    SHORT2,
    SHORT4,

    /* 16-bit Unsigned Integers */
    USHORT2,
    USHORT4,

    /* 16-bit Signed Normalized */
    SHORT2_NORM,
    SHORT4_NORM,

    /* 16-bit Unsigned Normalized */
    USHORT2_NORM,
    USHORT4_NORM,

    /* 16-bit Floats */
    HALF2,
    HALF4,

    ;

    public int code() {
        return this.ordinal();
    }
}
