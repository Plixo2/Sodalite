package io.github.plixo2.uiiii;

import io.github.plixo2.sodalite.category.gpu.*;
import io.github.plixo2.sodalite.memory.ConstantWriteBuffer;
import io.github.plixo2.sodalite.memory.IntList;
import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector4i;

public abstract class DrawBatch {

    @Getter
    private final ConstantWriteBuffer data;
    private final TransferBuffer transferBuffer;
    protected final Buffer gpuBuffer;

    private int drawCount = 0;
    protected int replaySection = 0;

    private final IntList sections = new IntList();
    private final SoftStack<Vector4i> scissors = new SoftStack<>(Vector4i::new);


    public DrawBatch(ResourceSet resources, Device device, long capacity) {
        this.data = ConstantWriteBuffer.allocate(resources, capacity);
        this.gpuBuffer = device.createBuffer(
                resources,
                BufferUsageFlags.GRAPHICS_STORAGE_READ,
                capacity
        );
        this.transferBuffer = device.createTransferBuffer(
                resources,
                TransferBufferUsage.UPLOAD,
                capacity
        );
    }

    void endSection() {
        this.sections.add(this.drawCount);
    }

    protected void beginSection(Vector4i scissor) {
        this.scissors.push().set(scissor);
    }

    protected void addDraw() {
        this.drawCount++;
    }

    protected void reset() {
        this.drawCount = 0;
        this.replaySection = 0;
        this.data.reset();
        this.sections.clear();
        this.scissors.clear();
    }

    protected void upload(
            Device device,
            CopyPass copyPass,
            Matrix4f projection
    ) {
        var length = this.data.position();
        try (var mapped = device.mapTransferBuffer(this.transferBuffer, Cycle.TRUE)) {
            mapped.memory().copyFrom(this.data.memory().asSlice(0, length));
        }
        copyPass.upload(this.transferBuffer, this.gpuBuffer, length, Cycle.TRUE);
    }

    final void replaySection(
        RenderPass renderPass,
        CommandBuffer commandBuffer
    ) {
        int count = getCount();

        if (count != 0) {
            int startIndex = getStartIndex();
            var scissor = getScissor();

            applyScissor(renderPass, scissor);
            draw(renderPass, commandBuffer, startIndex, count);
        }
        this.replaySection++;
    }

    private Vector4i getScissor() {
        return this.scissors.get(this.replaySection);
    }


    private int getStartIndex() {
        if (this.replaySection == 0) {
            return 0;
        }
        return this.sections.get(this.replaySection - 1);
    }

    private int getCount() {
        return this.sections.get(this.replaySection) - getStartIndex();
    }

    private void applyScissor(RenderPass renderPass, Vector4i rect) {
        var wDiff = Math.max(rect.z - rect.x, 0);
        var hDiff = Math.max(rect.w - rect.y, 0);

        renderPass.setScissor(rect.x, rect.y, wDiff, hDiff);
    }

    abstract void draw(RenderPass renderPass, CommandBuffer commandBuffer, int startIndex, int count);


}
