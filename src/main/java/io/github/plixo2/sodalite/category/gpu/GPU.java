package io.github.plixo2.sodalite.category.gpu;

import io.github.plixo2.sodalite.category.video.Window;
import io.github.plixo2.sodalite.memory.CStruct;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.jetbrains.annotations.Nullable;
import org.libsdl.sdl.*;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

public class GPU {

    /// @apiNote SDL_CreateGPUDevice
    public static Device createDevice(
            ResourceSet resources,
            @ShaderFormat int shaderFormat,
            boolean debugMode,
            PreferredGPUDriver preferredDriver
    ) {
        var device = check(SDL_CreateGPUDevice(shaderFormat, debugMode, preferredDriver.nameSegment()));
        return new Device(resources, device);
    }

    /// @apiNote SDL_DestroyGPUDevice
    static void destroyGPUDevice(MemorySegment device) {
        SDL_DestroyGPUDevice(device);
    }

    /// @apiNote SDL_ClaimWindowForGPUDevice
    static void claimWindowForGPUDevice(Device gpuDevice, Window window) {
        check(SDL_ClaimWindowForGPUDevice(gpuDevice.segment(), window.segment()));
    }


    /// @apiNote SDL_SetGPUSwapchainParameters
    static void setGPUSwapchainParameters(
            Device gpuDevice,
            Window window,
            SwapchainComposition swapchainComposition,
            PresentMode presentMode
    ) {
        check(SDL_SetGPUSwapchainParameters(
                gpuDevice.segment(),
                window.segment(),
                swapchainComposition.code(),
                presentMode.code()
        ));
    }


    /// @apiNote SDL_ReleaseGPUTexture
    static void releaseGPUTexture(Device device, MemorySegment texture) {
        SDL_ReleaseGPUTexture(device.segment(), texture);
    }

    /// @apiNote SDL_AcquireGPUCommandBuffer
    static CommandBuffer acquireGPUCommandBuffer(Device gpuDevice) {
        var commandBuffer = check(SDL_AcquireGPUCommandBuffer(gpuDevice.segment()));
        return new CommandBuffer(commandBuffer);
    }

    /// @apiNote SDL_SubmitGPUCommandBuffer
    static void submitGPUCommandBuffer(CommandBuffer commandBuffer) {
        check(SDL_SubmitGPUCommandBuffer(commandBuffer.segment()));
    }

    /// @apiNote SDL_CancelGPUCommandBuffer
    static void cancelGPUCommandBuffer(CommandBuffer commandBuffer) {
        check(SDL_CancelGPUCommandBuffer(commandBuffer.segment()));
    }

    /// @apiNote SDL_BindGPUGraphicsPipeline
    static void bindGPUGraphicsPipeline(RenderPass renderPass, GraphicsPipeline graphicsPipeline) {
        SDL_BindGPUGraphicsPipeline(renderPass.segment(), graphicsPipeline.segment());
    }

    /// @apiNote SDL_BindGPUVertexBuffers
    static void bindGPUVertexBuffer(
            RenderPass renderPass,
            int slot,
            Buffer buffer,
            int bufferOffset
    ) {
        try (var arena = Arena.ofConfined()) {
            var bufferBinding = SDL_GPUBufferBinding.allocate(arena);
            SDL_GPUBufferBinding.initialize(
                    bufferBinding,
                    buffer.segment(),
                    bufferOffset
            );
            SDL_BindGPUVertexBuffers(renderPass.segment(), slot, bufferBinding, 1);
        }
    }

    /// @apiNote SDL_BindGPUVertexStorageBuffers
    static void bindGPUVertexStorageBuffer(
            RenderPass renderPass,
            int slot,
            Buffer buffer
    ) {
        try (var arena = Arena.ofConfined()) {
            var pointerBuffer = arena.allocate(ValueLayout.ADDRESS);
            pointerBuffer.set(ValueLayout.ADDRESS, 0, buffer.segment());
            SDL_BindGPUVertexStorageBuffers(renderPass.segment(), slot, pointerBuffer, 1);
        }
    }

    /// @apiNote SDL_DrawGPUPrimitives
    static void drawGPUPrimitives(
            RenderPass renderPass,
            int numVertices,
            int numInstances,
            int firstVertex,
            int firstInstance
    ) {
        SDL_DrawGPUPrimitives(renderPass.segment(), numVertices, numInstances, firstVertex, firstInstance);
    }

    /// @apiNote SDL_WaitAndAcquireGPUSwapchainTexture
    static @Nullable Texture waitAndAcquireGPUSwapchainTexture(
            CommandBuffer commandBuffer,
            Window window
    ) {
        try (var arena = Arena.ofConfined()) {
            var texturePointer = arena.allocate(ValueLayout.ADDRESS);
            var widthPointer = arena.allocate(ValueLayout.JAVA_INT);
            var heightPointer = arena.allocate(ValueLayout.JAVA_INT);
            check(SDL_WaitAndAcquireGPUSwapchainTexture(
                    commandBuffer.segment(),
                    window.segment(),
                    texturePointer,
                    widthPointer,
                    heightPointer
            ));
            var texture = texturePointer.get(ValueLayout.ADDRESS, 0);
            var width = widthPointer.get(ValueLayout.JAVA_INT, 0);
            var height = heightPointer.get(ValueLayout.JAVA_INT, 0);
            if (texture.address() == 0) {
                return null;
            } else {
                var swapchainTexture = Texture.newSwapchainTexture(texture, width, height);
                commandBuffer.acquiredSwapchainTexture = swapchainTexture;
                return swapchainTexture;
            }
        }
    }

    /// @apiNote SDL_BeginGPURenderPass
    static RenderPass beginGPURenderPass(
            CommandBuffer commandBuffer,
            RenderPass.ColorTargetInfo[] colorTargets,
            @Nullable RenderPass.DepthStencilTargetInfo depthStencilTarget
    ) {
        try (var arena = Arena.ofConfined()){
            var colorTargetCount = colorTargets.length;

            var colorSegments = SDL_GPUColorTargetInfo.allocateArray(colorTargetCount, arena);
            for (var i = 0; i < colorTargetCount; i++) {
                var colorTarget = colorTargets[i];
                colorTarget.put(SDL_GPUColorTargetInfo.asSlice(colorSegments, i));
            }

            MemorySegment depthStencilSegment;
            if (depthStencilTarget != null) {
                depthStencilSegment = SDL_GPUDepthStencilTargetInfo.allocate(arena);
                depthStencilTarget.put(depthStencilSegment);
            } else {
                depthStencilSegment = MemorySegment.NULL;
            }

            var handle = SDL_BeginGPURenderPass(
                    commandBuffer.segment(),
                    colorSegments,
                    colorTargetCount,
                    depthStencilSegment
            );
            return new RenderPass(assertNotNull(handle));
        }
    }


    /// @apiNote SDL_EndGPURenderPass
    static void endGPURenderPass(RenderPass renderPass) {
        SDL_EndGPURenderPass(renderPass.segment());
    }

    /// @apiNote SDL_CreateGPUBuffer
    static Buffer createGPUBuffer(
            ResourceSet resources,
            Device device,
            @BufferUsageFlags int usageFlags,
            long size
    ) {
        assertU32(size);

        try (var arena = Arena.ofConfined()) {
            var createInfo = SDL_GPUBufferCreateInfo.allocate(arena);
            SDL_GPUBufferCreateInfo.initialize(
                    createInfo,
                    usageFlags,
                    (int) size,
                    0
            );

            var bufferSegment = check(SDL_CreateGPUBuffer(
                    device.segment(),
                    createInfo
            ));
            return new Buffer(resources, device, bufferSegment, size);
        }
    }

    /// @apiNote SDL_CreateGPUTransferBuffer
    static TransferBuffer createGPUTransferBuffer(
            ResourceSet resources,
            Device device,
            TransferBufferUsage usage,
            long size
    ) {
        assertU32(size);

        try (var arena = Arena.ofConfined()) {
            var createInfo = SDL_GPUTransferBufferCreateInfo.allocate(arena);
            SDL_GPUTransferBufferCreateInfo.initialize(
                    createInfo,
                    usage.code(),
                    (int) size,
                    0
            );
            var transferBufferSegment = check(SDL_CreateGPUTransferBuffer(
                    device.segment(),
                    createInfo
            ));
            return new TransferBuffer(resources, device, transferBufferSegment, size);
        }
    }

    /// @apiNote SDL_ReleaseGPUBuffer
    static void releaseGPUBuffer(Device device, MemorySegment buffer) {
        SDL_ReleaseGPUBuffer(device.segment(), buffer);
    }

    /// @apiNote SDL_MapGPUTransferBuffer
    static MemorySegment mapGPUTransferBuffer(
            Device device,
            TransferBuffer transferBuffer,
            Cycle cycle
    ) {
        return check(SDL_MapGPUTransferBuffer(
                device.segment(),
                transferBuffer.segment(),
                cycle.value()
        ));
    }

    /// @apiNote SDL_UnmapGPUTransferBuffer
    static void unmapGPUTransferBuffer(Device device, TransferBuffer transferBuffer) {
        SDL_UnmapGPUTransferBuffer(device.segment(), transferBuffer.segment());
    }

    /// @apiNote SDL_ReleaseGPUTransferBuffer
    static void releaseGPUTransferBuffer(Device device, MemorySegment transferBuffer) {
        SDL_ReleaseGPUTransferBuffer(device.segment(), transferBuffer);
    }

    /// @apiNote SDL_BeginGPUCopyPass
    static CopyPass beginGPUCopyPass(CommandBuffer commandBuffer) {
        var copyPassSegment = assertNotNull(SDL_BeginGPUCopyPass(commandBuffer.segment()));
        return new CopyPass(copyPassSegment);
    }

    /// @apiNote SDL_EndGPUCopyPass
    static void endGPUCopyPass(CopyPass copyPass) {
        SDL_EndGPUCopyPass(copyPass.segment());
    }

    /// @apiNote SDL_UploadToGPUBuffer
    static void uploadToGPUBuffer(
            CopyPass copyPass,
            TransferBuffer transferBuffer,
            long srcOffset,
            Buffer dstBuffer,
            long dstOffset,
            long size,
            Cycle cycle
    ) {
        assertU32(srcOffset);
        assertU32(dstOffset);
        assertU32(size);

        try (var arena = Arena.ofConfined()) {
            var transferBufferLocation = SDL_GPUTransferBufferLocation.allocate(arena);
            SDL_GPUTransferBufferLocation.initialize(
                    transferBufferLocation,
                    transferBuffer.segment(),
                    (int) srcOffset
            );
            var bufferLocation = SDL_GPUBufferRegion.allocate(arena);
            SDL_GPUBufferRegion.initialize(
                    bufferLocation,
                    dstBuffer.segment(),
                    (int) dstOffset,
                    (int) size
            );

            SDL_UploadToGPUBuffer(
                    copyPass.segment(),
                    transferBufferLocation,
                    bufferLocation,
                    cycle.value()
            );
        }
    }


    /// @apiNote SDL_CreateGPUShader
    static <T extends Throwable> Shader createGPUShader(
            ResourceSet resources,
            Device device,
            Shader.Source<T> code,
            String entryPoint,
            @ShaderFormat int shaderFormat,
            ShaderStage shaderStage,
            int num_samplers,
            int num_storage_textures,
            int num_storage_buffers,
            int num_uniform_buffers
    ) throws T {
        try (var arena = Arena.ofConfined()) {
            var codeSegment = code.load(arena);
            var codeSize = codeSegment.byteSize();
            var createInfo = SDL_GPUShaderCreateInfo.allocate(arena);
            var entryPointSegment = arena.allocateFrom(entryPoint);

            SDL_GPUShaderCreateInfo.initialize(
                    createInfo,
                    codeSize,
                    codeSegment,
                    entryPointSegment,
                    shaderFormat,
                    shaderStage.code(),
                    num_samplers,
                    num_storage_textures,
                    num_storage_buffers,
                    num_uniform_buffers,
                    0
            );

            var shader = SDL_CreateGPUShader(device.segment(), createInfo);
            return new Shader(resources, device, check(shader));
        }
    }

    /// @apiNote SDL_ReleaseGPUShader
    static void releaseGPUShader(Device device, MemorySegment shader) {
        SDL_ReleaseGPUShader(device.segment(), shader);
    }

    static GraphicsPipeline createGPUGraphicsPipeline(
            ResourceSet resources,
            Device device,
            Shader vertexShader,
            Shader fragmentShader,
            PrimitiveType primitiveType,
            VertexInputState vertexInputState,
            RasterizerState rasterizerState,
            MultisampleState multisampleState,
            DepthStencilState depthStencilState,
            GraphicsPipelineTargetInfo targetInfo
    ) {
        try (var arena = Arena.ofConfined()) {
            var createInfo = SDL_GPUGraphicsPipelineCreateInfo.allocate(arena);

            var vertexInputStateSegment = SDL_GPUGraphicsPipelineCreateInfo.vertex_input_state(createInfo);
            var rasterizerStateSegment = SDL_GPUGraphicsPipelineCreateInfo.rasterizer_state(createInfo);
            var multisampleStateSegment = SDL_GPUGraphicsPipelineCreateInfo.multisample_state(createInfo);
            var depthStencilStateSegment = SDL_GPUGraphicsPipelineCreateInfo.depth_stencil_state(createInfo);
            var targetInfoSegment = SDL_GPUGraphicsPipelineCreateInfo.target_info(createInfo);

            vertexInputState.put(arena, vertexInputStateSegment);
            rasterizerState.put(rasterizerStateSegment);
            MultisampleState.SampleCount.put(multisampleStateSegment, multisampleState);
            depthStencilState.put(depthStencilStateSegment);
            targetInfo.put(arena, targetInfoSegment);

            SDL_GPUGraphicsPipelineCreateInfo.initialize(
                    createInfo,
                    vertexShader.segment(),
                    fragmentShader.segment(),
                    vertexInputStateSegment,
                    primitiveType.code(),
                    rasterizerStateSegment,
                    multisampleStateSegment,
                    depthStencilStateSegment,
                    targetInfoSegment,
                    0
            );

            var graphicsPipelineSegment = check(SDL_CreateGPUGraphicsPipeline(device.segment(), createInfo));
            return new GraphicsPipeline(resources, device, graphicsPipelineSegment);
        }
    }

    /// @apiNote SDL_GetGPUSwapchainTextureFormat
    static TextureFormat getGPUSwapchainTextureFormat(Device device, Window window) {
        var format = SDL_GetGPUSwapchainTextureFormat(device.segment(), window.segment());
        var textureFormat = TextureFormat.fromCode(format);
        if (textureFormat == null) {
            throw new IllegalStateException("Swapchain texture format " + format + " is not recognized");
        }
        return textureFormat;
    }

    /// @apiNote SDL_ReleaseGPUGraphicsPipeline
    static void releaseGPUGraphicsPipeline(Device device, MemorySegment graphicsPipeline) {
        SDL_ReleaseGPUGraphicsPipeline(device.segment(), graphicsPipeline);
    }

    /// @apiNote SDL_PushGPUFragmentUniformData
    static void pushGPUFragmentUniformData(
            CommandBuffer commandBuffer,
            int slot,
            CStruct struct
    ) {
        SDL_PushGPUFragmentUniformData(
                commandBuffer.segment(),
                slot,
                struct.memory(),
                (int) struct.size()
        );
    }

    /// @apiNote SDL_PushGPUComputeUniformData
    static void pushGPUVertexUniformData(
            CommandBuffer commandBuffer,
            int slot,
            CStruct struct
    ) {
        SDL_PushGPUVertexUniformData(
                commandBuffer.segment(),
                slot,
                struct.memory(),
                (int) struct.size()
        );
    }

    /// @apiNote SDL_PushGPUComputeUniformData
    static void pushGPUComputeUniformData(
            CommandBuffer commandBuffer,
            int slot,
            CStruct struct
    ) {
        SDL_PushGPUComputeUniformData(
                commandBuffer.segment(),
                slot,
                struct.memory(),
                (int) struct.size()
        );
    }


}
