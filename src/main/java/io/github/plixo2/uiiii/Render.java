package io.github.plixo2.uiiii;

import io.github.plixo2.sodalite.category.gpu.*;
import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.awt.*;
import java.io.IOException;

public class Render {

    private final Matrix4f projection = new Matrix4f();

    @Getter
    private final Matrix4f transform = new Matrix4f();

    private final StagedScissorStack scissors = new StagedScissorStack();
    public final InstancedDraw draw;
    private final RectBatch rectBatch;

    public Render(
            Device device,
            ResourceSet resourceSet,
            TextureInfo colorTarget
    ) throws IOException {
        this.draw = new InstancedDraw(this.scissors);
        this.rectBatch = new RectBatch(resourceSet, device, colorTarget);
        this.draw.setAvailableBatches(this.rectBatch);
    }

    public void beginFrame(
            Matrix4f projection,
            int width,
            int height
    ) {
        this.projection.set(projection);

        this.transform.identity();
        this.scissors.reset(width, height);
    }

    public void drawRect(
            float x,
            float y,
            float x2,
            float y2,
            Vector4f color,
            float outline,
            Vector4f outlineColor,
            float roundness
    ) {
        var rect = this.rectBatch;
        this.draw.prepareDraw(rect);

        float width = x2 - x;
        float height = y2 - y;

        rect.addDraw();
        var buffer = rect.data();

        buffer.writeMatrix4f(this.transform)
              .writeVector4f(x, y, width, height)
              .writeVector4f(color)
              .writeVector4f(outlineColor)
              .writeFloat(roundness)
              .writeFloat(outline)
              .writeInt(0) // texture
              .writeInt(0); // isTexture
    }

    public void drawTexture(
            float x,
            float y,
            float x2,
            float y2,
            Vector4f color,
            float uv0x, float uv0y, float uv1x, float uv1y,
            Texture texture,
            Sampler sampler
    ) {
        var rect = this.rectBatch;
        var index = prepareDrawTexture(texture, sampler);

        float width = x2 - x;
        float height = y2 - y;

        rect.addDraw();
        var buffer = rect.data();

        buffer.writeMatrix4f(this.transform);
        buffer.writeVector4f(x, y, width, height);
        buffer.writeVector4f(color);

        buffer.writeVector4f(uv0x, uv0y, uv1x, uv1y);
        buffer.writeFloat(0f);
        buffer.writeFloat(0f);
        buffer.writeInt(index); // texture
        buffer.writeInt(1); // isTexture
    }

    public void drawTexture(
            float x,
            float y,
            float x2,
            float y2,
            Vector4f color,
            Texture texture,
            Sampler sampler
    ) {
        drawTexture(x, y, x2, y2, color, 0, 0, 1, 1, texture, sampler);
    }

    private int prepareDrawTexture(Texture texture, Sampler sampler) {
        var rect = this.rectBatch;
        this.draw.prepareDraw(rect);
        var index = rect.addTexture(texture, sampler);

        if (index == -1) {
            this.draw.endDraw();
            this.draw.prepareDraw(rect);
            index = rect.addTexture(texture, sampler);
            if (index == -1) {
                throw new IllegalStateException("Sampler array is full even after preparing new batch");
            }
        }

        return index;
    }

    public void upload(
            Device device,
            CopyPass copyPass
    ) {
        this.draw.upload(device, copyPass, this.projection);
    }

    public void renderFrame(
            RenderPass renderPass,
            CommandBuffer commandBuffer
    ) {
        this.draw.render(renderPass, commandBuffer);
    }

}
