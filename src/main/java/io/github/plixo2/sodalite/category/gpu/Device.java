package io.github.plixo2.sodalite.category.gpu;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import io.github.plixo2.sodalite.category.video.Window;
import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.foreign.MemorySegment;

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

    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }

    /// @return a AutoCloseable, which will release the claim when closed.
    @CanIgnoreReturnValue
    public WindowClaim claimWindow(Window window) {
        GPU.claimWindowForDevice(this, window);
        return new WindowClaim(window);
    }

    @CheckReturnValue
    public CommandBuffer acquireCommandBuffer() {
        return GPU.acquireGPUCommandBuffer(this);
    }

    public TextureFormat getSwapchainTextureFormat(Window window) {
        return GPU.getSwapchainTextureFormat(this, window);
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
        return GPU.windowSupportsPresentMode(this, window, presentMode);
    }

    public boolean supportsSwapchainComposition(
            Window window,
            SwapchainComposition swapchainComposition
    ) {
        return GPU.windowSupportsSwapchainComposition(this, window, swapchainComposition);
    }

    public boolean supportsTextureFormat(
            TextureFormat format,
            TextureType type,
            @TextureUsageFlags int usageFlags
    ) {
        return GPU.textureSupportsFormat(this, format, type, usageFlags);
    }
    public boolean supportsTextureSampleCount(
            TextureFormat format,
            SampleCount sampleCount
    ) {
        return GPU.textureSupportsSampleCount(this, format, sampleCount);
    }

    public Buffer createBuffer(
            ResourceSet resources,
            @BufferUsageFlags int usageFlags,
            long size
    ) {
        return GPU.createBuffer(resources, this, usageFlags, size);
    }

    public Buffer createBuffer(
            ResourceSet resources,
            String name,
            @BufferUsageFlags int usageFlags,
            long size
    ) {
        var buffer = createBuffer(resources, usageFlags, size);
        buffer.setName(this, name);
        return buffer;
    }

    public TransferBuffer createTransferBuffer(
            ResourceSet resources,
            TransferBufferUsage usage,
            long size
    ) {
        return GPU.createTransferBuffer(resources, this, usage, size);
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
                creator.shaderFormat(),
                stage,
                parameter.numSamplers(),
                parameter.numStorageTextures(),
                parameter.numStorageBuffers(),
                parameter.numUniformBuffers()
        );
    }

    public GraphicsPipeline createGraphicsPipeline(
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
        return GPU.createGraphicsPipeline(
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

    public <T extends Exception> GraphicsPipeline createGraphicsPipeline(
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
            return createGraphicsPipeline(
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

    public <T extends Exception> ComputePipeline createComputePipeline(
            ResourceSet resources,
            ComputeShader.Creator<T> creator
    ) throws T {
        return GPU.createComputePipeline(
                resources,
                this,
                creator
        );
    }

    public GPUDriver getDriver() {
        return GPU.getDeviceDriver(this);
    }
    public @ShaderFormat int getShaderFormats() {
        return GPU.getShaderFormats(this);
    }
    public boolean supportsShaderFormat(@ShaderFormat int shaderFormat) {
        return (getShaderFormats() & shaderFormat) == shaderFormat;
    }

    public void setAllowedFramesInFlight(int maxFramesInFlight) {
        GPU.setAllowedFramesInFlight(this, maxFramesInFlight);
    }

    public void waitForIdle() {
        GPU.waitForIdle(this);
    }

    public void waitForSwapchain(Window window) {
        GPU.waitForSwapchain(this, window);
    }

    @Override
    public String toString() {
        return "Device{" +
                "segment=" + this.segment.address() +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Device other && this.segment.address() == other.segment.address();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.segment.address());
    }

    /// Will release the window claim when closed
    @Getter
    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public class WindowClaim implements AutoCloseable {
        private final Window window;

        @Override
        public void close() {
            GPU.releaseWindowFromGPUDevice(Device.this, this.window);
        }
    }

}
