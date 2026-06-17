package io.github.plixo2.sodalite.examples;

import io.github.plixo2.sodalite.category.gpu.*;
import io.github.plixo2.sodalite.category.init.AppResult;
import io.github.plixo2.sodalite.category.main.Callbacks;
import io.github.plixo2.sodalite.category.timer.Timer;
import io.github.plixo2.sodalite.category.video.Video;
import io.github.plixo2.sodalite.category.video.Window;
import io.github.plixo2.sodalite.category.video.WindowFlags;
import io.github.plixo2.sodalite.memory.CStruct;
import io.github.plixo2.sodalite.memory.ConstantWriteBuffer;
import io.github.plixo2.sodalite.memory.Layouts;
import io.github.plixo2.sodalite.memory.WriteBuffer;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.joml.Vector4f;

import java.io.IOException;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.StructLayout;

/// Port of
/// ["SDL_gpu: it begins with a triangle" by Hamdy Elzanqali](https://hamdy-elzanqali.medium.com/let-there-be-triangles-sdl-gpu-edition-bd82cf2ef615)
public class GPUHelloTriangle implements Callbacks {
    static Vector4f CLEAR_COLOR = new Vector4f(240/255.0f, 240/255.0f, 240/255.0f, 255/255.0f);

    /// struct Vertex
    /// {
    ///     float x, y, z;      //vec3 position
    ///     float r, g, b, a;   //vec4 color
    /// };
    static StructLayout Vertex = MemoryLayout.structLayout(
            Layouts.VECTOR_3F.withName("position"),
            Layouts.VECTOR_4F.withName("color")
    );
    WriteBuffer<?> vertices = ConstantWriteBuffer.allocate(ResourceSet.global(), Vertex, 3)
         .writeFloats( 0.0f,  0.5f, 0.0f,   1.0f, 0.0f, 0.0f, 1.0f)
         .writeFloats(-0.5f, -0.5f, 0.0f,   1.0f, 1.0f, 0.0f, 1.0f)
         .writeFloats( 0.5f, -0.5f, 0.0f,   1.0f, 0.0f, 1.0f, 1.0f);


    /// struct UniformBuffer
    /// {
    ///     float time;
    /// };
    static StructLayout UniformBuffer = MemoryLayout.structLayout(
        Layouts.FLOAT.withName("time")
    );
    CStruct timeUniform = CStruct.allocate(ResourceSet.global(), UniformBuffer);

    Window window;
    Device device;
    Device.WindowClaim claim;
    Buffer vertexBuffer;
    GraphicsPipeline pipeline;

    @Override
    public AppResult onWindowCloseRequested(long timestamp, int windowID) {
        if (this.window.id() != windowID) {
            return AppResult.CONTINUE;
        }
        return AppResult.SUCCESS;
    }

    @Override
    public AppResult init(String[] args) throws IOException {
        this.window = Video.createWindow(
                ResourceSet.global(),
                "Hello, Triangle!",
                960, 540,
                WindowFlags.RESIZABLE
        );
        this.device = GPU.createDevice(
                ResourceSet.global(),
                ShaderFormat.SPIRV | ShaderFormat.DXIL,
                true,
                GPUDriver.optimal()
        );
        this.claim = this.device.claimWindow(this.window);

        var format = this.device.supportsShaderFormat(ShaderFormat.SPIRV)
                ? ShaderFormat.SPIRV
                : ShaderFormat.DXIL;
        var ext = format == ShaderFormat.SPIRV ? "spv" : "dxil";
        var dir = format == ShaderFormat.SPIRV ? "spirv" : "dxil";

        this.pipeline = this.device.createGraphicsPipeline(
                ResourceSet.global(),
                Shader.Creator.of(
                        format,
                        GPUHelloTriangle.class.getResourceAsStream("/GPUHelloTriangle/" + dir + "/vertex." + ext),
                        Shader.Parameters.of(0, 0, 0, 0)
                ),
                Shader.Creator.of(
                        format,
                        GPUHelloTriangle.class.getResourceAsStream("/GPUHelloTriangle/" + dir + "/fragment." + ext),
                        Shader.Parameters.of(0, 0, 0, 1)
                ),
                PrimitiveType.TRIANGLELIST,
                VertexInputState.of(0, Vertex, VertexInputState.Rate.VERTEX),
                RasterizerState.defaultValue(),
                MultisampleState.disabled(),
                DepthStencilState.disabled(),
                GraphicsPipelineTargetInfo.of(
                        this.device.getSwapchainTextureFormat(this.window),
                        ColorTargetBlendState.standardAlphaBlend()
                )
        );

        this.vertexBuffer = this.device.createBuffer(
                ResourceSet.global(),
                BufferUsageFlags.VERTEX,
                this.vertices.capacity()
        );

        try (var transferSet = ResourceSet.ofConfined()) {
            var transferBuffer = this.device.createTransferBuffer(
                    transferSet,
                    TransferBufferUsage.UPLOAD,
                    this.vertexBuffer.size()
            );
            try (var mapped = transferBuffer.map(this.device,Cycle.FALSE)) {
                mapped.memory().copyFrom(this.vertices.memory());
            }
            try (var commandBuffer = this.device.acquireCommandBuffer()) {
                try (var copyPass = commandBuffer.beginCopyPass()){
                    copyPass.upload(transferBuffer, this.vertexBuffer, Cycle.FALSE);
                }
            }
        }

        return AppResult.CONTINUE;
    }

    @Override
    public AppResult iterate() {
        try (var commandBuffer = this.device.acquireCommandBuffer()) {
            var swapchain = commandBuffer.waitAndAcquireSwapchainTexture(this.window);
            if (swapchain == null) {
                return AppResult.CONTINUE;
            }
            var colorTarget0 = RenderPass.ColorTargetInfo.clear(
                    swapchain,
                    CLEAR_COLOR,
                    Cycle.FALSE
            );
            try (var renderPass = commandBuffer.beginRenderPass(null, colorTarget0)) {
                renderPass.bindPipeline(this.pipeline);
                renderPass.bindVertexBuffer(0, this.vertexBuffer);
                this.timeUniform.at("time").writeFloat(Timer.getTicksNS() / 1e9f);
                commandBuffer.pushFragmentUniform(0, this.timeUniform);
                renderPass.drawPrimitives(3, 1, 0, 0);
            }
        }
        return AppResult.CONTINUE;
    }

    @Override
    public void quit(AppResult result) {
        if (this.claim != null) {
            this.claim.close();
        }
    }
}
