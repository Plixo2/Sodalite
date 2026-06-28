package io.github.plixo2.sodalite.examples.gpu_examples;

import io.github.plixo2.sodalite.category.gpu.*;
import io.github.plixo2.sodalite.category.video.WindowFlags;
import io.github.plixo2.sodalite.memory.MemorySource;
import io.github.plixo2.sodalite.io.image.ImageLoader;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

import java.io.IOException;
import java.lang.foreign.MemorySegment;

/// CompressedTextures.c
public class CompressedTextures extends Common {
    CompressedTextures() {
        super("CompressedTextures", WindowFlags.NONE);
    }

    static TextureFormat[] textureFormats = new TextureFormat[]{
        // BCn formats
        TextureFormat.BC1_RGBA_UNORM,
        TextureFormat.BC2_RGBA_UNORM,
        TextureFormat.BC3_RGBA_UNORM,
        TextureFormat.BC4_R_UNORM,
        TextureFormat.BC5_RG_UNORM,
        TextureFormat.BC6H_RGB_FLOAT,
        TextureFormat.BC6H_RGB_UFLOAT,
        TextureFormat.BC7_RGBA_UNORM,
        TextureFormat.BC1_RGBA_UNORM_SRGB,
        TextureFormat.BC2_RGBA_UNORM_SRGB,
        TextureFormat.BC3_RGBA_UNORM_SRGB,
        TextureFormat.BC7_RGBA_UNORM_SRGB,

        TextureFormat.ASTC_4x4_UNORM,
        TextureFormat.ASTC_5x4_UNORM,
        TextureFormat.ASTC_5x5_UNORM,
        TextureFormat.ASTC_6x5_UNORM,
        TextureFormat.ASTC_6x6_UNORM,
        TextureFormat.ASTC_8x5_UNORM,
        TextureFormat.ASTC_8x6_UNORM,
        TextureFormat.ASTC_8x8_UNORM,
        TextureFormat.ASTC_10x5_UNORM,
        TextureFormat.ASTC_10x6_UNORM,
        TextureFormat.ASTC_10x8_UNORM,
        TextureFormat.ASTC_10x10_UNORM,
        TextureFormat.ASTC_12x10_UNORM,
        TextureFormat.ASTC_12x12_UNORM,
    };
    static String[] textureNames = new String[] {
            "bcn/BC1.dds",
            "bcn/BC2.dds",
            "bcn/BC3.dds",
            "bcn/BC4.dds",
            "bcn/BC5.dds",
            "bcn/BC6H_S.dds",
            "bcn/BC6H_U.dds",
            "bcn/BC7.dds",
            "bcn/BC1_SRGB.dds",
            "bcn/BC2_SRGB.dds",
            "bcn/BC3_SRGB.dds",
            "bcn/BC7_SRGB.dds",

            "astc/4x4.astc",
            "astc/5x4.astc",
            "astc/5x5.astc",
            "astc/6x5.astc",
            "astc/6x6.astc",
            "astc/8x5.astc",
            "astc/8x6.astc",
            "astc/8x8.astc",
            "astc/10x5.astc",
            "astc/10x6.astc",
            "astc/10x8.astc",
            "astc/10x10.astc",
            "astc/12x10.astc",
            "astc/12x12.astc",
    };
    record TextureSet(Texture src, Texture dest) {}
    @Nullable TextureSet[] textureSets = new TextureSet[textureFormats.length];
    int currentTextureIndex = 0;

    @Override
    public void init() throws Exception {
        if(textureFormats.length != textureNames.length) {
            throw new IllegalStateException("Texture formats and names arrays must be the same length");
        }

        TransferBuffer downloadTransferBuffer = null;
        ResourceSet firstTextureResources = null;
        MemorySegment firstTextureData = null;

        var commandBuffer = this.device().acquireCommandBuffer();

        boolean anySupported = false;
        boolean anyASTC = false;

        try {
            try (var copyPass = commandBuffer.beginCopyPass()) {
                for (int i = 0; i < textureFormats.length; i++) {
                    var format = textureFormats[i];
                    var name = textureNames[i];

                    if (!this.device().supportsTextureFormat(
                            format,
                            TextureType.TEXTURE_2D,
                            TextureUsageFlags.SAMPLER
                    )) {
                        this.textureSets[i] = null;
                        continue;
                    }
                    var isBC = name.startsWith("bcn/");
                    anySupported = true;
                    anyASTC = anyASTC || !isBC;

                    try (var imgData = ResourceSet.ofConfined()) {
                        int width;
                        int height;
                        MemorySegment data;
                        TextureFormat dataFormat;
                        if (isBC) {
                            var result = ImageLoader.loadDDS(
                                imgData.arena(),
                                textureSource(name)
                            ).orThrow(IOException::new);
                            data = result.data();
                            width = result.width();
                            height = result.height();
                            dataFormat = result.format();
                        } else {
                            var result = ImageLoader.loadASTC(
                                imgData.arena(),
                                textureSource(name)
                            ).orThrow(IOException::new);
                            data = result.data();
                            width = result.width();
                            height = result.height();
                            dataFormat = result.format().asUNORM();
                        }

                        if (dataFormat != format) {
                            throw new IllegalStateException("Texture format mismatch for " + name + ": expected " + format + " but got " + dataFormat);
                        }

                        var textureCreator = TextureBuilder.of2D(
                                format,
                                TextureUsageFlags.SAMPLER,
                                width,
                                height
                        );
                        var src = textureCreator.build(ResourceSet.global(), device());
                        var dest = textureCreator.build(ResourceSet.global(), device());

                        var translateBuffer = this.device().createTransferBuffer(
                                imgData,
                                TransferBufferUsage.UPLOAD,
                                data.byteSize()
                        );
                        try (var mapped = translateBuffer.map(this.device(),Cycle.FALSE)) {
                            mapped.memory().copyFrom(data);
                        }
                        copyPass.upload(
                            translateBuffer,
                            TextureRegion.ofFull2D(src),
                            Cycle.FALSE
                        );
                        copyPass.copy(
                                TextureLocation.of2D(src),
                                TextureLocation.of2D(dest),
                                256,
                                256,
                                1,
                                Cycle.FALSE
                        );

                        this.textureSets[i] = new TextureSet(src, dest);

                        if (firstTextureResources == null) {
                            firstTextureResources = ResourceSet.ofConfined();
                            downloadTransferBuffer = this.device().createTransferBuffer(
                                firstTextureResources,
                                TransferBufferUsage.DOWNLOAD,
                                data.byteSize()
                            );

                            copyPass.download(
                                downloadTransferBuffer,
                                TextureRegion.of2D(
                                        src,
                                        0,
                                        0, 0,
                                        256, 256
                                )
                            );

                            firstTextureData = firstTextureResources.allocate(data.byteSize());
                            firstTextureData.copyFrom(data);
                        }
                    }


                }
            }
        } catch (Exception e) {
            commandBuffer.cancel();
            if (firstTextureResources != null) {
                firstTextureResources.close();
            }
            throw e;
        }
        try {
            try (var c = ResourceSet.ofConfined()){
                var fence = commandBuffer.closeAndAcquireFence(c);
                fence.await();
            }
            if (downloadTransferBuffer != null) {
                try (var mapped = downloadTransferBuffer.map(this.device(), Cycle.FALSE)) {
                    var memory = mapped.memory();

                    if (memCompare(firstTextureData, memory)) {
                        System.out.println("Success: Downloaded bytes match original texture bytes!");
                    } else {
                        System.out.println("Failure: Downloaded bytes dont match original texture bytes!");
                    }
                }
            }

        } finally {
            if (firstTextureResources != null) {
                firstTextureResources.close();
            }
        }
        if (!anySupported) {
            System.out.println("No supported compressed texture formats found on this device.");
        } else if (!anyASTC) {
            System.out.println("ASTC textures are not supported on this device. This can be expected on modern hardware.");
        }

        System.out.println("Press Left/Right to switch between textures");
    }
    private MemorySource<IOException> textureSource(String name)  {
        var path = "/gpu_examples/Images/" + name;
        return MemorySource.of(CompressedTextures.class, path);
        //        var stream = CompressedTextures.class.getResourceAsStream(path);
//        if (stream == null) {
//            throw new IOException("Texture not found: " + path);
//        }
//        return stream;
    }
    private boolean memCompare(MemorySegment a, MemorySegment b) {
        return a.mismatch(b) == -1;
    }

    @Override
    protected void onLeftPressed() {
        this.currentTextureIndex -= 1;
        if (this.currentTextureIndex < 0) {
            this.currentTextureIndex = this.textureSets.length - 1;
        }
        if(this.textureSets[this.currentTextureIndex] == null) {
            System.out.println("Unsupported texture format: " + textureNames[this.currentTextureIndex]);
        } else {
            System.out.println("Setting texture to: " + textureNames[this.currentTextureIndex]);
        }

    }

    @Override
    protected void onRightPressed() {
        this.currentTextureIndex = (this.currentTextureIndex + 1) % this.textureSets.length;

        if(this.textureSets[this.currentTextureIndex] == null) {
            System.out.println("Unsupported texture format: " + textureNames[this.currentTextureIndex]);
        } else {
            System.out.println("Setting texture to: " + textureNames[this.currentTextureIndex]);
        }
    }

    @Override
    public void update() throws Exception {

    }

    @Override
    public void draw() throws Exception {
        try (var commandBuffer = this.device().acquireCommandBuffer()) {
            var swapchain = commandBuffer.waitAndAcquireSwapchainTexture(this.window());
            if (swapchain == null) {
                return;
            }
            var currentTextureSet = this.textureSets[this.currentTextureIndex];
            if (currentTextureSet != null) {
                commandBuffer.blit(
                        BlitInfo.of(
                                BlitInfo.Region.of(
                                        currentTextureSet.src,
                                        0,
                                        0, 0,
                                        256, 256
                                ),
                                BlitInfo.Region.of(
                                        swapchain,
                                        0,
                                        0, 0,
                                        256, 256
                                ),
                                new Vector4f(1f),
                                LoadOp.CLEAR,
                                Cycle.FALSE
                        )
                );
                commandBuffer.blit(
                        BlitInfo.of(
                                BlitInfo.Region.of(
                                        currentTextureSet.dest,
                                        0,
                                        0, 0,
                                        256, 256
                                ),
                                BlitInfo.Region.of(
                                        swapchain,
                                        0,
                                        384, 0,
                                        256, 256
                                ),
                                new Vector4f(0f),
                                LoadOp.LOAD,
                                Cycle.FALSE
                        )
                );

            } else {
                var colorTarget0 = RenderPass.ColorTargetInfo.clear(
                        swapchain,
                        new Vector4f(1f),
                        Cycle.FALSE
                );
                try (var renderPass = commandBuffer.beginRenderPass(null, colorTarget0)) {
                    // clear
                }
            }
        }
    }

    @Override
    public void quit() {

    }
}
