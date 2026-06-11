package io.github.plixo2.uiiii;

import io.github.plixo2.sodalite.category.gpu.*;
import io.github.plixo2.sodalite.memory.CStruct;
import io.github.plixo2.sodalite.memory.Layouts;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.joml.Matrix4f;
import org.joml.Vector4i;

import java.io.IOException;
import java.lang.foreign.MemoryLayout;
import java.nio.file.Path;

public class RectBatch extends DrawBatch {


    /*
    struct Rect {
        float4x4 transform;
        float4 coords;  // x, y, width, height
        float4 fill_color;
        float4 outline_color_or_uv;
        float radius;
        float outline_width;
        uint texture;
        uint isTexture;
    };

    cbuffer Uniforms : register(b0, space1) {
        float4x4 projection;
        int      startIndex;
    };

    */

    private static final MemoryLayout RECT_LAYOUT = MemoryLayout.structLayout(
            Layouts.MAT_4F.withName("transform"),
            Layouts.VECTOR_4F.withName("coords"),
            Layouts.VECTOR_4F.withName("fill_color"),
            Layouts.VECTOR_4F.withName("outline_color_or_uv"),
            Layouts.FLOAT.withName("radius"),
            Layouts.FLOAT.withName("outline_width"),
            Layouts.UINT.withName("texture"),
            Layouts.UINT.withName("isTexture")
    );

    private static final MemoryLayout UNIFORM_LAYOUT = MemoryLayout.structLayout(
            Layouts.MAT_4F.withName("projection"),
            Layouts.INT.withName("startIndex")
    );

    private final CStruct uniform;
    private final long startIndexOffset;
    private final GraphicsPipeline pipeline;
    private final Texture empty;
    private final Sampler emptySampler;
    private final SoftStack<SamplerArray> samplerArrayStack;

    public RectBatch(
            ResourceSet resources,
            Device device,
            TextureInfo colorTarget
    ) throws IOException {
        super(resources, device, RECT_LAYOUT.byteSize() * 1024);
        this.uniform = CStruct.allocate(resources, UNIFORM_LAYOUT);
        this.startIndexOffset = this.uniform.offsetOf(Layouts.INT.withName("startIndex"));
        this.pipeline = createPipeline(device, resources, colorTarget);

        this.empty = TextureBuilder.of2D(
                TextureFormat.R8G8B8A8_UNORM,
                TextureUsageFlags.SAMPLER,
                1, 1
        ).build(resources, device);

        this.emptySampler = SamplerBuilder.of(
                Filter.LINEAR,
                SamplerAddressMode.REPEAT,
                0f
        ).build(resources, device);

        this.samplerArrayStack = new SoftStack<>(() -> new SamplerArray(8, this.empty, this.emptySampler));
    }

    private static GraphicsPipeline createPipeline(
            Device gpu,
            ResourceSet resources,
            TextureInfo colorTarget
    ) throws IOException {
        return gpu.createGPUGraphicsPipeline(
            resources,
            Shader.ShaderCreator.of(
                    ShaderFormat.SPIRV,
                    Path.of("resources/uber/bin/vertex.spv"),
                    Shader.Parameters.of(0, 0, 1, 1)
            ),
            Shader.ShaderCreator.of(
                    ShaderFormat.SPIRV,
                    Path.of("resources/uber/bin/fragment.spv"),
                    Shader.Parameters.of(8, 0, 0, 0)
            ),
            PrimitiveType.TRIANGLESTRIP,
            VertexInputState.of(),
            RasterizerState.of(FillMode.FILL, CullMode.NONE, FrontFace.defaultValue()),
            MultisampleState.enabled(colorTarget.sampleCount()),
            DepthStencilState.disabled(),
            GraphicsPipelineTargetInfo.of(colorTarget.format(), ColorTargetBlendState.standardAlphaBlend())
        );
    }

    @Override
    protected void beginSection(Vector4i scissor) {
        super.beginSection(scissor);
        this.samplerArrayStack.push().clear();
    }

    public int addTexture(Texture texture, Sampler sampler) {
        return currentSamplerArray().put(texture, sampler);
    }

    @Override
    protected void upload(Device device, CopyPass copyPass, Matrix4f projection) {
        super.upload(device, copyPass, projection);

        this.uniform.clear().writeMatrix4f(projection);
    }

    @Override
    void draw(
            RenderPass renderPass,
            CommandBuffer commandBuffer,
            int startIndex,
            int count
    ) {
        renderPass.bindPipeline(this.pipeline);

        this.uniform.seek(this.startIndexOffset).writeInt(startIndex);
        commandBuffer.pushVertexUniform(0, this.uniform);

        var samplerArray = getSamplerArray();
        samplerArray.bindFragmentSamplers(renderPass, 0);

        renderPass.bindVertexStorageBuffer(0, this.gpuBuffer);
        renderPass.drawPrimitives(4, count, 0, 0);
    }

    @Override
    protected void reset() {
        this.samplerArrayStack.clear();
        super.reset();
    }

    private SamplerArray currentSamplerArray() {
        return this.samplerArrayStack.peek();
    }

    private SamplerArray getSamplerArray() {
        return this.samplerArrayStack.get(this.replaySection);
    }
}
