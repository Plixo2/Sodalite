package io.github.plixo2.sodalite.category.gpu;

import com.google.errorprone.annotations.CheckReturnValue;
import io.github.plixo2.sodalite.category.video.Window;
import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.RequiredArgsConstructor;

import java.lang.foreign.MemorySegment;
import java.util.Objects;

/// @sdlAPI SDL_GPUDevice
public class Device extends ResourceObject {
    private final MemorySegment segment;

    Device(
            ResourceSet resources,
            MemorySegment segment
    ) {
        resources.register(this, () -> GPU.destroyGPUDevice(segment));
        this.segment = segment;
    }

    /// @return a AutoCloseable, which will release the claim when closed.
    @CheckReturnValue
    public WindowClaim claimWindow(Window window) {
        GPU.claimWindowForGPUDevice(this, window);
        return new WindowClaim(window);
    }

    public void releaseWindow(Window window) {
        GPU.releaseWindowFromGPUDevice(this, window);
    }

    public TextureFormat getSwapchainTextureFormat(Window window) {
        return GPU.getGPUSwapchainTextureFormat(this, window);
    }

    public void setSwapchainParameters(
            Window window,
            SwapchainComposition swapchainComposition,
            PresentMode presentMode
    ) {
        GPU.setGPUSwapchainParameters(this, window, swapchainComposition, presentMode);
    }

    public boolean supportsPresentMode(
            Window window,
            PresentMode presentMode
    ) {
        return GPU.windowSupportsGPUPresentMode(this, window, presentMode);
    }

    public boolean supportsSwapchainComposition(
            Window window,
            SwapchainComposition swapchainComposition
    ) {
        return GPU.windowSupportsGPUSwapchainComposition(this, window, swapchainComposition);
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

    @CheckReturnValue
    public TransferBuffer.Mapped mapTransferBuffer(
            TransferBuffer transferBuffer,
            Cycle cycle
    ) {
        return transferBuffer.mapTransferBuffer(this, cycle);
    }

    public <T extends Exception> Shader createShader(
            ResourceSet resources,
            Shader.Creator<T> creator,
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

    public <T extends Exception> GraphicsPipeline createGPUGraphicsPipeline(
            ResourceSet resources,
            Shader.Creator<T> vertexShader,
            Shader.Creator<T> fragmentShader,
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

    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }

    @CheckReturnValue
    public CommandBuffer acquireCommandBuffer() {
        return GPU.acquireGPUCommandBuffer(null, this);
    }

    @CheckReturnValue
    public CommandBuffer acquireCommandBuffer(FenceReference fenceReference) {
        return GPU.acquireGPUCommandBuffer(
                fenceReference,
                this
        );
    }

    /// Will release the window claim when closed
    @RequiredArgsConstructor
    public class WindowClaim implements AutoCloseable {
        private final Window window;

        @Override
        public void close() {
            Device.this.releaseWindow(this.window);
        }
    }

}
