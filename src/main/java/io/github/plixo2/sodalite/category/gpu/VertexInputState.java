package io.github.plixo2.sodalite.category.gpu;


import org.libsdl.sdl.SDL_GPUVertexAttribute;
import org.libsdl.sdl.SDL_GPUVertexBufferDescription;
import org.libsdl.sdl.SDL_GPUVertexInputState;

import java.lang.foreign.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/// @apiNote SDL_GPUVertexInputState
public record VertexInputState(
    BufferDescriptions vertexBufferDescriptions,
    Attributes vertexAttributes
) {
    public static VertexInputState of(
        BufferDescriptions vertexBufferDescriptions,
        Attributes vertexAttributes
    ) {
        return new VertexInputState(vertexBufferDescriptions, vertexAttributes);
    }

    public static VertexInputState of(
            VertexInputState... others
    ) {
        var vertexBufferDescriptions = new ArrayList<VertexBufferDescription>();
        var vertexAttributes = new ArrayList<VertexAttribute>();
        for (var other : others) {
            vertexBufferDescriptions.addAll(other.vertexBufferDescriptions.descriptions);
            vertexAttributes.addAll(other.vertexAttributes.attributes);
        }
        return new VertexInputState(
                new BufferDescriptions(vertexBufferDescriptions),
                new Attributes(vertexAttributes)
        );
    }

    public static VertexInputState of(
            int bindingSlot,
            StructLayout structLayout,
            VertexInputRate inputState
    ) {
        return fromStruct(structLayout, bindingSlot, inputState);
    }

    public record BufferDescriptions(
            List<VertexBufferDescription> descriptions
    ) {
        public static BufferDescriptions of(VertexBufferDescription... vertexBufferDescriptions) {
            return new BufferDescriptions(List.of(vertexBufferDescriptions));
        }
    }
    public record Attributes(
            List<VertexAttribute> attributes
    ) {
        public static Attributes of(VertexAttribute... vertexAttributes) {
            return new Attributes(List.of(vertexAttributes));
        }
    }

    /// @apiNote SDL_GPUVertexBufferDescription
    public record VertexBufferDescription(
        int bindingSlot,
        int pitchAkaStride,
        VertexInputRate inputRate
    ) {
        public static VertexBufferDescription of(
            int bindingSlot,
            int pitchAkaStride,
            VertexInputRate inputRate
        ) {
            return new VertexBufferDescription(bindingSlot, pitchAkaStride, inputRate);
        }
        public static VertexBufferDescription of(
                int bindingSlot,
                long pitchAkaStride,
                VertexInputRate inputRate
        ) {
            return new VertexBufferDescription(bindingSlot, (int) pitchAkaStride, inputRate);
        }
    }

    /// @apiNote SDL_GPUVertexAttribute
    public record VertexAttribute(
        int location,
        int bufferSlot,
        VertexElementFormat format,
        int offset
    ) {
        public static VertexAttribute of(
            int location,
            int bufferSlot,
            VertexElementFormat format,
            int offset
        ) {
            return new VertexAttribute(location, bufferSlot, format, offset);
        }
    }

    /// @apiNote SDL_GPUVertexInputRate
    public enum VertexInputRate {
        VERTEX,
        INSTANCE,

        ;

        public int code() {
            return this.ordinal();
        }
    }

    void put(SegmentAllocator arena, MemorySegment segment) {
        var bufferDescriptions = this.vertexBufferDescriptions.descriptions;
        var vertexBufferDescriptionsSegment = SDL_GPUVertexBufferDescription
                .allocateArray(bufferDescriptions.size(), arena);

        for (var i = 0; i < bufferDescriptions.size(); i++) {
            var vertexBufferDescriptionSegment = SDL_GPUVertexBufferDescription
                    .asSlice(vertexBufferDescriptionsSegment, i);
            var vertexBufferDescription = bufferDescriptions.get(i);
            SDL_GPUVertexBufferDescription.initialize(
                    vertexBufferDescriptionSegment,
                    vertexBufferDescription.bindingSlot,
                    vertexBufferDescription.pitchAkaStride,
                    vertexBufferDescription.inputRate.code(),
                    0
            );
        }


        var attributes = this.vertexAttributes.attributes;
        var vertexAttributesSegment = SDL_GPUVertexAttribute
                .allocateArray(attributes.size(), arena);

        for (var i = 0; i < attributes.size(); i++) {
            var vertexAttributeSegment = SDL_GPUVertexAttribute
                    .asSlice(vertexAttributesSegment, i);
            var vertexAttribute = attributes.get(i);
            SDL_GPUVertexAttribute.initialize(
                    vertexAttributeSegment,
                    vertexAttribute.location,
                    vertexAttribute.bufferSlot,
                    vertexAttribute.format.code(),
                    vertexAttribute.offset
            );
        }

        SDL_GPUVertexInputState.initialize(
                segment,
                vertexBufferDescriptionsSegment,
                bufferDescriptions.size(),
                vertexAttributesSegment,
                attributes.size()
        );

    }



    private static VertexInputState fromStruct(
            StructLayout structLayout,
            int bindingSlot,
            VertexInputRate inputState
    ) {
        List<VertexAttribute> attributes = new ArrayList<>();

        var members = structLayout.memberLayouts();
        for (int i = 0; i < members.size(); i++) {
            MemoryLayout member = members.get(i);
            if (member instanceof PaddingLayout) continue;

            long offset = structLayout.byteOffset(MemoryLayout.PathElement.groupElement(i));


            var attrib = VertexAttribute.of(
                    i,
                    bindingSlot,
                    fromMemoryLayout(member),
                    (int) offset
            );
            attributes.add(attrib);
        }


        var bufferDescription = BufferDescriptions.of(
                VertexBufferDescription.of(
                        bindingSlot,
                        structLayout.byteSize(),
                        inputState
                )
        );

        return new VertexInputState(
                bufferDescription,
                new Attributes(attributes)
        );
    }

    private static VertexElementFormat fromMemoryLayout(
            MemoryLayout layout
    ) {
        switch (layout) {
            case GroupLayout _ -> {
                throw unsupportedLayout(layout);
            }
            case PaddingLayout _ -> {
                throw new IllegalStateException("should be filted by caller");
            }
            case SequenceLayout sequenceLayout -> {
                var elementLayout = sequenceLayout.elementLayout();
                var count = sequenceLayout.elementCount();
                return fromPrimitiveMemoryLayout(
                        (int) count,
                        elementLayout
                );
            }
            case ValueLayout valueLayout -> {
                return fromPrimitiveMemoryLayout(
                        1,
                        valueLayout
                );
            }
        }
    }


    private static VertexElementFormat fromPrimitiveMemoryLayout(
            int count,
            MemoryLayout layout
    ) {
        if (layout instanceof ValueLayout valueLayout) {
            var availableLayouts = layouts.get(valueLayout.carrier());
            if (availableLayouts == null) {
                var typeName = valueLayoutToString(valueLayout);
                throw new IllegalArgumentException("Unsupported primitive type for vertex attribute: " + typeName);
            }
            var format = availableLayouts.get(count);
            if (format == null) {
                var typeName = valueLayoutToString(valueLayout);
                throw new IllegalArgumentException("Unsupported primitive " + typeName + " for vertex attribute with count " + count);
            }
            return format;
        } else {
            throw unsupportedLayout(layout);
        }
    }

    private static String valueLayoutToString(ValueLayout layout) {
        if (layout instanceof AddressLayout) {
            return "Address";
        } else {
            return layout.carrier().getSimpleName();
        }
    }

    private static final Map<Class<?>, Map<Integer, VertexElementFormat>> layouts = Map.of(
            byte.class, Map.of(
                    2, VertexElementFormat.BYTE2,
                    4, VertexElementFormat.BYTE4
            ),
            float.class, Map.of(
                    1, VertexElementFormat.FLOAT,
                    2, VertexElementFormat.FLOAT2,
                    3, VertexElementFormat.FLOAT3,
                    4, VertexElementFormat.FLOAT4
            ),
            int.class, Map.of(
                    1, VertexElementFormat.INT,
                    2, VertexElementFormat.INT2,
                    3, VertexElementFormat.INT3,
                    4, VertexElementFormat.INT4
            ),
            short.class, Map.of(
                    2, VertexElementFormat.SHORT2,
                    4, VertexElementFormat.SHORT4
            )
    );


    private static RuntimeException unsupportedLayout(MemoryLayout layout) {
        return new IllegalArgumentException(
                "'" + layout + "'"
                + " is not supported for vertex attributes. "
                + "Only value layouts, or sequences of value layouts, "
                + "are supported for vertex attributes"
        );
    }


}
