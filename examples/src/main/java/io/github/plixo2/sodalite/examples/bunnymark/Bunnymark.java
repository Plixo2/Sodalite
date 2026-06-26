package io.github.plixo2.sodalite.examples.bunnymark;

import io.github.plixo2.sodalite.category.gpu.*;
import io.github.plixo2.sodalite.category.init.AppResult;
import io.github.plixo2.sodalite.category.init.Init;
import io.github.plixo2.sodalite.category.keycode.Keycode;
import io.github.plixo2.sodalite.category.keycode.Keymod;
import io.github.plixo2.sodalite.category.main.Callbacks;
import io.github.plixo2.sodalite.category.mouse.Mouse;
import io.github.plixo2.sodalite.category.mouse.MouseButton;
import io.github.plixo2.sodalite.category.scancode.Scancode;
import io.github.plixo2.sodalite.category.timer.Timer;
import io.github.plixo2.sodalite.category.video.Video;
import io.github.plixo2.sodalite.category.video.Window;
import io.github.plixo2.sodalite.category.video.WindowFlags;
import io.github.plixo2.sodalite.io.image.ImageChannels;
import io.github.plixo2.sodalite.io.image.ImageData;
import io.github.plixo2.sodalite.io.image.ImageDynamicRange;
import io.github.plixo2.sodalite.io.image.ImageLoader;
import io.github.plixo2.sodalite.memory.CStruct;
import io.github.plixo2.sodalite.memory.Layouts;
import io.github.plixo2.sodalite.memory.MemorySource;
import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.SneakyThrows;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.awt.*;
import java.io.IOException;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.StructLayout;
import java.util.concurrent.ThreadLocalRandom;

/// Port of
/// ["SDL_gpu: it begins with a triangle" by Hamdy Elzanqali](https://hamdy-elzanqali.medium.com/let-there-be-triangles-sdl-gpu-edition-bd82cf2ef615)
public class Bunnymark implements Callbacks {
    private static final Vector4f CLEAR_COLOR = new Vector4f(1f);

    static StructLayout UniformBuffer = MemoryLayout.structLayout(
            Layouts.FLOAT_4X4.withName("u_projection")
    );
    CStruct uniforms = CStruct.allocate(ResourceSet.global(), UniformBuffer);

    Window window;
    Device device;
    GraphicsPipeline pipeline;
    BunnyStorage bunnyStorage;
    Texture texture;
    Sampler sampler;

    Matrix4f projection = new Matrix4f();
    Vector4f viewport = new Vector4f();
    Vector4f bunnyColor = new Vector4f(1);
    long lastTick = Timer.getTicksNS();
    double lastUploadTime = 0;   // time last upload took
    long lastFPSUpdate = Timer.getTicksNS();
    int lastUpdatedFPS = 0;
    int fpsCounter = 0;

    boolean vsync = true;
    boolean processParallel = true;
    int newBunnyAmount = 256;

    private void update() {

        var beforeUpdate = Timer.getTicksNS();
        this.bunnyStorage.update(this.processParallel, this.viewport);
        var afterUpdate = Timer.getTicksNS();
        var updateTime = (afterUpdate - beforeUpdate) / (double)Timer.NS_PER_MS;

        var delta = this.updateTimings();
        this.updateWindowTitle(delta, updateTime + this.lastUploadTime);

        var flags = Mouse.getMouseState().flags();
        if (flags != 0) {
            this.bunnyStorage.add(this.newBunnyAmount, this.bunnyColor);
        }

    }

    private void upload(CopyPass copyPass) {
        var beforeUpload = Timer.getTicksNS();
        this.bunnyStorage.upload(this.processParallel, this.device, copyPass);
        var afterUpload = Timer.getTicksNS();
        this.lastUploadTime = (afterUpload - beforeUpload) / (double)Timer.NS_PER_MS;
    }

    private void render(CommandBuffer commandBuffer, RenderPass renderPass) {
        if (this.bunnyStorage.count() == 0) {
            return;
        }
        renderPass.bindPipeline(this.pipeline);

        this.uniforms.at("u_projection").writeMatrix4f(this.projection);
        commandBuffer.pushVertexUniform(0, this.uniforms);

        renderPass.bindFragmentSampler(0, this.texture, this.sampler);
        renderPass.bindVertexBuffer(0, this.bunnyStorage.currentBuffer());

        renderPass.drawPrimitives(4, this.bunnyStorage.count(), 0, 0);
    }

    /// @return delta time in milliseconds
    private double updateTimings() {
        var tick = Timer.getTicksNS();
        var delta = (tick - this.lastTick) / (double)Timer.NS_PER_MS;
        this.lastTick = tick;

        if (tick - this.lastFPSUpdate > Timer.NS_PER_SECOND) {
            this.lastFPSUpdate = tick;
            this.lastUpdatedFPS = this.fpsCounter;
            this.fpsCounter = 0;
        }
        this.fpsCounter++;
        return delta;
    }

    private void updateWindowTitle(double delta, double updateTime) {

        var title = String.format(
                "Bunnymark | %7d bunnies | %4d FPS | %6.2f ms | %6.2f ms cpu | %5d MB used",
                this.bunnyStorage.count(),
                this.lastUpdatedFPS,
                delta,
                updateTime,
                getUsedMemoryMB()
        );

        this.window.setTitle(title);
    }

    @Override
    public AppResult onMouseButtonDown(
            long timestamp, int windowID,
            int mouseID, @MouseButton int button,
            int clicks,
            float x, float y
    ) {
        var random = ThreadLocalRandom.current();
        var color = Color.getHSBColor(
                random.nextFloat(),
                random.nextFloat(0.65f, 0.95f),
                random.nextFloat(0.85f, 1f)
        );
        this.bunnyColor = new Vector4f(
                color.getRed() / 255f,
                color.getGreen() / 255f,
                color.getBlue() / 255f,
                1f
        );


        return AppResult.CONTINUE;
    }

    @Override
    public AppResult onWindowCloseRequested(long timestamp, int windowID) {
        return AppResult.SUCCESS;
    }

    @Override
    public AppResult onWindowPixelSizeChanged(long timestamp, int windowID, int width, int height) {
        this.projection.identity().ortho2D(0, width, height, 0);
        this.viewport.set(-16, -16, width, height);

        return AppResult.CONTINUE;
    }


    @Override
    public AppResult onKeyDown(
            long timestamp, int windowID, int keyboardID,
            Scancode scancode, Keycode key,
            @Keymod int keymod,
            short raw,
            boolean repeat
    ) {
        var bunnyAmount = this.newBunnyAmount;
        if (scancode == Scancode.V) {
            this.vsync = !this.vsync;
            if (this.vsync) {
                System.out.println("Vsync enabled");
            } else {
                System.out.println("Vsync disabled");
            }
            // `setSwapchainParameters` may set a new swapchain format,
            // which requires a new pipeline to be created
            setPipeline(this.vsync);
        } else if (scancode == Scancode.P) {
            this.processParallel = !this.processParallel;
            if (this.processParallel) {
                System.out.println("Parallel processing enabled");
            } else {
                System.out.println("Parallel processing disabled");
            }
        } else if (scancode == Scancode.UP) {
            this.newBunnyAmount = Math.min(this.newBunnyAmount * 2, 4096);
        } else if (scancode == Scancode.DOWN) {
            this.newBunnyAmount = Math.max(this.newBunnyAmount / 2, 1);
        }

        if (this.newBunnyAmount != bunnyAmount) {
            System.out.println("Bunnies: " + this.newBunnyAmount);
        }

        return AppResult.CONTINUE;
    }

    @SneakyThrows
    private void setPipeline(boolean vsync) {
        var currentFormat = this.device.getSwapchainTextureFormat(this.window);

        if (vsync) {
            this.device.setSwapchainParameters(this.window, SwapchainComposition.SDR, PresentMode.VSYNC);
        } else {
            // Only SwapchainComposition.SDR + PresentMode.VSYNC is guaranteed to be supported
            if (this.device.supportsPresentMode(this.window, PresentMode.IMMEDIATE)) {
                this.device.setSwapchainParameters(this.window, SwapchainComposition.SDR, PresentMode.IMMEDIATE);
            }
        }

        var newFormat = this.device.getSwapchainTextureFormat(this.window);

        if (this.pipeline == null || currentFormat != newFormat) {
            this.pipeline = createPipeline(ResourceSet.ofAuto(), this.device, newFormat);
        }
    }

    @Override
    public AppResult init(String[] args) throws IOException {
        Init.setAppMetaData("Bunnymark", "1.0", "io.github.plixo2.Sodalite");
        this.window = Video.createWindow(
                ResourceSet.global(),
                "Bunnymark",
                1280, 720,
                WindowFlags.RESIZABLE
        );
        this.device = GPU.createDevice(
                ResourceSet.global(),
                ShaderFormat.SPIRV,
                true,
                GPUDriver.optimal()
        );
        var _ = this.device.claimWindow(this.window);

        System.out.println("Using GPU driver: " + this.device.getDriver().name());
        System.out.println("Hold any mouse button to add bunnies");
        System.out.println("Press V to toggle vsync");
        System.out.println("Press P to toggle parallel processing");
        System.out.println("Press UP or DOWN to change the number of bunnies to add");
        System.out.println("Bunnies to add: " + this.newBunnyAmount);

        this.texture = bunnyTexture(ResourceSet.global(), this.device);
        this.sampler = SamplerBuilder.of(
            Filter.NEAREST,
            SamplerAddressMode.REPEAT,
            0f
        ).build(ResourceSet.global(), this.device);

        this.bunnyStorage = new BunnyStorage(ResourceSet.global(), this.device);

        setPipeline(this.vsync);

        return AppResult.CONTINUE;
    }

    @Override
    public AppResult iterate() {
        this.update();

        try (var commandBuffer = this.device.acquireCommandBuffer()) {
            var swapchain = commandBuffer.waitAndAcquireSwapchainTexture(this.window);
            if (swapchain == null) {
                return AppResult.CONTINUE;
            }

            try (var copyPass = commandBuffer.beginCopyPass()) {
                this.upload(copyPass);
            }

            var colorTarget0 = RenderPass.ColorTargetInfo.clear(swapchain, CLEAR_COLOR, Cycle.TRUE);

            try (var renderPass = commandBuffer.beginRenderPass(null, colorTarget0)) {
                this.render(commandBuffer, renderPass);
            }
        }

        return AppResult.CONTINUE;
    }


    @Override
    public void quit(AppResult result) {}


    private static Texture bunnyTexture(
            ResourceSet resources,
            Device device
    ) throws IOException {
        var stream = Bunnymark.class.getResourceAsStream("/Bunnymark/bunny.png");
        try (var textureData = ResourceSet.ofConfined()) {
            var imageData = ImageLoader.load(
                    textureData.arena(),
                    MemorySource.of(stream),
                    ImageDynamicRange.SDR,
                    ImageChannels.RGBA
            ).orThrow(IOException::new);

            var texture = TextureBuilder.of2D(
                    TextureFormat.R8G8B8A8_UNORM,
                    TextureUsageFlags.SAMPLER,
                    imageData.width(),
                    imageData.height()
            ).build(resources, device);

            uploadTexture(device, imageData, texture);

            return texture;
        }

    }

    private static void uploadTexture(
            Device device,
            ImageData imageData,
            Texture texture
    ) {
        try (var uploadSet = ResourceSet.ofConfined()) {
            var region = TextureRegion.ofFull2D(texture);

            var textureTransferBuffer = device.createTransferBuffer(
                    uploadSet,
                    TransferBufferUsage.UPLOAD,
                    imageData.data().byteSize()
            );
            try (var mapped = textureTransferBuffer.map(device, Cycle.FALSE)) {
                mapped.memory().copyFrom(imageData.data());
            }
            try (var commandBuffer = device.acquireCommandBuffer()){
                try (var copy = commandBuffer.beginCopyPass()) {
                    copy.upload(
                            textureTransferBuffer,
                            region,
                            Cycle.FALSE
                    );
                }
            }
        }
    }

    private static GraphicsPipeline createPipeline(
            ResourceSet resources,
            Device device,
            TextureFormat colorTargetFormat
    ) throws IOException {
        var useSpirv = device.supportsShaderFormat(ShaderFormat.SPIRV);
        var format = useSpirv ? ShaderFormat.SPIRV : ShaderFormat.DXIL;
        var ext = useSpirv ? "spv" : "dxil";
        var dir = useSpirv ? "spirv" : "dxil";

        var vs = Bunnymark.class.getResourceAsStream("/Bunnymark/" + dir + "/vertex." + ext);
        var fs = Bunnymark.class.getResourceAsStream("/Bunnymark/" + dir + "/fragment." + ext);

        return device.createGraphicsPipeline(
                resources,
                Shader.Creator.of(format, vs, Shader.Parameters.of(0, 0, 0, 1)),
                Shader.Creator.of(format, fs, Shader.Parameters.of(1, 0, 0, 0)),
                PrimitiveType.TRIANGLESTRIP,
                VertexInputState.of(0, Bunny.LAYOUT, VertexInputState.Rate.INSTANCE),
                RasterizerState.defaultValue(),
                MultisampleState.disabled(),
                DepthStencilState.disabled(),
                GraphicsPipelineTargetInfo.of(
                        ColorTargetDescription.of(
                                colorTargetFormat,
                                ColorTargetBlendState.disabled()
                        )
                )
        );
    }

    private static long getUsedMemoryMB() {
        var runtime = Runtime.getRuntime();
        var usedMemory = runtime.totalMemory() - runtime.freeMemory();
        return usedMemory / (1024 * 1024);
    }

}
