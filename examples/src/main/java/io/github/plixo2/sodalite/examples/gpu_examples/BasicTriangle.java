package io.github.plixo2.sodalite.examples.gpu_examples;

import io.github.plixo2.sodalite.category.gpu.*;
import io.github.plixo2.sodalite.category.rect.Rect;
import io.github.plixo2.sodalite.category.video.WindowFlags;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.joml.Vector4f;

import java.io.IOException;

/// BasicTriangle.c
public class BasicTriangle extends Common {
    BasicTriangle() {
        super("BasicTriangle", WindowFlags.NONE);
    }

    static final Viewport smallViewport = Viewport.of(
            160, 120, 320, 240, 0.1f, 1.0f
    );
    static final Rect scissorRect = Rect.of(320, 240, 320, 240);

    GraphicsPipeline fillPipeline;
    GraphicsPipeline linePipeline;

    boolean useWireframeMode = false;
    boolean useSmallViewport = false;
    boolean useScissorRect = false;

    @Override
    public void init() throws Exception {
        this.fillPipeline = createPipeline(FillMode.FILL);
        this.linePipeline = createPipeline(FillMode.LINE);
    }

    private GraphicsPipeline createPipeline(FillMode fillMode) throws IOException {
        return this.device().createGraphicsPipeline(
                ResourceSet.global(),
                shader("RawTriangle.vert", 0, 0, 0, 0),
                shader("SolidColor.frag", 0, 0, 0, 0),
                PrimitiveType.TRIANGLELIST,
                VertexInputState.of(),
                RasterizerState.of(fillMode, CullMode.NONE, FrontFace.defaultValue()),
                MultisampleState.disabled(),
                DepthStencilState.disabled(),
                GraphicsPipelineTargetInfo.of(
                    ColorTargetDescription.of(
                        this.device().getSwapchainTextureFormat(this.window()),
                        ColorTargetBlendState.disabled()
                    )
                )
        );
    }

    @Override
    protected void onLeftPressed() {
        this.useWireframeMode = !this.useWireframeMode;
    }

    @Override
    protected void onDownPressed() {
        this.useSmallViewport = !this.useSmallViewport;
    }

    @Override
    protected void onRightPressed() {
        this.useScissorRect = !this.useScissorRect;
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
                if (this.useWireframeMode) {
                    renderPass.bindPipeline(this.linePipeline);
                } else {
                    renderPass.bindPipeline(this.fillPipeline);
                }
                if (this.useSmallViewport) {
                    renderPass.setViewport(smallViewport);
                }
                if (this.useScissorRect) {
                    renderPass.setScissor(scissorRect);
                }

                renderPass.drawPrimitives( 3, 1, 0, 0);
            }
        }
    }

    @Override
    public void quit() {

    }
}
