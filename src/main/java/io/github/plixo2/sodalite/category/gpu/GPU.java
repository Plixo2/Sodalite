package io.github.plixo2.sodalite.category.gpu;

import io.github.plixo2.sodalite.category.pixels.PixelFormat;
import io.github.plixo2.sodalite.category.video.Window;
import io.github.plixo2.sodalite.memory.MemorySource;
import io.github.plixo2.sodalite.memory.WriteBuffer;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.jetbrains.annotations.Nullable;
import org.libsdl.sdl.*;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.ArrayList;
import java.util.List;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlCategory CategoryGPU
public final class GPU {
    private GPU() {}

    /// @sdlAPI SDL_CreateGPUDevice
    public static Device createDevice(
            ResourceSet resources,
            @ShaderFormat int shaderFormat,
            boolean debugMode,
            GPUDriver preferredDriver
    ) {
        try (var arena = Arena.ofConfined()) {
            var device = check(SDL_CreateGPUDevice(
                    shaderFormat,
                    debugMode,
                    preferredDriver.nameSegment(arena)
            ));
            return new Device(resources, device);
        }
    }

    public static List<GPUDriver> getDrivers() {
        var numDrivers = getNumDrivers();
        var list = new ArrayList<GPUDriver>();
        for (var i = 0; i < numDrivers; i++) {
            list.add(getDriver(i));
        }
        return list;
    }

    /// @sdlAPI SDL_GetNumGPUDrivers
    static int getNumDrivers() {
        return SDL_GetNumGPUDrivers();
    }

    /// @sdlAPI SDL_GetGPUDriver
    static GPUDriver getDriver(
            int index
    ) {
        var nameSegment = SDL_GetGPUDriver(index);
        if (nameSegment.address() == 0) {
            return GPUDriver.ofUnknown("unknown");
        }
        return GPUDriver.ofUnknown(nameSegment.getString(0));
    }

    /// @sdlAPI SDL_GetGPUShaderFormats
    static @ShaderFormat int getShaderFormats(
            Device device
    ) {
        //noinspection MagicConstant
        return SDL_GetGPUShaderFormats(device.segment());
    }

    /// @sdlAPI SDL_GetGPUDeviceDriver
    static GPUDriver getDeviceDriver(
            Device device
    ) {
        var nameSegment = SDL_GetGPUDeviceDriver(device.segment());
        if (nameSegment.address() == 0) {
            return GPUDriver.ofUnknown("unknown");
        }
        return GPUDriver.ofUnknown(nameSegment.getString(0));
    }

    /// @sdlAPI SDL_DestroyGPUDevice
    static void destroyGPUDevice(MemorySegment device) {
        SDL_DestroyGPUDevice(device);
    }

    /// @sdlAPI SDL_ClaimWindowForGPUDevice
    static void claimWindowForDevice(Device gpuDevice, Window window) {
        check(SDL_ClaimWindowForGPUDevice(gpuDevice.segment(), window.segment()));
    }

    /// @sdlAPI SDL_ReleaseWindowFromGPUDevice
    static void releaseWindowFromGPUDevice(Device gpuDevice, Window window) {
        SDL_ReleaseWindowFromGPUDevice(gpuDevice.segment(), window.segment());
    }


    /// @sdlAPI SDL_SetGPUSwapchainParameters
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

    /// @sdlAPI SDL_WindowSupportsGPUPresentMode
    static boolean windowSupportsPresentMode(
            Device gpuDevice,
            Window window,
            PresentMode presentMode
    ) {
        return SDL_WindowSupportsGPUPresentMode(gpuDevice.segment(), window.segment(), presentMode.code());
    }

    /// @sdlAPI SDL_WindowSupportsGPUSwapchainComposition
    static boolean windowSupportsSwapchainComposition(
            Device gpuDevice,
            Window window,
            SwapchainComposition swapchainComposition
    ) {
        return SDL_WindowSupportsGPUSwapchainComposition(gpuDevice.segment(), window.segment(), swapchainComposition.code());
    }


    /// @sdlAPI SDL_ReleaseGPUTexture
    static void releaseGPUTexture(Device device, MemorySegment texture) {
        SDL_ReleaseGPUTexture(device.segment(), texture);
    }

    /// @sdlAPI SDL_AcquireGPUCommandBuffer
    static CommandBuffer acquireGPUCommandBuffer(
            Device gpuDevice
    ) {
        var commandBuffer = check(SDL_AcquireGPUCommandBuffer(gpuDevice.segment()));
        return new CommandBuffer(
                gpuDevice,
                commandBuffer
        );
    }

    /// @sdlAPI SDL_SubmitGPUCommandBuffer
    static void submitGPUCommandBuffer(CommandBuffer commandBuffer) {
        check(SDL_SubmitGPUCommandBuffer(commandBuffer.segment()));
    }

    /// @sdlAPI SDL_CancelGPUCommandBuffer
    static void cancelGPUCommandBuffer(CommandBuffer commandBuffer) {
        check(SDL_CancelGPUCommandBuffer(commandBuffer.segment()));
    }

    /// @sdlAPI SDL_BindGPUGraphicsPipeline
    static void bindGPUGraphicsPipeline(RenderPass renderPass, GraphicsPipeline graphicsPipeline) {
        SDL_BindGPUGraphicsPipeline(renderPass.segment(), graphicsPipeline.segment());
    }

    /// @sdlAPI SDL_BindGPUVertexBuffers
    /// @sdlOther SDL_GPUBufferBinding
    static void bindVertexBuffer(
            RenderPass renderPass,
            int slot,
            Buffer buffer,
            int bufferOffset
    ) {
        try (var arena = Arena.ofConfined()) {
            var bufferBinding = SDL_GPUBufferBinding.create(
                    arena,
                    buffer.segment(),
                    bufferOffset
            );
            SDL_BindGPUVertexBuffers(renderPass.segment(), slot, bufferBinding, 1);
        }
    }

    /// @sdlAPI SDL_BindGPUVertexStorageBuffers
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

    /// @sdlAPI SDL_DrawGPUPrimitives
    static void drawGPUPrimitives(
            RenderPass renderPass,
            int numVertices,
            int numInstances,
            int firstVertex,
            int firstInstance
    ) {
        SDL_DrawGPUPrimitives(renderPass.segment(), numVertices, numInstances, firstVertex, firstInstance);
    }


    /// @sdlAPI SDL_DrawGPUIndexedPrimitives
    static void drawGPUIndexedPrimitives(
            RenderPass renderPass,
            int numIndices,
            int numInstances,
            int firstIndex,
            int vertexOffset,
            int firstInstance
    ) {
        SDL_DrawGPUIndexedPrimitives(renderPass.segment(), numIndices, numInstances, firstIndex, vertexOffset, firstInstance);
    }

    /// @sdlAPI SDL_DrawGPUPrimitivesIndirect
    static void drawGPUPrimitivesIndirect(
            RenderPass renderPass,
            Buffer indirectBuffer,
            long bufferOffset,
            int drawCount
    ) {
        SDL_DrawGPUPrimitivesIndirect(
                renderPass.segment(),
                indirectBuffer.segment(),
                assertU32(bufferOffset, "bufferOffset"),
                drawCount
        );
    }

    /// @sdlAPI SDL_DrawGPUIndexedPrimitivesIndirect
    static void drawGPUIndexedPrimitivesIndirect(
            RenderPass renderPass,
            Buffer indirectBuffer,
            long bufferOffset,
            int drawCount
    ) {
        SDL_DrawGPUIndexedPrimitivesIndirect(
                renderPass.segment(),
                indirectBuffer.segment(),
                assertU32(bufferOffset, "bufferOffset"),
                drawCount
        );
    }

    /// @sdlAPI SDL_SetGPUScissor
    static void setGPUScissor(
            RenderPass renderPass,
            int x, int y,
            int width, int height
    ) {
        try (var arena = Arena.ofConfined()) {
            var rect = SDL_Rect.allocate(arena);
            SDL_Rect.initialize(rect, x, y, width, height);
            SDL_SetGPUScissor(renderPass.segment(), rect);
        }
    }

    /// @sdlAPI SDL_WaitAndAcquireGPUSwapchainTexture
    static @Nullable Texture waitAndAcquireSwapchainTexture(
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
                return new CommandBuffer.SwapchainTexture(texture, width, height);
            }
        }
    }

    /// @sdlAPI SDL_AcquireGPUSwapchainTexture
    static @Nullable Texture acquireSwapchainTexture(
            CommandBuffer commandBuffer,
            Window window
    ) {
        try (var arena = Arena.ofConfined()) {
            var texturePointer = arena.allocate(ValueLayout.ADDRESS);
            var widthPointer = arena.allocate(ValueLayout.JAVA_INT);
            var heightPointer = arena.allocate(ValueLayout.JAVA_INT);
            check(SDL_AcquireGPUSwapchainTexture(
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
                return new CommandBuffer.SwapchainTexture(texture, width, height);
            }
        }
    }

    /// @sdlAPI SDL_BeginGPURenderPass
    static RenderPass beginRenderPass(
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


    /// @sdlAPI SDL_EndGPURenderPass
    static void endGPURenderPass(RenderPass renderPass) {
        SDL_EndGPURenderPass(renderPass.segment());
    }

    /// @sdlAPI SDL_CreateGPUBuffer
    /// @sdlOther SDL_GPUBufferCreateInfo
    static Buffer createBuffer(
            ResourceSet resources,
            Device device,
            @BufferUsageFlags int usageFlags,
            long size
    ) {

        try (var arena = Arena.ofConfined()) {
            var createInfo = SDL_GPUBufferCreateInfo.allocate(arena);
            SDL_GPUBufferCreateInfo.initialize(
                    createInfo,
                    usageFlags,
                    assertU32(size, "size"),
                    0
            );

            var bufferSegment = check(SDL_CreateGPUBuffer(
                    device.segment(),
                    createInfo
            ));
            return new Buffer(resources, device, bufferSegment, size);
        }
    }

    /// @sdlAPI SDL_CreateGPUTransferBuffer
    /// @sdlOther SDL_GPUTransferBufferCreateInfo
    static TransferBuffer createTransferBuffer(
            ResourceSet resources,
            Device device,
            TransferBufferUsage usage,
            long size
    ) {

        try (var arena = Arena.ofConfined()) {
            var createInfo = SDL_GPUTransferBufferCreateInfo.allocate(arena);
            SDL_GPUTransferBufferCreateInfo.initialize(
                    createInfo,
                    usage.code(),
                    assertU32(size, "size"),
                    0
            );
            var transferBufferSegment = check(SDL_CreateGPUTransferBuffer(
                    device.segment(),
                    createInfo
            ));
            return new TransferBuffer(resources, device, transferBufferSegment, size);
        }
    }

    /// @sdlAPI SDL_ReleaseGPUBuffer
    static void releaseBuffer(Device device, MemorySegment buffer) {
        SDL_ReleaseGPUBuffer(device.segment(), buffer);
    }

    /// @sdlAPI SDL_MapGPUTransferBuffer
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

    /// @sdlAPI SDL_UnmapGPUTransferBuffer
    static void unmapTransferBuffer(Device device, TransferBuffer transferBuffer) {
        SDL_UnmapGPUTransferBuffer(device.segment(), transferBuffer.segment());
    }

    /// @sdlAPI SDL_ReleaseGPUTransferBuffer
    static void releaseTransferBuffer(Device device, MemorySegment transferBuffer) {
        SDL_ReleaseGPUTransferBuffer(device.segment(), transferBuffer);
    }

    /// @sdlAPI SDL_BeginGPUCopyPass
    static CopyPass beginGPUCopyPass(CommandBuffer commandBuffer) {
        var copyPassSegment = assertNotNull(SDL_BeginGPUCopyPass(commandBuffer.segment()));
        return new CopyPass(copyPassSegment);
    }

    /// @sdlAPI SDL_EndGPUCopyPass
    static void endGPUCopyPass(CopyPass copyPass) {
        SDL_EndGPUCopyPass(copyPass.segment());
    }


    /// @sdlAPI SDL_UploadToGPUTexture
    /// @sdlOther SDL_GPUTextureTransferInfo
    static void uploadToTexture(
            CopyPass copyPass,
            TransferBuffer sourceBuffer,
            long sourceOffset,
            TextureRegion destination,
            Cycle cycle
    ) {
        try (var arena = Arena.ofConfined()) {
            var transferInfoSegment = SDL_GPUTextureTransferInfo.allocate(arena);
            SDL_GPUTextureTransferInfo.initialize(
                    transferInfoSegment,
                    sourceBuffer.segment(),
                    assertU32(sourceOffset, "sourceOffset"),
                    0, // dont care about these two
                    0
            );
            var regionSegment = destination.create(arena);

            SDL_UploadToGPUTexture(
                    copyPass.segment(),
                    transferInfoSegment,
                    regionSegment,
                    cycle.value()
            );
        }
    }

    /// @sdlAPI SDL_UploadToGPUBuffer
    /// @sdlOther SDL_GPUTransferBufferLocation
    /// @sdlOther SDL_GPUBufferRegion
    static void uploadToBuffer(
            CopyPass copyPass,
            TransferBuffer sourceBuffer,
            long sourceOffset,
            Buffer destinationBuffer,
            long destinationOffset,
            long size,
            Cycle cycle
    ) {
        try (var arena = Arena.ofConfined()) {
            var transferBufferLocation = SDL_GPUTransferBufferLocation.create(
                    arena,
                    sourceBuffer.segment(),
                    assertU32(sourceOffset, "sourceOffset")
            );
            var bufferLocation = SDL_GPUBufferRegion.create(
                    arena,
                    destinationBuffer.segment(),
                    assertU32(destinationOffset, "destinationOffset"),
                    assertU32(size, "size")
            );

            SDL_UploadToGPUBuffer(
                    copyPass.segment(),
                    transferBufferLocation,
                    bufferLocation,
                    cycle.value()
            );
        }
    }


    /// @sdlAPI SDL_CreateGPUShader
    /// @sdlOther SDL_GPUShaderCreateInfo
    static <T extends Exception> Shader createGPUShader(
            ResourceSet resources,
            Device device,
            MemorySource<T> code,
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

    /// @sdlAPI SDL_ReleaseGPUShader
    static void releaseGPUShader(Device device, MemorySegment shader) {
        SDL_ReleaseGPUShader(device.segment(), shader);
    }

    /// @sdlAPI SDL_CreateGPUGraphicsPipeline
    /// @sdlOther SDL_GPUGraphicsPipelineCreateInfo
    static GraphicsPipeline createGraphicsPipeline(
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
            multisampleState.put(multisampleStateSegment);
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

    /// @sdlAPI SDL_GetGPUSwapchainTextureFormat
    static TextureFormat getSwapchainTextureFormat(Device device, Window window) {
        var format = SDL_GetGPUSwapchainTextureFormat(device.segment(), window.segment());
        return TextureFormat.fromCode(format);
    }

    /// @sdlAPI SDL_ReleaseGPUGraphicsPipeline
    static void releaseGraphicsPipeline(Device device, MemorySegment graphicsPipeline) {
        SDL_ReleaseGPUGraphicsPipeline(device.segment(), graphicsPipeline);
    }

    /// @sdlAPI SDL_PushGPUFragmentUniformData
    static void pushGPUFragmentUniformData(
            CommandBuffer commandBuffer,
            int slot,
            WriteBuffer<?> data
    ) {
        var memory = data.memory();
        SDL_PushGPUFragmentUniformData(
                commandBuffer.segment(),
                slot,
                memory,
                (int) memory.byteSize()
        );
    }

    /// @sdlAPI SDL_PushGPUVertexUniformData
    static void pushGPUVertexUniformData(
            CommandBuffer commandBuffer,
            int slot,
            WriteBuffer<?> data
    ) {
        var memory = data.memory();
        SDL_PushGPUVertexUniformData(
                commandBuffer.segment(),
                slot,
                memory,
                (int) memory.byteSize()
        );
    }

    /// @sdlAPI SDL_PushGPUComputeUniformData
    static void pushGPUComputeUniformData(
            CommandBuffer commandBuffer,
            int slot,
            WriteBuffer<?> data
    ) {
        var memory = data.memory();
        SDL_PushGPUComputeUniformData(
                commandBuffer.segment(),
                slot,
                memory,
                (int) memory.byteSize()
        );
    }


    /// @sdlAPI SDL_CreateGPUTexture
    static Texture createTexture(
            ResourceSet resources,
            Device device,
            TextureInfo createInfo
    ) {
        try (var arena = Arena.ofConfined()) {
            var createInfoSegment = SDL_GPUTextureCreateInfo.allocate(arena);
            TextureCreateInfo.put(createInfoSegment, createInfo);
            var textureSegment = check(SDL_CreateGPUTexture(device.segment(), createInfoSegment));
            var texture = new Texture(resources, device, textureSegment, createInfo);

            if (texture.name() != null) {
                setTextureName(device, texture, texture.name());
            }

            return texture;
        }
    }


    /// @sdlAPI SDL_CreateGPUSampler
    static Sampler createGPUSampler(
            ResourceSet resources,
            Device device,
            SamplerInfo samplerInfo
    ) {
        try (var arena = Arena.ofConfined()) {
            var samplerInfoSegment = SDL_GPUSamplerCreateInfo.allocate(arena);
            SamplerCreateInfo.put(samplerInfoSegment, samplerInfo);
            var samplerSegment = check(SDL_CreateGPUSampler(device.segment(), samplerInfoSegment));
            return new Sampler(resources, device, samplerSegment, samplerInfo);
        }
    }

    /// @sdlAPI SDL_ReleaseGPUSampler
    static void releaseGPUSampler(Device device, MemorySegment sampler) {
        SDL_ReleaseGPUSampler(device.segment(), sampler);
    }

    /// @sdlAPI SDL_BindGPUFragmentSamplers
    /// @sdlOther SDL_GPUTextureSamplerBinding
    static void bindFragmentSampler(
            RenderPass renderPass,
            int slot,
            Texture texture,
            Sampler sampler
    ) {
        try (var arena = Arena.ofConfined()) {
            var bindingSegment = SDL_GPUTextureSamplerBinding.allocate(arena);
            SDL_GPUTextureSamplerBinding.initialize(
                    bindingSegment,
                    texture.segment(),
                    sampler.segment()
            );
            SDL_BindGPUFragmentSamplers(
                    renderPass.segment(),
                    slot,
                    bindingSegment,
                    1
            );
        }
    }

    /// @sdlAPI SDL_BindGPUFragmentSamplers
    /// @sdlOther SDL_GPUTextureSamplerBinding
    static void bindFragmentSamplers(
            RenderPass renderPass,
            int firstSlot,
            Texture[] textures,
            Sampler[] samplers
    ) {
        var numBindings = textures.length;
        if (numBindings != samplers.length) {
            throw new IllegalArgumentException("Texture and sampler arrays must have the same length");
        }

        try (var arena = Arena.ofConfined()) {
            var bindingSegment = SDL_GPUTextureSamplerBinding.allocateArray(
                    numBindings,
                    arena
            );
            for (var i = 0; i < numBindings; i++) {
                var textureSegment = textures[i].segment();
                var samplerSegment = samplers[i].segment();
                SDL_GPUTextureSamplerBinding.initialize(
                        SDL_GPUTextureSamplerBinding.asSlice(bindingSegment, i),
                        textureSegment,
                        samplerSegment
                );
            }
            SDL_BindGPUFragmentSamplers(
                    renderPass.segment(),
                    firstSlot,
                    bindingSegment,
                    numBindings
            );
        }
    }

    /// @sdlAPI SDL_BindGPUFragmentSamplers
    /// @sdlOther SDL_GPUTextureSamplerBinding
    static void bindFragmentSamplers(
            RenderPass renderPass,
            int firstSlot,
            Texture[] texture,
            Sampler samplers
    ) {
        var numBindings = texture.length;

        try (var arena = Arena.ofConfined()) {
            var bindingSegment = SDL_GPUTextureSamplerBinding.allocateArray(
                    numBindings,
                    arena
            );
            for (var i = 0; i < numBindings; i++) {
                var textureSegment = texture[i].segment();
                var samplerSegment = samplers.segment();
                SDL_GPUTextureSamplerBinding.initialize(
                        SDL_GPUTextureSamplerBinding.asSlice(bindingSegment, i),
                        textureSegment,
                        samplerSegment
                );
            }
            SDL_BindGPUFragmentSamplers(
                    renderPass.segment(),
                    firstSlot,
                    bindingSegment,
                    numBindings
            );
        }
    }

    /// @sdlAPI SDL_SetGPUViewport
    static void setGPUViewport(
        RenderPass renderPass,
        Viewport viewport
    ) {
        try (var arena = Arena.ofConfined()) {
            var viewportSegment = SDL_GPUViewport.allocate(arena);
            viewport.put(viewportSegment);
            SDL_SetGPUViewport(renderPass.segment(), viewportSegment);
        }
    }

    /// @sdlAPI SDL_ReleaseGPUComputePipeline
    static void releaseComputePipeline(Device device, MemorySegment graphicsPipeline) {
        SDL_ReleaseGPUComputePipeline(device.segment(), graphicsPipeline);
    }

    /// @sdlAPI SDL_CreateGPUComputePipeline
    /// @sdlOther SDL_GPUComputePipelineCreateInfo
    static <T extends Exception> ComputePipeline createComputePipeline(
            ResourceSet resources,
            Device device,
            ComputeShader.Creator<T> creator
    ) throws T {
        try (var arena = Arena.ofConfined()) {
            var format = creator.shaderFormat();
            var source = creator.source();
            var threadCount = creator.threadCount();
            var parameter = creator.parameter();

            var codeSegment = source.load(arena);
            var codeSize = codeSegment.byteSize();
            var entryPointSegment = arena.allocateFrom(parameter.entryPoint());

            var createInfoSegment = SDL_GPUComputePipelineCreateInfo.allocate(arena);


            SDL_GPUComputePipelineCreateInfo.initialize(
                    createInfoSegment,
                    codeSize,
                    codeSegment,
                    entryPointSegment,
                    format,
                    parameter.samplers(),
                    parameter.readonlyStorageTextures(),
                    parameter.readonlyStorageBuffers(),
                    parameter.readwriteStorageTextures(),
                    parameter.readwriteStorageBuffers(),
                    parameter.nniformBuffers(),
                    threadCount.x(),
                    threadCount.y(),
                    threadCount.z(),
                    0
            );

            var segment = check(SDL_CreateGPUComputePipeline(device.segment(), createInfoSegment));

            return new ComputePipeline(resources, device, segment);
        }
    }

    /// @sdlAPI SDL_BeginGPUComputePass
    static ComputePass beginComputePass(
            CommandBuffer commandBuffer,
            ComputePass.Binding[] bindings
    ) {
        try (var arena = Arena.ofConfined()) {
            int numTextureBindings = 0;
            int numBufferBindings = 0;
            for (var binding : bindings) {
                switch (binding) {
                    case ComputePass.TextureBinding ignored -> numTextureBindings++;
                    case ComputePass.BufferBinding ignored -> numBufferBindings++;
                }
            }

            var textureBindingSegment = SDL_GPUStorageTextureReadWriteBinding.allocateArray(numTextureBindings, arena);
            var bufferBindingSegment = SDL_GPUStorageBufferReadWriteBinding.allocateArray(numBufferBindings, arena);

            var textureIndex = 0;
            var bufferIndex = 0;
            for (var binding : bindings) {
                switch (binding) {
                    case ComputePass.TextureBinding textureBinding -> {
                        textureBinding.put(
                                SDL_GPUStorageTextureReadWriteBinding.asSlice(
                                        textureBindingSegment,
                                        textureIndex++
                                )
                        );
                    }
                    case ComputePass.BufferBinding bufferBinding -> {
                        bufferBinding.put(
                                SDL_GPUBufferBinding.asSlice(
                                        bufferBindingSegment,
                                        bufferIndex++
                                )
                        );
                    }
                }
            }

            var computePassSegment = SDL_BeginGPUComputePass(
                    commandBuffer.segment(),
                    textureBindingSegment,
                    numTextureBindings,
                    bufferBindingSegment,
                    numBufferBindings
            );
            return new ComputePass(computePassSegment);
        }
    }

    /// @sdlAPI SDL_EndGPUComputePass
    static void endComputePass(
            ComputePass computePass
    ) {
        SDL_EndGPUComputePass(computePass.segment());
    }

    /// @sdlAPI SDL_BindGPUComputePipeline
    static void bindComputePipeline(
            ComputePass computePass,
            ComputePipeline computePipeline
    ) {
        SDL_BindGPUComputePipeline(computePass.segment(), computePipeline.segment());
    }

    /// @sdlAPI SDL_DispatchGPUCompute
    static void dispatchCompute(
            ComputePass computePass,
            long groupCountX, long groupCountY, long groupCountZ
    ) {
        SDL_DispatchGPUCompute(
                computePass.segment(),
                assertU32(groupCountX, "groupCountX"),
                assertU32(groupCountY, "groupCountY"),
                assertU32(groupCountZ, "groupCountZ")
        );
    }

    /// @sdlAPI SDL_DispatchGPUComputeIndirect
    static void dispatchComputeIndirect(
            ComputePass computePass,
            Buffer buffer,
            long offset
    ) {
        SDL_DispatchGPUComputeIndirect(
                computePass.segment(),
                buffer.segment(),
                assertU32(offset, "offset")
        );
    }

    /// @sdlAPI SDL_BindGPUComputeSamplers
    /// @sdlOther SDL_GPUTextureSamplerBinding
    static void bindComputeSampler(
            ComputePass computePass,
            int slot,
            Texture texture,
            Sampler sampler
    ) {
        try (var arena = Arena.ofConfined()) {
            var bindingSegment = SDL_GPUTextureSamplerBinding.allocate(arena);
            SDL_GPUTextureSamplerBinding.initialize(
                    bindingSegment,
                    texture.segment(),
                    sampler.segment()
            );
            SDL_BindGPUComputeSamplers(
                    computePass.segment(),
                    slot,
                    bindingSegment,
                    1
            );
        }
    }

    //// @sdlAPI SDL_BindGPUComputeSamplers
    /// @sdlOther SDL_GPUTextureSamplerBinding
    static void bindComputeSamplers(
            ComputePass computePass,
            int firstSlot,
            Texture[] textures,
            Sampler[] samplers
    ) {
        var numBindings = textures.length;
        if (numBindings != samplers.length) {
            throw new IllegalArgumentException("Texture and sampler arrays must have the same length");
        }

        try (var arena = Arena.ofConfined()) {
            var bindingSegment = SDL_GPUTextureSamplerBinding.allocateArray(
                    numBindings,
                    arena
            );
            for (var i = 0; i < numBindings; i++) {
                var textureSegment = textures[i].segment();
                var samplerSegment = samplers[i].segment();
                SDL_GPUTextureSamplerBinding.initialize(
                        SDL_GPUTextureSamplerBinding.asSlice(bindingSegment, i),
                        textureSegment,
                        samplerSegment
                );
            }
            SDL_BindGPUComputeSamplers(
                    computePass.segment(),
                    firstSlot,
                    bindingSegment,
                    numBindings
            );
        }
    }

    /// @sdlAPI SDL_BindGPUComputeSamplers
    /// @sdlOther SDL_GPUTextureSamplerBinding
    static void bindComputeSamplers(
            ComputePass computePass,
            int firstSlot,
            Texture[] texture,
            Sampler samplers
    ) {
        var numBindings = texture.length;

        try (var arena = Arena.ofConfined()) {
            var bindingSegment = SDL_GPUTextureSamplerBinding.allocateArray(
                    numBindings,
                    arena
            );
            for (var i = 0; i < numBindings; i++) {
                var textureSegment = texture[i].segment();
                var samplerSegment = samplers.segment();
                SDL_GPUTextureSamplerBinding.initialize(
                        SDL_GPUTextureSamplerBinding.asSlice(bindingSegment, i),
                        textureSegment,
                        samplerSegment
                );
            }
            SDL_BindGPUComputeSamplers(
                    computePass.segment(),
                    firstSlot,
                    bindingSegment,
                    numBindings
            );
        }
    }

    /// @sdlAPI SDL_BindGPUComputeStorageBuffers
    static void bindGPUComputeStorageBuffer(
            ComputePass computePass,
            int slot,
            Buffer buffer
    ) {
        try (var arena = Arena.ofConfined()) {
            var pointerBuffer = arena.allocate(ValueLayout.ADDRESS);
            pointerBuffer.set(ValueLayout.ADDRESS, 0, buffer.segment());
            SDL_BindGPUComputeStorageBuffers(computePass.segment(), slot, pointerBuffer, 1);
        }
    }

    /// @sdlAPI SDL_BindGPUComputeStorageTextures
    static void bindGPUComputeStorageTexture(
            ComputePass computePass,
            int slot,
            Texture texture
    ) {
        try (var arena = Arena.ofConfined()) {
            var pointerBuffer = arena.allocate(ValueLayout.ADDRESS);
            pointerBuffer.set(ValueLayout.ADDRESS, 0, texture.segment());
            SDL_BindGPUComputeStorageTextures(computePass.segment(), slot, pointerBuffer, 1);
        }
    }

    /// @sdlAPI SDL_BindGPUFragmentStorageBuffers
    static void bindFragmentStorageBuffer(
            RenderPass renderPass,
            int slot,
            Buffer buffer
    ) {
        try (var arena = Arena.ofConfined()) {
            var pointerBuffer = arena.allocate(ValueLayout.ADDRESS);
            pointerBuffer.set(ValueLayout.ADDRESS, 0, buffer.segment());
            SDL_BindGPUFragmentSamplers(
                    renderPass.segment(),
                    slot,
                    pointerBuffer,
                    1
            );
        }
    }

    /// @sdlAPI SDL_BindGPUFragmentStorageTextures
    static void bindFragmentStorageTexture(
            RenderPass renderPass,
            int slot,
            Texture texture
    ) {
        try (var arena = Arena.ofConfined()) {
            var pointerBuffer = arena.allocate(ValueLayout.ADDRESS);
            pointerBuffer.set(ValueLayout.ADDRESS, 0, texture.segment());
            SDL_BindGPUFragmentStorageTextures(
                    renderPass.segment(),
                    slot,
                    pointerBuffer,
                    1
            );
        }
    }

    /// @sdlAPI SDL_BindGPUVertexSamplers
    /// @sdlOther SDL_GPUTextureSamplerBinding
    static void bindVertexSampler(
            RenderPass renderPass,
            int slot,
            Texture texture,
            Sampler sampler
    ) {
        try (var arena = Arena.ofConfined()) {
            var bindingSegment = SDL_GPUTextureSamplerBinding.allocate(arena);
            SDL_GPUTextureSamplerBinding.initialize(
                    bindingSegment,
                    texture.segment(),
                    sampler.segment()
            );
            SDL_BindGPUVertexSamplers(
                    renderPass.segment(),
                    slot,
                    bindingSegment,
                    1
            );
        }
    }

    /// @sdlAPI SDL_BindGPUVertexStorageTextures
    static void bindVertexStorageTexture(
            RenderPass renderPass,
            int slot,
            Texture texture
    ) {
        try (var arena = Arena.ofConfined()) {
            var pointerBuffer = arena.allocate(ValueLayout.ADDRESS);
            pointerBuffer.set(ValueLayout.ADDRESS, 0, texture.segment());
            SDL_BindGPUVertexStorageTextures(
                    renderPass.segment(),
                    slot,
                    pointerBuffer,
                    1
            );
        }
    }

    /// @sdlAPI SDL_BindGPUIndexBuffer
    /// @sdlOther SDL_GPUBufferBinding
    static void bindIndexBuffer(
            RenderPass renderPass,
            Buffer buffer,
            int bufferOffset,
            IndexElementSize indexElementSize
    ) {
        try (var arena = Arena.ofConfined()) {
            var bufferBinding = SDL_GPUBufferBinding.allocate(arena);
            SDL_GPUBufferBinding.initialize(
                    bufferBinding,
                    buffer.segment(),
                    bufferOffset
            );
            SDL_BindGPUIndexBuffer(
                    renderPass.segment(),
                    bufferBinding,
                    indexElementSize.code()
            );
        }
    }

    /// @sdlAPI SDL_BlitGPUTexture
    static void blitTexture(
            CommandBuffer commandBuffer,
            BlitInfo blitInfo
    ) {
        try (var arena = Arena.ofConfined()) {
            var blitSegment = SDL_GPUBlitInfo.allocate(arena);
            blitInfo.put(blitSegment);
            SDL_BlitGPUTexture(
                    commandBuffer.segment(),
                    blitSegment
            );
        }
    }

    /// @sdlAPI SDL_CalculateGPUTextureFormatSize
    static long calculateTextureSize(
            TextureFormat format,
            long width,
            long height,
            long depthOrLayerCount
    ) {
        int u32 = SDL_CalculateGPUTextureFormatSize(
                format.code(),
                assertU32(width, "width"),
                assertU32(height, "height"),
                assertU32(depthOrLayerCount, "depthOrLayerCount")
        );

        return Integer.toUnsignedLong(u32);
    }

    /// @sdlAPI SDL_CopyGPUBufferToBuffer
    /// @sdlOther SDL_GPUBufferLocation
    static void copyBuffer(
            CopyPass copyPass,
            Buffer source,
            long sourceOffset,
            Buffer destination,
            long destinationOffset,
            long size,
            Cycle cycle
    ) {
        try (var arena = Arena.ofConfined()) {
            var srcSegment = SDL_GPUBufferLocation.allocate(arena);
            var destSegment = SDL_GPUBufferLocation.allocate(arena);
            SDL_GPUBufferLocation.initialize(
                    srcSegment,
                    source.segment(),
                    assertU32(sourceOffset, "sourceOffset")
            );
            SDL_GPUBufferLocation.initialize(
                    destSegment,
                    destination.segment(),
                    assertU32(destinationOffset, "sourceOffset")
            );
            SDL_CopyGPUBufferToBuffer(
                    copyPass.segment(),
                    srcSegment,
                    destSegment,
                    assertU32(size, "size"),
                    cycle.value()
            );
        }
    }

    /// @sdlAPI SDL_CopyGPUTextureToTexture
    static void copyTexture(
            CopyPass copyPass,
            TextureLocation source,
            TextureLocation destination,
            long width,
            long height,
            long depth,
            Cycle cycle
    ) {
        try (var arena = Arena.ofConfined()) {
            var srcSegment = SDL_GPUTextureLocation.allocate(arena);
            var destSegment = SDL_GPUTextureLocation.allocate(arena);
            source.put(srcSegment);
            destination.put(destSegment);
            SDL_CopyGPUTextureToTexture(
                    copyPass.segment(),
                    srcSegment,
                    destSegment,
                    assertU32(width, "width"),
                    assertU32(height, "height"),
                    assertU32(depth, "depth"),
                    cycle.value()
            );
        }
    }

    /// @sdlAPI SDL_ReleaseGPUFence
    static void releaseFence(
            Device device,
            MemorySegment segment
    ) {
        SDL_ReleaseGPUFence(
                device.segment(),
                segment
        );
    }

    /// @sdlAPI SDL_QueryGPUFence
    static boolean queryFence(
            Device device,
            Fence fence
    ) {
        return SDL_QueryGPUFence(
                device.segment(),
                fence.segment()
        );
    }

    /// @sdlAPI SDL_WaitForGPUFences
    static void waitForFences(
            Device device,
            boolean waitAll,
            Fence[] fence
    ) {
        try (var arena = Arena.ofConfined()) {
            var pointerBuffer = arena.allocate(ValueLayout.ADDRESS, fence.length);
            for (var i = 0; i < fence.length; i++) {
                pointerBuffer.set(ValueLayout.ADDRESS, i, fence[i].segment());
            }
            check(SDL_WaitForGPUFences(
                    device.segment(),
                    waitAll,
                    pointerBuffer,
                    fence.length
            ));
        }
    }

    /// @sdlAPI SDL_WaitForGPUFences
    static void waitForFence(
            Device device,
            Fence fence
    ) {
        try (var arena = Arena.ofConfined()) {
            var pointerBuffer = arena.allocate(ValueLayout.ADDRESS);
            pointerBuffer.set(ValueLayout.ADDRESS, 0, fence.segment());
            check(SDL_WaitForGPUFences(
                    device.segment(),
                    true,
                    pointerBuffer,
                    1
            ));
        }
    }

    /// @sdlAPI SDL_SubmitGPUCommandBufferAndAcquireFence
    static Fence submitCommandBufferAndAcquire(
            ResourceSet resources,
            Device device,
            CommandBuffer commandBuffer
    ) {
        var fence = check(SDL_SubmitGPUCommandBufferAndAcquireFence(
                commandBuffer.segment()
        ));
        return new Fence(resources, device, fence);
    }

    /// @sdlAPI SDL_DownloadFromGPUTexture
    /// @sdlOther SDL_GPUTextureTransferInfo
    static void downloadFromTexture(
            CopyPass copyPass,
            TransferBuffer destinationBuffer,
            long destinationOffset,
            TextureRegion source
    ) {
        try (var arena = Arena.ofConfined()) {
            var regionSegment = source.create(arena);
            var transferInfoSegment = SDL_GPUTextureTransferInfo.create(
                    arena,
                    destinationBuffer.segment(),
                    assertU32(destinationOffset, "destinationOffset"),
                    0, // dont care about these two
                    0
            );

            SDL_DownloadFromGPUTexture(
                    copyPass.segment(),
                    regionSegment,
                    transferInfoSegment
            );
        }
    }

    /// @sdlAPI SDL_DownloadFromGPUBuffer
    /// @sdlOther SDL_GPUTransferBufferLocation
    /// @sdlOther SDL_GPUBufferRegion
    static void downloadFromBuffer(
            CopyPass copyPass,
            TransferBuffer destinationBuffer,
            long destinationOffset,
            Buffer sourceBuffer,
            long sourceOffset,
            long size
    ) {
        try (var arena = Arena.ofConfined()) {
            var bufferLocation = SDL_GPUBufferRegion.create(
                    arena,
                    sourceBuffer.segment(),
                    assertU32(sourceOffset, "sourceOffset"),
                    assertU32(size, "size")
            );

            var transferBufferLocation = SDL_GPUTransferBufferLocation.create(
                    arena,
                    destinationBuffer.segment(),
                    assertU32(destinationOffset, "destinationOffset")
            );

            SDL_DownloadFromGPUBuffer(
                    copyPass.segment(),
                    bufferLocation,
                    transferBufferLocation
            );
        }
    }


    /// @sdlAPI SDL_GenerateMipmapsForGPUTexture
    static void generateMipmaps(
            CommandBuffer commandBuffer,
            Texture texture
    ) {
        SDL_GenerateMipmapsForGPUTexture(
                commandBuffer.segment(),
                texture.segment()
        );
    }

    /// @sdlAPI SDL_GetGPUTextureFormatFromPixelFormat
    static TextureFormat textureFormatFromPixelFormat(PixelFormat format) {
        var code = SDL_GetGPUTextureFormatFromPixelFormat(format.code());
        return TextureFormat.fromCode(code);
    }

    /// @sdlAPI SDL_GetPixelFormatFromGPUTextureFormat
    static PixelFormat pixelFormatFromTextureFormat(TextureFormat format) {
        var code = SDL_GetPixelFormatFromGPUTextureFormat(format.code());
        return PixelFormat.fromCode(code);
    }

    /// @sdlAPI SDL_GPUSupportsShaderFormats
    static boolean supportsShaderFormats(
            @ShaderFormat int shaderFormats,
            GPUDriver driver
    ) {
        try (var arena = Arena.ofConfined()) {
            return SDL_GPUSupportsShaderFormats(
                    shaderFormats,
                    driver.nameSegment(arena)
            );
        }

    }

    /// @sdlAPI SDL_GPUTextureFormatTexelBlockSize
    static long textureFormatTexelBlockSize(
            TextureFormat format
    ) {
        int u32 = SDL_GPUTextureFormatTexelBlockSize(format.code());
        return Integer.toUnsignedLong(u32);
    }

    /// @sdlAPI SDL_GPUTextureSupportsFormat
    static boolean textureSupportsFormat(
            Device device,
            TextureFormat format,
            TextureType type,
            @TextureUsageFlags int usageFlags
    ) {
        return SDL_GPUTextureSupportsFormat(
                device.segment(),
                format.code(),
                type.code(),
                usageFlags
        );
    }

    /// @sdlAPI SDL_GPUTextureSupportsSampleCount
    static boolean textureSupportsSampleCount(
            Device device,
            TextureFormat format,
            SampleCount sampleCount
    ) {
        return SDL_GPUTextureSupportsSampleCount(
                device.segment(),
                format.code(),
                sampleCount.code()
        );
    }

    /// @sdlAPI SDL_InsertGPUDebugLabel
    static void insertDebugLabel(
            CommandBuffer commandBuffer,
            String label
    ) {
        try (var arena = Arena.ofConfined()) {
            var labelSegment = arena.allocateFrom(label);
            SDL_InsertGPUDebugLabel(commandBuffer.segment(), labelSegment);
        }
    }

    /// @sdlAPI SDL_PushGPUDebugGroup
    static void pushDebugGroup(
            CommandBuffer commandBuffer,
            String label
    ) {
        try (var arena = Arena.ofConfined()) {
            var labelSegment = arena.allocateFrom(label);
            SDL_PushGPUDebugGroup(commandBuffer.segment(), labelSegment);
        }
    }

    /// @sdlAPI SDL_PopGPUDebugGroup
    static void popDebugGroup(
            CommandBuffer commandBuffer
    ) {
        SDL_PopGPUDebugGroup(commandBuffer.segment());
    }

    /// @sdlAPI SDL_SetGPUTextureName
    static void setTextureName(
            Device device,
            Texture texture,
            String name
    ) {
        try (var arena = Arena.ofConfined()) {
            var nameSegment = arena.allocateFrom(name);
            SDL_SetGPUTextureName(
                    device.segment(),
                    texture.segment(),
                    nameSegment
            );
        }
    }

    /// @sdlAPI SDL_SetGPUStencilReference
    static void setStencilReference(
            RenderPass renderPass,
            byte reference
    ) {
        SDL_SetGPUStencilReference(
                renderPass.segment(),
                reference
        );
    }

    /// @sdlAPI SDL_SetGPUBufferName
    static void setBufferName(
            Device device,
            Buffer buffer,
            String name
    ) {
        try (var arena = Arena.ofConfined()) {
            var nameSegment = arena.allocateFrom(name);
            SDL_SetGPUBufferName(
                    device.segment(),
                    buffer.segment(),
                    nameSegment
            );
        }
    }

    /// @sdlAPI SDL_SetGPUBlendConstants
    /// @sdlOther SDL_FColor
    static void setBlendConstants(
            CommandBuffer commandBuffer,
            float red, float green, float blue, float alpha
    ) {
        try (var arena = Arena.ofConfined()) {
            var colorSegment = SDL_FColor.allocate(arena);
            SDL_FColor.initialize(colorSegment, red, green, blue, alpha);
            SDL_SetGPUBlendConstants(commandBuffer.segment(), colorSegment);
        }
    }

    /// @sdlAPI SDL_SetGPUAllowedFramesInFlight
    static void setAllowedFramesInFlight(
            Device device,
            int allowedFrames
    ) {
        if (allowedFrames < 1 || allowedFrames > 3) {
            throw new IllegalArgumentException(
                    "The minimum value of allowed frames in flight is 1, and the maximum is 3."
            );
        }

        check(SDL_SetGPUAllowedFramesInFlight(
                device.segment(),
                allowedFrames
        ));
    }

    /// @sdlAPI SDL_WaitForGPUIdle
    static void waitForIdle(
            Device device
    ) {
        check(SDL_WaitForGPUIdle(device.segment()));
    }

    /// @sdlAPI SDL_WaitForGPUSwapchain
    static void waitForSwapchain(
            Device device,
            Window window
    ) {
        check(SDL_WaitForGPUSwapchain(device.segment(), window.segment()));
    }

}
