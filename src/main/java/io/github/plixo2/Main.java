

import io.github.plixo2.sodalite.category.timer.Timer;
import io.github.plixo2.sodalite.memory.CStruct;
import io.github.plixo2.sodalite.memory.GrowableWriteBuffer;
import io.github.plixo2.sodalite.memory.Layouts;
import io.github.plixo2.sodalite.memory.WriteBuffer;
import io.github.plixo2.sodalite.resource.ResourceSet;
import io.github.plixo2.sodalite.category.events.EventOld;
import io.github.plixo2.sodalite.category.events.Events;
import io.github.plixo2.sodalite.category.gpu.*;
import io.github.plixo2.sodalite.category.init.Init;
import io.github.plixo2.sodalite.category.log.Log;
import io.github.plixo2.sodalite.category.log.LogCategory;
import io.github.plixo2.sodalite.category.log.LogPriority;
import io.github.plixo2.sodalite.category.video.Video;
import io.github.plixo2.sodalite.category.video.WindowFlags;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.libsdl.sdl.SDL3_h;


import java.io.IOException;
import java.lang.foreign.*;
import java.nio.file.Path;

boolean running = true;

/*
struct Vertex
{
    float x, y, z;      //vec3 position
    float r, g, b, a;   //vec4 color
};
*/

StructLayout vertexLayout = MemoryLayout.structLayout(
    MemoryLayout.sequenceLayout(3, SDL3_h.C_FLOAT).withName("position"),
    MemoryLayout.sequenceLayout(4, SDL3_h.C_FLOAT).withName("color")
);


void writeVertex(
        MemorySegment vertices,
        int index,
        float[] values
) {
    var size = this.vertexLayout.byteSize();
    var slice = vertices.asSlice(size * index, this.vertexLayout);

    for (var i = 0; i < values.length; i++) {
        var value = values[i];
        slice.setAtIndex(SDL3_h.C_FLOAT, i, value);
    }
}

MemorySegment createVerticies() {
    MemorySegment vertices = Arena.global().allocate(this.vertexLayout, 3);
    writeVertex(vertices, 0, new float[]{0.0f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f});
    writeVertex(vertices, 1, new float[]{-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f});
    writeVertex(vertices, 2, new float[]{0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f});
    return vertices;
}

MemorySegment verticies = createVerticies();

void main() {
    Log.setLogPriority(LogCategory.GPU, LogPriority.DEBUG);
    System.setProperty("joml.format", "false");

    try (var appResources = ResourceSet.ofConfined()) {
        var window = Video.createWindow(
                appResources,
                "Hello World",
                800,600,
                WindowFlags.RESIZABLE | WindowFlags.HIGH_PIXEL_DENSITY
        );

        var gpu = GPU.createDevice(
                appResources,
                ShaderFormat.SPIRV,
                true,
                PreferredGPUDriver.VULKAN
        );

        gpu.claimWindowForDevice(window);
        gpu.setSwapchainParameters(window, SwapchainComposition.SDR, PresentMode.VSYNC);

        var vertexBuffer = gpu.createBuffer(
            appResources,
            BufferUsageFlags.VERTEX,
            this.verticies.byteSize()
        );

        var transferBuffer = gpu.createTransferBuffer(
            appResources,
            TransferBufferUsage.UPLOAD,
            this.verticies.byteSize()
        );

        try (var mapped = gpu.mapTransferBuffer(transferBuffer, Cycle.FALSE)) {
            var memory = mapped.memory();
            memory.copyFrom(this.verticies);
        }

        try (var commandBuffer = gpu.acquireCommandBuffer()) {
            try (var copy = commandBuffer.beginCopyPass()) {
                copy.upload(transferBuffer, vertexBuffer, Cycle.FALSE);
            }
        }

        var swapchainFormat = gpu.getSwapchainTextureFormat(window);

        var pipeline = gpu.createGPUGraphicsPipeline(
                appResources,
                Shader.ShaderCreator.of(
                    ShaderFormat.SPIRV,
                    Path.of("resources/shaders/out/vertex.spv"),
                    Shader.Parameter.of(0, 0, 0, 0)
                ),
                Shader.ShaderCreator.of(
                    ShaderFormat.SPIRV,
                    Path.of("resources/shaders/out/fragment.spv"),
                    Shader.Parameter.of(0, 0, 0, 1)
                ),
                PrimitiveType.TRIANGLELIST,
                VertexInputState.of(0, this.vertexLayout, VertexInputState.VertexInputRate.VERTEX),
                RasterizerState.of(FillMode.FILL, CullMode.NONE, FrontFace.defaultValue()),
                MultisampleState.disabled(),
                DepthStencilState.of(CompareOp.LESS, DepthTest.DISABLED, DepthWrite.DISABLED),
                GraphicsPipelineTargetInfo.of(swapchainFormat, ColorTargetBlendState.standardAlphaBlend())
        );


        var cstruct = CStruct.allocate(
            appResources,
            MemoryLayout.structLayout(
                Layouts.VECTOR_3F.withName("color"),
                Layouts.MAT_4F.withName("model")
            )
        );
        var color = cstruct.vec3("color");


        var rectPipeline = new RectPipeline(appResources, gpu, swapchainFormat);


        var rectData = GrowableWriteBuffer.create(appResources);

        var viewProj = new Matrix4f().identity();
        rectData.writeFloat(-1f);
        rectData.writeFloat(-1f);
        rectData.writeFloat(1f);
        rectData.writeFloat(1f);
        rectData.writeVector4f(new Vector4f(0f, 1f, 0f, 1f));
        rectData.writeVector4f(new Vector4f(1f, 0f, 0f, 1f));
        rectData.writeFloat(0f);
        rectData.writeInt(0);
        rectData.writeFloat(0.00f);
        rectData.writeFloat(0f); //pad

        rectData.writeFloat(0f);
        rectData.writeFloat(0f);
        rectData.writeFloat(1f);
        rectData.writeFloat(1f);
        rectData.writeVector4f(new Vector4f(1f, 1f, 0f, 1f));
        rectData.writeVector4f(new Vector4f(0f, 0f, 1f, 1f));
        rectData.writeFloat(0f);
        rectData.writeInt(0);
        rectData.writeFloat(0.00f);
        rectData.writeFloat(0f); //pad

        var lastTime = Timer.getTicksNS();
        var fpsCounter = 0;

        Vector2i lastWindowSize = new Vector2i();

        while (this.running) {
            for (var event : Events.pollEvents()) {
//                if (event.data() instanceof EventOld.QuitEvent) {
//                    this.running = false;
//                    break;
//                }
//                if (event.data() instanceof EventOld.KeyboardEvent)  {
//                }
//                if (event.data() instanceof EventOld.WindowEvent)  {
//                }
            }

            var time = Timer.getTicksNS();
            if (time - lastTime >= 1e9) {
//                System.out.println("FPS: " + fpsCounter);
                fpsCounter = 0;
                lastTime = time;
            } else {
                fpsCounter++;
            }



            try (var commandBuffer = gpu.acquireCommandBuffer()) {

                var swapchain = commandBuffer.waitAndAcquireSwapchainTexture(window);
                if (swapchain == null) {
                    continue;
                }
                if (!lastWindowSize.equals(swapchain.width(), swapchain.height())) {
                    System.out.println("Swapchain resized: " + swapchain.width() + " x " + swapchain.height());
                    var windowSize = window.getSize(new Vector2i());
                    var windowSizePixels = window.getSizeInPixels(new Vector2i());
                    System.out.println("windowSize = " + windowSize);
                    System.out.println("windowSizePixels = " + windowSizePixels);
                    var scale = window.getDisplayScale();
                    System.out.println("scale = " + scale);
                    lastWindowSize.set(swapchain.width(), swapchain.height());
                }

                var clearColor = new Vector4f(0.1f, 0.2f, 0.3f, 1.0f);
                var colorTarget0 = RenderPass.ColorTargetInfo.clear(swapchain, clearColor, Cycle.FALSE);


                rectPipeline.upload(
                        gpu,
                        commandBuffer,
                        rectData
                );


                try (var renderPass = commandBuffer.beginRenderPass(null, colorTarget0)) {

                    renderPass.bindPipeline(pipeline);
                    color.set(1f, 1f, 0f);

                    commandBuffer.pushFragmentUniform(0, cstruct);

                    renderPass.bindVertexBuffer(0, vertexBuffer);

                    renderPass.drawPrimitives(3, 1, 0, 0);


                    rectPipeline.render(
                            renderPass,
                            commandBuffer,
                            viewProj
                    );

                }
            }

        }

    } catch (IOException e) {
        throw new RuntimeException(e);
    } finally {
        Init.quit();
    }

}

static class RectPipeline {


//    struct Rect {
//        x: float
//        y: float
//        width: float
//        height: float
//
//        color: Vec4
//        outline_color: Vec4
//
//        roundness: float
//        z_index: u32
//        outline_thickness: float
//        pad2: float
//    }

    static StructLayout RECT_LAYOUT = MemoryLayout.structLayout(
            Layouts.FLOAT.withName("x"),
            Layouts.FLOAT.withName("y"),
            Layouts.FLOAT.withName("width"),
            Layouts.FLOAT.withName("height"),
            Layouts.VECTOR_4F.withName("color"),
            Layouts.VECTOR_4F.withName("outline_color"),
            Layouts.FLOAT.withName("roundness"),
            Layouts.UINT.withName("z_index"),
            Layouts.FLOAT.withName("outline_thickness"),
            MemoryLayout.paddingLayout(4).withName("pad2")
    );
    static long RECT_SIZE = RECT_LAYOUT.byteSize();

    static int MAX_COUNT = 1024;

    GraphicsPipeline pipeline;
    Buffer buffer;
    TransferBuffer transferBuffer;
    CStruct vertexUniform;
    CStruct.Mat4Setter u_viewProj;

    RectPipeline(
        ResourceSet resources,
        Device gpu,
        TextureFormat colorTargetFormat
    ) throws IOException {

        this.pipeline = createPipeline(gpu, resources, colorTargetFormat);
        this.buffer = gpu.createBuffer(
            resources,
            BufferUsageFlags.GRAPHICS_STORAGE_READ,
            MAX_COUNT * RECT_SIZE
        );
        this.transferBuffer = gpu.createTransferBuffer(
            resources,
            TransferBufferUsage.UPLOAD,
            MAX_COUNT * RECT_SIZE
        );
        this.vertexUniform = CStruct.allocate(
            resources,
            MemoryLayout.structLayout(
                Layouts.MAT_4F.withName("u_viewProj")
            )
        );
        this.u_viewProj = this.vertexUniform.mat4("u_viewProj");

    }

    GraphicsPipeline createPipeline(
            Device gpu,
            ResourceSet resources,
            TextureFormat colorTargetFormat
    ) throws IOException {
        return gpu.createGPUGraphicsPipeline(
                resources,
                Shader.ShaderCreator.of(
                        ShaderFormat.SPIRV,
                        Path.of("resources/ui_rect/bin/vertex.spv"),
                        Shader.Parameter.of(0, 0, 1, 1)
                ),
                Shader.ShaderCreator.of(
                        ShaderFormat.SPIRV,
                        Path.of("resources/ui_rect/bin/fragment.spv"),
                        Shader.Parameter.of(0, 0, 0, 0)
                ),
                PrimitiveType.TRIANGLESTRIP,
                VertexInputState.of(),
                RasterizerState.of(FillMode.FILL, CullMode.NONE, FrontFace.defaultValue()),
                MultisampleState.disabled(),
                DepthStencilState.of(CompareOp.LESS, DepthTest.DISABLED, DepthWrite.DISABLED),
                GraphicsPipelineTargetInfo.of(colorTargetFormat, ColorTargetBlendState.standardAlphaBlend())
        );
    }


    void upload(
            Device gpu,
            CommandBuffer commandBuffer,
            WriteBuffer data
    ) {
        var rectCount = (int) (this.buffer.size() / RECT_SIZE);
        if (rectCount == 0) {
            return;
        }
        rectCount = Math.min(rectCount, MAX_COUNT);

        try (var mapped = gpu.mapTransferBuffer(this.transferBuffer,Cycle.TRUE)) {
            mapped.memory().copyFrom(data.memory());
        }
        try (var copyPass = commandBuffer.beginCopyPass()) {
            copyPass.upload(
                    this.transferBuffer,
                    0,
                    this.buffer,
                    0,
                    rectCount * RECT_SIZE,
                    Cycle.TRUE
            );
        }
    }


    void render(
            RenderPass renderPass,
            CommandBuffer commandBuffer,
            Matrix4f viewProj
    ) {
        var rectCount = (int) (this.buffer.size() / RECT_SIZE);
        if (rectCount == 0) {
            return;
        }
        rectCount = Math.min(rectCount, MAX_COUNT);

        renderPass.bindPipeline(this.pipeline);

        this.u_viewProj.set(viewProj);
        commandBuffer.pushVertexUniform(0, this.vertexUniform);

        renderPass.bindVertexStorageBuffer(0, this.buffer);

        renderPass.drawPrimitives(4, rectCount, 0, 0);
    }

}



