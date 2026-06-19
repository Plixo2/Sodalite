package io.github.plixo2.ui;

import io.github.plixo2.sodalite.category.gpu.CommandBuffer;
import io.github.plixo2.sodalite.category.gpu.CopyPass;
import io.github.plixo2.sodalite.category.gpu.Device;
import io.github.plixo2.sodalite.category.gpu.RenderPass;
import org.joml.Matrix4f;
import org.joml.Vector4i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InstancedDraw {

    private DrawBatch lastBatch;

    private DrawBatch[] allBatches = null;
    private final List<DrawBatch> sequence = new ArrayList<>();

    private final StagedScissorStack scissors;
    private final Vector4i currentScissor = new Vector4i();

    public InstancedDraw(StagedScissorStack scissors) {
        this.scissors = scissors;
    }

    protected void setAvailableBatches(DrawBatch... batchs) {
        this.allBatches = Arrays.copyOf(batchs, batchs.length);
    }


    public void endDraw() {
        if (this.lastBatch != null) {
            this.lastBatch.endSection();
            this.sequence.add(this.lastBatch);
            this.lastBatch = null;
        }
    }

    protected void prepareDraw(DrawBatch batch) {
        if (!shouldEndDraw(batch)) {
            return;
        }
        endDraw();
        batch.beginSection(this.currentScissor);
        this.lastBatch = batch;
    }

    protected boolean shouldEndDraw(DrawBatch batch) {
        var scissor = this.scissors.peek();
        if (this.lastBatch != batch) {
            this.currentScissor.set(scissor);
            return true;
        }
        if (!scissor.equals(this.currentScissor)) {
            this.currentScissor.set(scissor);
            return true;
        }
        return false;
    }

    private void reset() {
        this.sequence.clear();
        for (var batch : this.allBatches) {
            batch.reset();
        }
        this.lastBatch = null;
    }


    public void upload(
            Device device,
            CopyPass copyPass,
            Matrix4f projection
    ) {
        if (this.allBatches == null) {
            throw new IllegalStateException("Batchs not set");
        }
        endDraw();

        for (var batch : this.allBatches) {
            batch.upload(device, copyPass, projection);
        }

    }

    public void render(
            RenderPass renderPass,
            CommandBuffer commandBuffer
    ) {

        for (var batch : this.sequence) {
            batch.replaySection(renderPass, commandBuffer);
        }

        reset();
    }

}
