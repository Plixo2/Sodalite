package io.github.plixo2.sodalite.examples.gpu_examples;

import io.github.plixo2.sodalite.category.gpu.*;
import io.github.plixo2.sodalite.category.init.AppResult;
import io.github.plixo2.sodalite.category.keycode.Keycode;
import io.github.plixo2.sodalite.category.main.Callbacks;
import io.github.plixo2.sodalite.category.scancode.Scancode;
import io.github.plixo2.sodalite.category.video.Video;
import io.github.plixo2.sodalite.category.video.Window;
import io.github.plixo2.sodalite.category.video.WindowFlags;
import io.github.plixo2.sodalite.memory.Layouts;
import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;

public abstract class Common implements Callbacks {
    private final String name;
    private final @WindowFlags long windowFlags;


    Common(String name, @WindowFlags long windowFlags) {
        this.name = name;
        this.windowFlags = windowFlags;
    }

    // typedef struct PositionVertex
    // {
    //     float x, y, z;
    // } PositionVertex;
    static StructLayout PositionVertex = MemoryLayout.structLayout(
            Layouts.FLOAT_3.withName("position")
    );

    // typedef struct PositionColorVertex
    // {
    //     float x, y, z;
    //     Uint8 r, g, b, a;
    // } PositionColorVertex;
    static StructLayout PositionColorVertex = MemoryLayout.structLayout(
            Layouts.FLOAT_3.withName("position"),
            Layouts.UINT8_4.withName("color")
    );

    // typedef struct PositionTextureVertex
    // {
    //     float x, y, z;
    //     float u, v;
    // } PositionTextureVertex;
    static StructLayout PositionTextureVertex = MemoryLayout.structLayout(
            Layouts.FLOAT_3.withName("position"),
            Layouts.FLOAT_2.withName("uv")
    );

    @Getter
    private Window window;
    @Getter
    private Device device;
    private Device.WindowClaim claim;
    private @ShaderFormat int shaderFormat;

    private boolean leftPressed;
    private boolean rightPressed;
    private boolean downPressed;
    private boolean upPressed;

    protected void onLeftPressed() {}
    protected void onRightPressed() {}
    protected void onDownPressed() {}
    protected void onUpPressed() {}

    @Override
    public AppResult onKeyDown(
            long timestamp, int windowID, int keyboardID, Scancode scancode, Keycode key,
            int keymod, short raw, boolean repeat
    ) {
        if (windowID != this.window.id().value()) {
            return AppResult.CONTINUE;
        }
        if (key == Keycode.DOWN) {
            this.downPressed = true;
            onDownPressed();
        } else if (key == Keycode.UP) {
            this.upPressed = true;
            onUpPressed();
        } else if (key == Keycode.LEFT) {
            this.leftPressed = true;
            onLeftPressed();
        } else if (key == Keycode.RIGHT) {
            this.rightPressed = true;
            onRightPressed();
        }

        return AppResult.CONTINUE;
    }

    @Override
    public AppResult onKeyUp(
            long timestamp,
            int windowID,
            int keyboardID,
            Scancode scancode,
            Keycode key,
            int keymod,
            short raw
    ) {
        if (windowID != this.window.id().value()) {
            return AppResult.CONTINUE;
        }

        if (key == Keycode.DOWN) {
            this.downPressed = false;
        } else if (key == Keycode.UP) {
            this.upPressed = false;
        } else if (key == Keycode.LEFT) {
            this.leftPressed = false;
        } else if (key == Keycode.RIGHT) {
            this.rightPressed = false;
        }

        return AppResult.CONTINUE;
    }

    @Override
    public AppResult onWindowCloseRequested(long timestamp, int windowID) {
        if (windowID != this.window.id().value()) {
            return AppResult.CONTINUE;
        }
        return AppResult.SUCCESS;
    }

    public abstract void init() throws Exception;
    public abstract void update() throws Exception;
    public abstract void draw() throws Exception;
    public abstract void quit();

    @Override
    public AppResult init(String[] args) throws Exception {
        this.window = Video.createWindow(
                ResourceSet.global(),
                this.name,
                640, 480,
                this.windowFlags
        );
        var availableFormats = ShaderFormat.SPIRV | ShaderFormat.DXIL | ShaderFormat.MSL;
        this.device = GPU.createDevice(
                ResourceSet.global(),
                availableFormats,
                true,
                GPUDriver.optimal()
        );
        this.claim = this.device.claimWindow(this.window);

        // just pick any supported shader format
        //noinspection MagicConstant
        this.shaderFormat = Integer.lowestOneBit(
            this.device.getShaderFormats() & availableFormats
        );
        init();

        return AppResult.CONTINUE;
    }

    @Override
    public AppResult iterate() throws Exception {
        update();
        draw();
        return AppResult.CONTINUE;
    }

    @Override
    public void quit(AppResult result) {
        quit();
        if (this.claim != null) {
            this.claim.close();
        }
    }

    protected void upload(Buffer buffer, float... data) {
        try (var transferSet = ResourceSet.ofConfined()) {
            var transferBuffer = this.device.createTransferBuffer(
                    transferSet,
                    TransferBufferUsage.UPLOAD,
                    (long) data.length * Float.BYTES
            );
            try (var mapped = transferBuffer.map(this.device, Cycle.FALSE)) {
                MemorySegment.copy(
                        data,
                        0,
                        mapped.memory(),
                        ValueLayout.JAVA_FLOAT,
                        0,
                        data.length
                );
            }
            try (var commandBuffer = this.device.acquireCommandBuffer()) {
                try (var copyPass = commandBuffer.beginCopyPass()) {
                    copyPass.upload(transferBuffer, buffer, Cycle.FALSE);
                }
            }
        }
    }
    protected void upload(Buffer buffer, float[][] data) {
        var total = 0;

        for (var row : data) {
            total += row.length;
        }
        var buff = new float[total];

        var offset = 0;
        for (var row : data) {
            System.arraycopy(row, 0, buff, offset, row.length);
            offset += row.length;
        }
        upload(buffer, buff);
    }

    protected ComputePipeline computePipeline(
            String name,
            ComputeShader.ThreadCount threadCount,
            ComputeShader.Parameters parameters
    ) throws IOException {
        return this.device.createComputePipeline(
            ResourceSet.global(),
            ComputeShader.Creator.of(
                    this.shaderFormat,
                    shaderSrc(name),
                    threadCount,
                    parameters.withEntryPoint(entry())
            )
        );
    }

    protected Shader.Creator<IOException> shader(
            String name,
            int samplerCount,
            int uniformBufferCount,
            int storageBufferCount,
            int storageTextureCount
    ) {
        return Shader.Creator.of(
                this.shaderFormat,
                shaderSrc(name),
                Shader.Parameters.of(entry(), samplerCount, storageTextureCount, storageBufferCount, uniformBufferCount)
        );
    }

    private InputStream shaderSrc(
        String name
    ) {
        var ext = switch (this.shaderFormat) {
            case ShaderFormat.SPIRV -> "spv";
            case ShaderFormat.DXIL -> "dxil";
            case ShaderFormat.MSL -> "msl";
            default -> throw new IllegalStateException("Unrecognized backend shader format!");
        };
        var dir = switch (this.shaderFormat) {
            case ShaderFormat.SPIRV -> "SPIRV";
            case ShaderFormat.DXIL -> "DXIL";
            case ShaderFormat.MSL -> "MSL";
            default -> throw new IllegalStateException("Unrecognized backend shader format!");
        };
        var path = "/gpu_examples/Shaders/Compiled/" + dir + "/" + name + "." + ext;
        return Common.class.getResourceAsStream(path);
    }

    private String entry() {
        return switch (this.shaderFormat) {
            case ShaderFormat.SPIRV, ShaderFormat.DXIL -> "main";
            case ShaderFormat.MSL -> "main0";
            default -> throw new IllegalStateException("Unrecognized backend shader format!");
        };
    }

}
