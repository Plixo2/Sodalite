

import io.github.plixo2.sodalite.file.ImageChannels;
import io.github.plixo2.sodalite.file.ImageDynamicRange;
import io.github.plixo2.sodalite.file.ImageLoader;
import io.github.plixo2.sodalite.category.events.EventConsumer;
import io.github.plixo2.sodalite.category.timer.Timer;
import io.github.plixo2.sodalite.category.video.Window;
import io.github.plixo2.sodalite.memory.*;
import io.github.plixo2.sodalite.resource.ResourceSet;
import io.github.plixo2.sodalite.category.events.Events;
import io.github.plixo2.sodalite.category.gpu.*;
import io.github.plixo2.sodalite.category.init.Init;
import io.github.plixo2.sodalite.category.log.Log;
import io.github.plixo2.sodalite.category.log.LogCategory;
import io.github.plixo2.sodalite.category.log.LogPriority;
import io.github.plixo2.sodalite.category.video.Video;
import io.github.plixo2.sodalite.category.video.WindowFlags;
import io.github.plixo2.uiiii.Render;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector4f;


import java.io.IOException;
import java.lang.foreign.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;


/*
struct Vertex
{
    float x, y, z;      //vec3 position
    float r, g, b, a;   //vec4 color
    float u, v;         //vec2 uv
};
*/

static StructLayout vertexLayout = MemoryLayout.structLayout(
    Layouts.VECTOR_3F.withName("position"),
    Layouts.VECTOR_4F.withName("color"),
    Layouts.VECTOR_2F.withName("uv")
);

static WriteBuffer<?> createVerticies() {
    var buffer = ConstantWriteBuffer.allocate(ResourceSet.global(), vertexLayout, 4);
    buffer.writeFloats(0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f);
    buffer.writeFloats(1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    buffer.writeFloats(0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f);
    buffer.writeFloats(1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f);
    return buffer;
}

static class Instance {
    boolean running = true;
    WriteBuffer<?> verticies = createVerticies();

    Matrix4f projectionMatrix = new Matrix4f();

    ResourceSet msaaTextureSet = null;
    Texture msaaTexture = null;

    void newMsaaTexture(
            ResourceSet parentResources,
            Device gpu,
            TextureFormat format,
            int width,
            int height
    ) {
        if (this.msaaTexture != null) {
            if (width == this.msaaTexture.width() && height == this.msaaTexture.height()) {
                return;
            }
            this.msaaTextureSet.close();
        }
        System.out.println("Creating new MSAA texture with size " + width + "x" + height);
        this.msaaTextureSet = ResourceSet.ofConfined(parentResources);
        this.msaaTexture = TextureBuilder.of2D(
                format,
                TextureUsageFlags.COLOR_TARGET,
                width,
                height
        ).setSampleCount(SampleCount.COUNT_4).build(
                this.msaaTextureSet,
                gpu
        );

    }
}



static void runFor(
        Instance instance,
        Device gpu,
        Window window
) throws IOException {
    try (var appResources = ResourceSet.ofConfined()) {

        gpu.setSwapchainParameters(window, SwapchainComposition.SDR, PresentMode.VSYNC);

        var exampleSamplers = new Sampler[14];
        for (var i = 0; i < exampleSamplers.length; i++) {
            exampleSamplers[i] = SamplerBuilder.of(
                    Filter.LINEAR,
                    SamplerAddressMode.REPEAT,
                    0f
            ).build(appResources, gpu);
        }
        var pixelated = SamplerBuilder.of(
                Filter.NEAREST,
                SamplerAddressMode.REPEAT,
                0f
        ).build(appResources, gpu);


        Texture exampleTexture;


        var vertexBuffer = gpu.createBuffer(
                appResources,
                BufferUsageFlags.VERTEX,
                instance.verticies.capacity()
        );

        var testTextures = new ArrayList<Texture>();

        try (var uploadCommandBuffer = gpu.acquireCommandBuffer()) {

            {
                var vertexTransferBuffer = gpu.createTransferBuffer(
                        appResources,
                        TransferBufferUsage.UPLOAD,
                        instance.verticies.capacity()
                );
                try (var mapped = gpu.mapTransferBuffer(vertexTransferBuffer, Cycle.FALSE)) {
                    var memory = mapped.memory();
                    memory.copyFrom(instance.verticies.memory());
                }
                try (var copy = uploadCommandBuffer.beginCopyPass()) {
                    copy.upload(vertexTransferBuffer, vertexBuffer, Cycle.FALSE);
                }
            }

            {
                exampleTexture = loadTexture(
                        appResources,
                        gpu,
                        uploadCommandBuffer,
                        Path.of("resources/example.png")
                );
            }

            for (var i = 1; i <= 48; i++) {
                var path = Path.of("resources/tests/test (" + i + ").png");
                var texture = loadTexture(
                        appResources,
                        gpu,
                        uploadCommandBuffer,
                        path
                );
                testTextures.add(texture);
            }
        }

        var exampleTextures = new Texture[exampleSamplers.length];
        Arrays.fill(exampleTextures, exampleTexture);

        var swapchainFormat = gpu.getSwapchainTextureFormat(window);

        var windowSizeInPixels = window.getSizeInPixels(new Vector2i());

        instance.newMsaaTexture(
                appResources,
                gpu,
                swapchainFormat,
                windowSizeInPixels.x,
                windowSizeInPixels.y
        );
        var pipeline = gpu.createGPUGraphicsPipeline(
                appResources,
                Shader.ShaderCreator.of(
                        ShaderFormat.SPIRV,
                        Path.of("resources/textured/bin/vertex.spv"),
                        Shader.Parameters.of(0, 0, 0, 0)
                ),
                Shader.ShaderCreator.of(
                        ShaderFormat.SPIRV,
                        Path.of("resources/textured/bin/fragment.spv"),
                        Shader.Parameters.of(exampleSamplers.length, 0, 0, 1)
                ),
                PrimitiveType.TRIANGLESTRIP,
                VertexInputState.of(0, vertexLayout, VertexInputState.VertexInputRate.VERTEX),
                RasterizerState.of(FillMode.FILL, CullMode.NONE, FrontFace.defaultValue()),
                MultisampleState.enabled(instance.msaaTexture.sampleCount()),
                DepthStencilState.of(CompareOp.LESS, DepthTest.DISABLED, DepthWrite.DISABLED),
                GraphicsPipelineTargetInfo.of(instance.msaaTexture.format(), ColorTargetBlendState.standardAlphaBlend())
        );

        var cstruct = CStruct.allocate(
            appResources,
            MemoryLayout.structLayout(
                    Layouts.VECTOR_3F.withName("color"),
                    Layouts.MAT_4F.withName("model")
            )
        );

        var rectPipeline = new RectPipeline(appResources, gpu, instance.msaaTexture);
        var render = new Render(gpu, appResources, instance.msaaTexture);

        var rectData = GrowableWriteBuffer.create(appResources);

        rectData.writeFloat(100f);
        rectData.writeFloat(100f);
        rectData.writeFloat(300f);
        rectData.writeFloat(300f);
        rectData.writeVector4f(new Vector4f(0f, 1f, 0f, 1f));
        rectData.writeVector4f(new Vector4f(1f, 0f, 0f, 1f));
        rectData.writeFloat(30f);
        rectData.writeInt(0);
        rectData.writeFloat(5f);
        rectData.writeFloat(0f); //pad

        rectData.writeFloat(0f);
        rectData.writeFloat(0f);
        rectData.writeFloat(90f);
        rectData.writeFloat(90f);
        rectData.writeVector4f(new Vector4f(1f, 1f, 0f, 1f));
        rectData.writeVector4f(new Vector4f(0f, 0f, 1f, 1f));
        rectData.writeFloat(10f);
        rectData.writeInt(0);
        rectData.writeFloat(4f);
        rectData.writeFloat(0f); //pad

        var lastTimeFPS = Timer.getTicksNS();
        var fpsCounter = 0;

        var events = new EventConsumer() {

            @Override
            public void onWindowCloseRequested(long timestamp, int windowID) {
                if (windowID != window.id()) {
                    return;
                }
                instance.running = false;
            }

            @Override
            public void onWindowPixelSizeChanged(
                    long timestamp,
                    int windowID,
                    int width,
                    int height
            ) {
                if (windowID != window.id()) {
                    return;
                }

                instance.projectionMatrix.identity().ortho(
                        0,
                        width,
                        height,
                        0,
                        -1.0f,
                        1.0f
                );
            }

            @Override
            public void onWindowDisplayScaleChanged(long timestamp, int windowID) {
                if (windowID != window.id()) {
                    return;
                }
                var scale = window.getDisplayScale();
                System.out.println("Window display scale changed: " + scale);
            }
        };

        long lastTime = Timer.getTicksNS();
        while (instance.running) {
            Events.pollEvents(events);

            var time = Timer.getTicksNS();
            fpsCounter++;

            if (time - lastTimeFPS >= Timer.NS_PER_SECOND) {
//                var freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024);
//                var totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
//                var usedMemory = totalMemory - freeMemory;
//                System.out.println("FPS: " + fpsCounter + " | Memory Usage: " + usedMemory + " MB / " + totalMemory + " MB");

                System.out.println("FPS: " + fpsCounter);
                fpsCounter = 0;
                var timeOver = time - lastTimeFPS - Timer.NS_PER_SECOND;
                lastTimeFPS = time - timeOver;
            }

            var delta = (float) ((time - lastTime) / 1e9d);
            lastTime = time;

//            instance.projectionMatrix.rotate(delta * 5f, 0f, 1f, 0f);

            try (var commandBuffer = gpu.acquireCommandBuffer()) {

                var swapchain = commandBuffer.waitAndAcquireSwapchainTexture(window);
                if (swapchain == null) {
                    continue;
                }

                instance.newMsaaTexture(
                        appResources,
                        gpu,
                        swapchainFormat,
                        swapchain.width(),
                        swapchain.height()
                );

                var clearColor = new Vector4f(0.1f, 0.2f, 0.3f, 1.0f);
                var colorTarget0 = RenderPass.ColorTargetInfo.resolve(
                        instance.msaaTexture,
                        clearColor,
                        swapchain,
                        Cycle.FALSE,
                        Cycle.FALSE
                );

//                rectPipeline.upload(
//                        gpu,
//                        commandBuffer,
//                        rectData
//                );

                render.beginFrame(
                        instance.projectionMatrix,
                        swapchain.width(),
                        swapchain.height()
                );

                render.drawRect(
                        0, 0,
                        500, 500,
                        new Vector4f(1f, 0f, 1f, 1f),
                        30f,
                        new Vector4f(0f, 1f, 1f, 1f),
                        5f
                );

                for (var i = 0; i < testTextures.size(); i++) {
                    var t = testTextures.get(i);
                    var x = i * 30;
                    render.drawTexture(
                            x, 0,
                            x + 400, 400,
                            new Vector4f(1f, 1f, 1f, 1f),
                            t,
                            pixelated
                    );
                }
                render.drawRect(
                        500, 500,
                        700, 600,
                        new Vector4f(0.1f, 0.15f, 0.2f, 1f),
                        3f,
                        new Vector4f(1f, 1f, 1f, 1f),
                        5f
                );

                try (var copypass = commandBuffer.beginCopyPass()){
                    render.upload(gpu, copypass);
                }

                try (var renderPass = commandBuffer.beginRenderPass(null, colorTarget0)) {

                    render.renderFrame(renderPass, commandBuffer);

//                    rectPipeline.render(
//                            renderPass,
//                            commandBuffer,
//                            instance.projectionMatrix
//                    );
                }
            }
        }
    }

}


static Texture loadTexture(
        ResourceSet resources,
        Device device,
        CommandBuffer commandBuffer,
        Path path
) throws IOException {
    try (var textureData = ResourceSet.ofConfined()) {
        var imageData = ImageLoader.load(
                textureData.arena(),
                path,
                ImageDynamicRange.SDR,
                ImageChannels.RGBA
        ).orThrow(IOException::new);

        var texture = TextureBuilder.of2D(
                TextureFormat.R8G8B8A8_UNORM,
                TextureUsageFlags.SAMPLER,
                imageData.width(),
                imageData.height()
        ).build(resources, device);

        var region = TextureRegion.ofFull2D(texture);

        var textureTransferBuffer = device.createTransferBuffer(
                textureData,
                TransferBufferUsage.UPLOAD,
                imageData.data().byteSize()
        );
        try (var mapped = device.mapTransferBuffer(textureTransferBuffer, Cycle.FALSE)) {
            mapped.memory().copyFrom(imageData.data());
        }
        try (var copy = commandBuffer.beginCopyPass()) {
            copy.upload(
                    textureTransferBuffer,
                    region,
                    Cycle.FALSE
            );
        }

        return texture;
    }
}

void run() throws IOException {
    try (var staticResourc = ResourceSet.ofConfined()) {
        var gpu = GPU.createDevice(
                staticResourc,
                ShaderFormat.SPIRV,
                true,
                PreferredGPUDriver.VULKAN
        );
        var window = Video.createWindow(
                staticResourc,
                "Hello World",
                800,600,
                WindowFlags.RESIZABLE | WindowFlags.HIGH_PIXEL_DENSITY
        );

        try (var _ = gpu.claimWindow(window)) {
            runFor(new Instance(), gpu, window);
        }
    }

}

void main() throws IOException {
    System.setProperty("joml.format", "false");

    Init.setAppMetaData("Hello World", "1.0.0", "com.example.helloworld");
    Log.setLogPriority(LogCategory.GPU, LogPriority.DEBUG);

    try {
        run();
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
    WriteBuffer<?> vertexUniform;

    RectPipeline(
        ResourceSet resources,
        Device gpu,
        TextureInfo colorTargetFormat
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

    }

    GraphicsPipeline createPipeline(
            Device gpu,
            ResourceSet resources,
            TextureInfo colorTarget
    ) throws IOException {
        return gpu.createGPUGraphicsPipeline(
                resources,
                Shader.ShaderCreator.of(
                        ShaderFormat.SPIRV,
                        Path.of("resources/ui_rect/bin/vertex.spv"),
                        Shader.Parameters.of(0, 0, 1, 1)
                ),
                Shader.ShaderCreator.of(
                        ShaderFormat.SPIRV,
                        Path.of("resources/ui_rect/bin/fragment.spv"),
                        Shader.Parameters.of(0, 0, 0, 0)
                ),
                PrimitiveType.TRIANGLESTRIP,
                VertexInputState.of(),
                RasterizerState.of(FillMode.FILL, CullMode.NONE, FrontFace.defaultValue()),
                MultisampleState.enabled(colorTarget.sampleCount()),
                DepthStencilState.disabled(),
                GraphicsPipelineTargetInfo.of(colorTarget.format(), ColorTargetBlendState.standardAlphaBlend())
        );
    }


    void upload(
            Device gpu,
            CommandBuffer commandBuffer,
            WriteBuffer<?> data
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
                    this.buffer,
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

        this.vertexUniform.clear().writeMatrix4f(viewProj);
        commandBuffer.pushVertexUniform(0, this.vertexUniform);

        renderPass.bindVertexStorageBuffer(0, this.buffer);

        renderPass.drawPrimitives(4, rectCount, 0, 0);
    }

}



