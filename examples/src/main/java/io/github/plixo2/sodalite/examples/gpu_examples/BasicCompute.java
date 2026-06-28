package io.github.plixo2.sodalite.examples.gpu_examples;

import io.github.plixo2.sodalite.category.gpu.*;
import io.github.plixo2.sodalite.category.video.WindowFlags;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.joml.Vector2i;
import org.joml.Vector4f;


/// BasicCompute.c
public class BasicCompute extends Common {
    BasicCompute() {
        super("BasicCompute", WindowFlags.NONE);
    }

    ComputePipeline pipeline;
    GraphicsPipeline drawPipeline;
    Texture texture;
    Sampler sampler;
    Buffer vertexBuffer;
    @Override
    public void init() throws Exception {
        this.pipeline = computePipeline(
                "FillTexture.comp",
                ComputeShader.ThreadCount.of(8, 8),
                ComputeShader.Parameters.none()
                    .withReadwriteStorageTextures(1)
        );
        this.drawPipeline = this.device().createGraphicsPipeline(
                ResourceSet.global(),
                shader("TexturedQuad.vert", 0, 0, 0, 0),
                shader("TexturedQuad.frag", 1, 0, 0, 0),
                PrimitiveType.TRIANGLELIST,
                VertexInputState.of(0, PositionTextureVertex, VertexInputState.Rate.VERTEX),
                RasterizerState.defaultValue(),
                MultisampleState.disabled(),
                DepthStencilState.disabled(),
                GraphicsPipelineTargetInfo.of(
                    ColorTargetDescription.of(
                        this.device().getSwapchainTextureFormat(this.window()),
                        ColorTargetBlendState.disabled()
                    )
                )
        );
        var windowSize = this.window().getSizeInPixels(new Vector2i());

        this.texture = TextureBuilder.of2D(
                TextureFormat.R8G8B8A8_UNORM,
                TextureUsageFlags.COMPUTE_STORAGE_WRITE | TextureUsageFlags.SAMPLER,
                windowSize.x,
                windowSize.y
        ).build(ResourceSet.global(), this.device());

        this.sampler = SamplerBuilder.of(
                Filter.NEAREST,
                SamplerAddressMode.REPEAT
        ).build(ResourceSet.global(), this.device());

        this.vertexBuffer = this.device().createBuffer(
                ResourceSet.global(),
                BufferUsageFlags.VERTEX,
                PositionTextureVertex.byteSize() * 6
        );

        upload(
                this.vertexBuffer,
                new float[][]{
                        { -1, -1, 0, 0, 0 },
                        {  1, -1, 0, 1, 0 },
                        {  1,  1, 0, 1, 1 },
                        { -1, -1, 0, 0, 0 },
                        {  1,  1, 0, 1, 1 },
                        { -1,  1, 0, 0, 1 }
                }
        );

        try (var commandBuffer = this.device().acquireCommandBuffer()) {
            try (var computePass = commandBuffer.beginComputePass(
                    ComputePass.Binding.texture(
                            this.texture,
                            Cycle.FALSE
                    )
            )) {
                computePass.bindPipeline(this.pipeline);
                computePass.dispatch(windowSize.x / 8, windowSize.y / 8, 1);
            }
        }

    }

    @Override
    public void update() throws Exception {

    }

    @Override
    public void draw() throws Exception {
        try (var commandBuffer = this.device().acquireCommandBuffer()) {
            var swapchain = commandBuffer.waitAndAcquireSwapchainTexture(this.window());
            if (swapchain == null) {
                return;
            }
            var colorTarget0 = RenderPass.ColorTargetInfo.clear(
                    swapchain,
                    new Vector4f(0, 0, 0, 1f),
                    Cycle.FALSE
            );
            try (var renderPass = commandBuffer.beginRenderPass(null, colorTarget0)) {
                renderPass.bindPipeline(this.drawPipeline);
                renderPass.bindVertexBuffer(0, this.vertexBuffer);
                renderPass.bindFragmentSampler(0, this.texture, this.sampler);
                renderPass.drawPrimitives(6, 1, 0, 0);
            }
        }
    }

    @Override
    public void quit() {

    }
}
