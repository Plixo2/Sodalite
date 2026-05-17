package io.github.plixo2.sodalite.category.gpu;

import io.github.plixo2.sodalite.category.video.Window;
import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.nio.file.Path;
import java.util.List;

/// @apiNote SDL_GPUDevice
public class Device extends ResourceObject {
    private final MemorySegment segment;

    Device(
            ResourceSet resources,
            MemorySegment segment
    ) {
        resources.register(this, () -> GPU.destroyGPUDevice(segment));
        this.segment = segment;
    }

    public void claimWindowForDevice(Window window) {
        GPU.claimWindowForGPUDevice(this, window);
    }

    public void setSwapchainParameters(
            Window window,
            SwapchainComposition swapchainComposition,
            PresentMode presentMode
    ) {
        GPU.setGPUSwapchainParameters(this, window, swapchainComposition, presentMode);
    }

    public Buffer createBuffer(
            ResourceSet resources,
            @BufferUsageFlags int usageFlags,
            long size
    ) {
        return GPU.createGPUBuffer(resources, this, usageFlags, size);
    }

    public TransferBuffer createTransferBuffer(
            ResourceSet resources,
            TransferBufferUsage usage,
            long size
    ) {
        return GPU.createGPUTransferBuffer(resources, this, usage, size);
    }

    public TransferBuffer.Mapped mapTransferBuffer(
            TransferBuffer transferBuffer,
            Cycle cycle
    ) {
        return transferBuffer.mapTransferBuffer(this, cycle);
    }

    public <T extends Throwable> Shader createShader(
            ResourceSet resources,
            Shader.ShaderCreator<T> creator,
            ShaderStage stage
    ) throws T {
        var parameter = creator.parameter();
        return GPU.createGPUShader(
                resources,
                this,
                creator.source(),
                parameter.entryPoint(),
                creator.source().shaderFormat(),
                stage,
                parameter.num_samplers(),
                parameter.num_storage_textures(),
                parameter.num_storage_buffers(),
                parameter.num_uniform_buffers()
        );
    }

    public GraphicsPipeline createGPUGraphicsPipeline(
            ResourceSet resources,
            Shader vertexShader,
            Shader fragmentShader,
            PrimitiveType primitiveType,
            VertexInputState vertexInputState,
            RasterizerState rasterizerState,
            MultisampleState multisampleState,
            DepthStencilState depthStencilState,
            GraphicsPipelineTargetInfo targetInfo
    ) {

        return GPU.createGPUGraphicsPipeline(
                resources,
                this,
                vertexShader,
                fragmentShader,
                primitiveType,
                vertexInputState,
                rasterizerState,
                multisampleState,
                depthStencilState,
                targetInfo
        );
    }

    public <T extends Throwable> GraphicsPipeline createGPUGraphicsPipeline(
            ResourceSet resources,
            Shader.ShaderCreator<T> vertexShader,
            Shader.ShaderCreator<T> fragmentShader,
            PrimitiveType primitiveType,
            VertexInputState vertexInputState,
            RasterizerState rasterizerState,
            MultisampleState multisampleState,
            DepthStencilState depthStencilState,
            GraphicsPipelineTargetInfo targetInfo
    ) throws T {
        try (var pipelineResources = ResourceSet.ofConfined()) {
            var vs = createShader(
                    pipelineResources,
                    vertexShader,
                    ShaderStage.VERTEX
            );
            var fs = createShader(
                    pipelineResources,
                    fragmentShader,
                    ShaderStage.FRAGMENT
            );
            return createGPUGraphicsPipeline(
                    resources,
                    vs,
                    fs,
                    primitiveType,
                    vertexInputState,
                    rasterizerState,
                    multisampleState,
                    depthStencilState,
                    targetInfo
            );
        }
    }


    public TextureFormat getSwapchainTextureFormat(Window window) {
        return GPU.getGPUSwapchainTextureFormat(this, window);
    }

    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }

    public CommandBuffer acquireCommandBuffer() {
        return GPU.acquireGPUCommandBuffer(this);
    }



}
