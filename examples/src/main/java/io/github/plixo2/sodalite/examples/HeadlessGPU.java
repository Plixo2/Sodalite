
import io.github.plixo2.sodalite.category.gpu.*;
import io.github.plixo2.sodalite.category.init.Init;
import io.github.plixo2.sodalite.category.init.InitFlags;
import io.github.plixo2.sodalite.category.timer.Timer;
import io.github.plixo2.sodalite.examples.GPUHelloTriangle;
import io.github.plixo2.sodalite.io.image.ImageChannels;
import io.github.plixo2.sodalite.io.image.ImageData;
import io.github.plixo2.sodalite.io.image.ImageFormat;
import io.github.plixo2.sodalite.io.image.ImageWriter;
import io.github.plixo2.sodalite.memory.*;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.joml.Vector4f;

import java.awt.*;
import java.io.IOException;
import java.lang.foreign.*;
import java.nio.file.Path;

///
/// Renders [GPUHelloTriangle] to an image and saves it to disk.
///
static StructLayout Vertex = MemoryLayout.structLayout(
        Layouts.FLOAT_3.withName("position"),
        Layouts.FLOAT_4.withName("color")
);

static StructLayout UniformBuffer = MemoryLayout.structLayout(
        Layouts.FLOAT.withName("time")
);
static Path IMAGE_PATH = Path.of("output.png");

void main() throws IOException {
    try {
        WriteBuffer<?> vertices = ConstantWriteBuffer.allocate(ResourceSet.global(), Vertex, 3)
             .writeFloats( 0.0f,  0.5f, 0.0f,   1.0f, 0.0f, 0.0f, 1.0f)
             .writeFloats(-0.5f, -0.5f, 0.0f,   1.0f, 1.0f, 0.0f, 1.0f)
             .writeFloats( 0.5f, -0.5f, 0.0f,   1.0f, 0.0f, 1.0f, 1.0f);

        CStruct timeUniform = CStruct.allocate(ResourceSet.global(), UniformBuffer);

        Init.ensureInit(InitFlags.VIDEO);

        var device = GPU.createDevice(
                ResourceSet.global(),
                ShaderFormat.SPIRV | ShaderFormat.DXIL,
                true,
                GPUDriver.optimal()
        );

        var target = TextureBuilder.of2D(
                TextureFormat.R8G8B8A8_UNORM,
                TextureUsageFlags.COLOR_TARGET,
                1024, 1024
        ).build(ResourceSet.global(), device);

        var useSpirv = device.supportsShaderFormat(ShaderFormat.SPIRV);
        var format = useSpirv ? ShaderFormat.SPIRV : ShaderFormat.DXIL;
        var ext = useSpirv ? "spv" : "dxil";
        var dir = useSpirv ? "spirv" : "dxil";

        var pipeline = device.createGraphicsPipeline(
            ResourceSet.global(),
            Shader.Creator.of(
                format,
                MemorySource.of(GPUHelloTriangle.class, "/GPUHelloTriangle/" + dir + "/vertex." + ext),
                Shader.Parameters.of(0, 0, 0, 0)
            ),
            Shader.Creator.of(
                format,
                MemorySource.of(GPUHelloTriangle.class, "/GPUHelloTriangle/" + dir + "/fragment." + ext),
                Shader.Parameters.of(0, 0, 0, 1)
            ),
            PrimitiveType.TRIANGLELIST,
            VertexInputState.of(0, Vertex, VertexInputState.Rate.VERTEX),
            RasterizerState.defaultValue(),
            MultisampleState.disabled(),
            DepthStencilState.disabled(),
            GraphicsPipelineTargetInfo.of(
                ColorTargetDescription.of(
                    target.format(),
                    ColorTargetBlendState.standardAlphaBlend()
                )
            )
        );

        var vertexBuffer = device.createBuffer(
                ResourceSet.global(),
                BufferUsageFlags.VERTEX,
                vertices.capacity()
        );

        try (var transferSet = ResourceSet.ofConfined()) {
            var transferBuffer = device.createTransferBuffer(
                    transferSet,
                    TransferBufferUsage.UPLOAD,
                    vertexBuffer.size()
            );
            try (var mapped = transferBuffer.map(device,Cycle.FALSE)) {
                mapped.memory().copyFrom(vertices.memory());
            }
            try (var commandBuffer = device.acquireCommandBuffer()) {
                try (var copyPass = commandBuffer.beginCopyPass()) {
                    copyPass.upload(transferBuffer, vertexBuffer, Cycle.FALSE);
                }
            }
        }


        try (var commandBuffer = device.acquireCommandBuffer()) {
            var clearR = (float)Math.random();
            var clearB = (float)Math.random();
            var clearG = (float)Math.random();

            var colorTarget0 = RenderPass.ColorTargetInfo.clear(
                    target,
                    new Vector4f(clearR, clearB, clearG, 1f),
                    Cycle.FALSE
            );
            timeUniform.at("time").writeFloat(0);
            try (var renderPass = commandBuffer.beginRenderPass(null, colorTarget0)) {
                renderPass.bindPipeline(pipeline);
                renderPass.bindVertexBuffer(0, vertexBuffer);
                commandBuffer.pushFragmentUniform(0, timeUniform);
                renderPass.drawPrimitives(3, 1, 0, 0);
            }
        }

        var downloadBuffer = device.createTransferBuffer(
                ResourceSet.global(),
                TransferBufferUsage.DOWNLOAD,
                target.format().calculateTextureSize(target)
        );

        var commandBuffer = device.acquireCommandBuffer();
        try {
            try (var copyPass = commandBuffer.beginCopyPass()) {
                copyPass.download(
                        downloadBuffer,
                        TextureRegion.ofFull2D(target)
                );
            }
        } catch (Exception e) {
            commandBuffer.cancel(); // always cancel or submit
            throw e;
        }

        // Wait for the download
        var pre = Timer.getTicksNS();
        commandBuffer.closeAndAcquireFence(ResourceSet.global()).await();
        var post = Timer.getTicksNS();
        System.out.println("Download took " + (post - pre) / (double)Timer.NS_PER_MS + " ms");


        try (var mapped = downloadBuffer.map(device, Cycle.FALSE)) {
            var memory = mapped.memory();

            ImageWriter.write(
                IMAGE_PATH,
                ImageFormat.PNG(),
                ImageData.of(
                    target.width(),
                    target.height(),
                    ImageChannels.RGBA,
                    memory
                )
            );
        }
        try {
            Thread.sleep(500); // lets give the OS a moment to finish writing the file before we try to open it
            Desktop.getDesktop().open(IMAGE_PATH.toFile());
        } catch(Exception e) {
            // ignore
        }

    } finally {
        Init.quit();
    }
}
