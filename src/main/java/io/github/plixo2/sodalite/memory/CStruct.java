package io.github.plixo2.sodalite.memory;

import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.Getter;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.StructLayout;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/// @see ConstantWriteBuffer
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

    /// @throws IllegalArgumentException if the size of `layout` exceeds 2^32 bytes (4 GiB)
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


}
