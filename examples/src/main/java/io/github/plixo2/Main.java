

import io.github.plixo2.sodalite.category.version.Version;
import io.github.plixo2.sodalite.category.version.VersionTarget;
import io.github.plixo2.sodalite.memory.MemorySource;
import io.github.plixo2.sodalite.io.image.ImageChannels;
import io.github.plixo2.sodalite.io.image.ImageDynamicRange;
import io.github.plixo2.sodalite.io.image.ImageLoader;
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
import io.github.plixo2.ui.Render;
import lombok.RequiredArgsConstructor;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector4f;


import java.io.IOException;
import java.lang.foreign.*;
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
    Layouts.FLOAT_3.withName("position"),
    Layouts.FLOAT_4.withName("color"),
    Layouts.FLOAT_2.withName("uv")
);

static WriteBuffer<?> createVerticies() {
    var buffer = ConstantWriteBuffer.allocate(ResourceSet.global(), vertexLayout, 4);
    buffer.writeFloats(0.0f, 0.0f, 0.0f,   1.0f, 0.0f, 0.0f, 1.0f,   0.0f, 1.0f);
    buffer.writeFloats(1.0f, 0.0f, 0.0f,   1.0f, 1.0f, 0.0f, 1.0f,   1.0f, 1.0f);
    buffer.writeFloats(0.0f, 1.0f, 0.0f,   1.0f, 0.0f, 1.0f, 1.0f,   0.0f, 0.0f);
    buffer.writeFloats(1.0f, 1.0f, 0.0f,   1.0f, 0.0f, 1.0f, 1.0f,   1.0f, 0.0f);
    return buffer;
}

@RequiredArgsConstructor
static class Instance implements EventConsumer {
    boolean running = true;
    WriteBuffer<?> verticies = createVerticies();

    Matrix4f projectionMatrix = new Matrix4f();

    ResourceSet msaaTextureSet = null;
    Texture msaaTexture = null;

    final Window window;
    final Device gpu;

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


    @Override
    public void onWindowCloseRequested(long timestamp, int windowID) {
        if (windowID != this.window.id().value()) {
            return;
        }
        this.running = false;
    }

    @Override
    public void onWindowPixelSizeChanged(
            long timestamp,
            int windowID,
            int width,
            int height
    ) {
        if (windowID != this.window.id().value()) {
            return;
        }

        this.projectionMatrix.identity().ortho(
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
        if (windowID != this.window.id().value()) {
            return;
        }
        var scale = this.window.getDisplayScale();
        System.out.println("Window display scale changed: " + scale);
    }


    void run() throws IOException {

        var appResources = ResourceSet.global();
//        var tray = Tray.createTray(appResources, null, "Tray stuff");
//        var menu = tray.createMenu();
//        menu.addButton("Set Clipboard 0").setCallback(ref -> {
//            Clipboard.setData(
//                    (arena, mimeType) -> MemorySegment.NULL,
//                    "text/"
//            );
//        });
//        menu.addButton("Read clipboard").setCallback(ref -> {
//            for (var mimeType : Clipboard.getMimeTypes()) {
//                System.out.println("mimeType = " + mimeType);
//                try (var c = ResourceSet.ofConfined()) {
//                    var data = Clipboard.getData(c, mimeType);
//                    System.out.println("data.segment().address() = " + data.segment().address());
//                    System.out.println("data.segment().address() = " + data.segment().byteSize());
//                }
//            }
//        });
//        menu.addButton("HI").setCallback(ref -> System.out.println("Clicked HI"));
//        menu.addButton("HI?", true).setCallback(ref -> System.out.println("Clicked HI?"));
//        menu.addSeparator();
//        menu.addCheckbox("Check me");
//        menu.addCheckbox("Check me1", true, false);
//        menu.addCheckbox("Check me2", true, true);
//        menu.addCheckbox("Check me3", false, false);
//        menu.addCheckbox("Check me4", false, true).setCallback(ref -> System.out.println("Check me4"));
//        menu.addSeparator();
//        menu.addSubmenu("Submenu").addButton("In submenu");

//        System.out.println("Clipboard.getMimeTypes() = " + Clipboard.getMimeTypes());
        this.gpu.setSwapchainParameters(this.window, SwapchainComposition.SDR, PresentMode.VSYNC);

        var exampleSamplers = new Sampler[14];
        for (var i = 0; i < exampleSamplers.length; i++) {
            exampleSamplers[i] = SamplerBuilder.of(
                    Filter.LINEAR,
                    SamplerAddressMode.REPEAT,
                    0f
            ).build(appResources, this.gpu);
        }
        var pixelated = SamplerBuilder.of(
                Filter.NEAREST,
                SamplerAddressMode.REPEAT,
                0f
        ).build(appResources, this.gpu);


        Texture exampleTexture;


        var vertexBuffer = this.gpu.createBuffer(
                appResources,
                BufferUsageFlags.VERTEX,
                this.verticies.capacity()
        );

        var testTextures = new ArrayList<Texture>();

        try (var uploadCommandBuffer = this.gpu.acquireCommandBuffer()) {

            {
                var vertexTransferBuffer = this.gpu.createTransferBuffer(
                        appResources,
                        TransferBufferUsage.UPLOAD,
                        this.verticies.capacity()
                );
                try (var mapped = vertexTransferBuffer.map(this.gpu, Cycle.FALSE)) {
                    var memory = mapped.memory();
                    memory.copyFrom(this.verticies.memory());
                }
                try (var copy = uploadCommandBuffer.beginCopyPass()) {
                    copy.upload(vertexTransferBuffer, vertexBuffer, Cycle.FALSE);
                }
            }

            {
                exampleTexture = loadTexture(
                        appResources,
                        this.gpu,
                        uploadCommandBuffer,
                        Path.of("resources/example.png")
                );
            }

            for (var i = 1; i <= 48; i++) {
                var path = Path.of("resources/tests/test (" + i + ").png");
                var texture = loadTexture(
                        appResources,
                        this.gpu,
                        uploadCommandBuffer,
                        path
                );
                testTextures.add(texture);
            }

        }

        var exampleTextures = new Texture[exampleSamplers.length];
        Arrays.fill(exampleTextures, exampleTexture);

        var swapchainFormat = this.gpu.getSwapchainTextureFormat(this.window);

        var windowSizeInPixels = this.window.getSizeInPixels(new Vector2i());

        this.newMsaaTexture(
                appResources,
                this.gpu,
                swapchainFormat,
                windowSizeInPixels.x,
                windowSizeInPixels.y
        );

        time("Resource Creation");

        var render = new Render(this.gpu, appResources, this.msaaTexture);

        time("Render Creation");

        var lastTimeFPS = Timer.getTicksNS();
        var fpsCounter = 0;

        long lastTime = Timer.getTicksNS();
        while (this.running) {
            Events.pollEvents(this);

            var time = Timer.getTicksNS();
            fpsCounter++;

            var secondPassed = time - lastTimeFPS >= Timer.NS_PER_SECOND;
            if (secondPassed) {
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

            try (var commandBuffer = this.gpu.acquireCommandBuffer()) {
                var swapchain = commandBuffer.waitAndAcquireSwapchainTexture(this.window);
                if (swapchain == null) {
                    continue;
                }

                this.newMsaaTexture(
                        appResources,
                        this.gpu,
                        swapchainFormat,
                        swapchain.width(),
                        swapchain.height()
                );

                var clearColor = new Vector4f(0.1f, 0.2f, 0.3f, 1.0f);
                var colorTarget0 =
                        RenderPass.ColorTargetInfo.resolve(
                                this.msaaTexture,
                                clearColor,
                                swapchain,
                                Cycle.FALSE,
                                Cycle.FALSE
                        );

                render.beginFrame(
                        this.projectionMatrix,
                        swapchain.width(),
                        swapchain.height()
                );

                render.drawRect(
                        0,
                        0,
                        500,
                        500,
                        new Vector4f(1f, 0f, 1f, 1f),
                        30f,
                        new Vector4f(0f, 1f, 1f, 1f),
                        5f
                );

                render.transform().rotate(Math.toRadians(32.23f), 0f, 0f, 1f);


                for (var i = 0; i < testTextures.size(); i++) {
                    var t = testTextures.get(i);
                    var x = i * 30;
                    render.drawTexture(
                            x,
                            0,
                            x + 400,
                            400,
                            new Vector4f(1f, 1f, 1f, 1f),
                            t,
                            pixelated
                    );
                }
                render.transform().identity();

                render.drawRect(
                        500,
                        500,
                        700,
                        600,
                        new Vector4f(0.1f, 0.15f, 0.2f, 1f),
                        3f,
                        new Vector4f(1f, 1f, 1f, 1f),
                        5f
                );

                try (var copypass = commandBuffer.beginCopyPass()) {
                    render.upload(this.gpu, copypass);
                }

                try (var renderPass = commandBuffer.beginRenderPass(null, colorTarget0)) {
                    render.renderFrame(renderPass, commandBuffer);
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
                MemorySource.of(path),
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
        try (var mapped = textureTransferBuffer.map(device, Cycle.FALSE)) {
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

//    var dir =
//            Path.of("examples/src/main/resources/gpu_examples/Images/bcn/");
//    try (var stream = Files.walk(dir)) {
//        for (var path : stream.filter(Files::isRegularFile).toList()) {
//            try (var imgData = Arena.ofConfined()) {
//                System.out.println(path);
//                var a = ImageLoader.loadDDS(imgData, ImageSource.of(path));
//                var b = a.orThrow(IOException::new);
//            }
//        }
//    }
//
//    dir =
//            Path.of("examples/src/main/resources/gpu_examples/Images/astc/");
//    try (var stream = Files.walk(dir)) {
//        for (var path : stream.filter(Files::isRegularFile).toList()) {
//            try (var imgData = Arena.ofConfined()) {
//                System.out.println(path);
//                var a = ImageLoader.loadASTC(imgData, ImageSource.of(path));
//                var b = a.orThrow(IOException::new);
//            }
//        }
//    }


    var window = Video.createWindow(
            ResourceSet.global(),
            "Hello World",
            800, 600,
            WindowFlags.RESIZABLE | WindowFlags.HIGH_PIXEL_DENSITY
    );
    time("Window Creation");
    var gpu = GPU.createDevice(
            ResourceSet.global(),
            ShaderFormat.SPIRV,
            true,
            GPUDriver.optimal()
    );
    time("GPU Creation");

    try (
        var _ = gpu.claimWindow(window);
    ) {
        var instance = new Instance(window, gpu);
        instance.run();
    }
}

void main() throws IOException {
    System.setProperty("joml.format", "false");

    startTime = Timer.getTicksNS();

    Init.setAppMetaData("Sodalite", "0.0.1", "io.github.plixo2.Sodalite");
    Log.setLogPriority(LogCategory.GPU, LogPriority.DEBUG);
    System.out.println("Compiled against SDL version " + Version.getVersion(VersionTarget.COMPILED));
    System.out.println("Linked against SDL version " + Version.getVersion(VersionTarget.LINKED));
    System.out.println("Compiled against SDL revision " + Version.getRevision(VersionTarget.COMPILED));
    System.out.println("Linked against SDL revision " + Version.getRevision(VersionTarget.LINKED));

    time("Initialization");
    try {
        run();
    } finally {
        Init.quit();
    }


}
static long startTime;
static void time(String location) {
    var currentTime = Timer.getTicksNS();
    var delta = (currentTime - startTime) / 1e6d;

    System.out.println(location + " +" + String.format("%.2f", delta) + " ms");

}