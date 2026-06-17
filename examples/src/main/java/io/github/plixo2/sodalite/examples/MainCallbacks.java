package io.github.plixo2.sodalite.examples;

import io.github.plixo2.sodalite.category.gpu.*;
import io.github.plixo2.sodalite.category.init.AppResult;
import io.github.plixo2.sodalite.category.main.Callbacks;
import io.github.plixo2.sodalite.category.init.Init;
import io.github.plixo2.sodalite.category.video.Video;
import io.github.plixo2.sodalite.category.video.Window;
import io.github.plixo2.sodalite.category.video.WindowFlags;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.joml.Vector4f;

/// The `main` method in [Callbacks] defines the main method
/// thanks to [Instance Main Methods](https://openjdk.org/jeps/445)
class MainCallbacks implements Callbacks {
    Window window;
    Device device;
    Device.WindowClaim claim;

    @Override
    public AppResult onWindowCloseRequested(long timestamp, int windowID) {
        if (this.window.id() != windowID) {
            return AppResult.CONTINUE;
        }
        return AppResult.SUCCESS;
    }

    @Override
    public AppResult init(String[] args) {
        Init.setAppMetaData("Sodalite App", "0.0.1", "com.example.sodalite");

        this.window = Video.createWindow(
                ResourceSet.global(),
                "GPU Demo",
                800, 600,
                WindowFlags.RESIZABLE
        );
        this.device = GPU.createDevice(
                ResourceSet.global(),
                ShaderFormat.SPIRV | ShaderFormat.DXIL | ShaderFormat.MSL,
                true,
                GPUDriver.optimal()
        );
        this.claim = this.device.claimWindow(this.window);

        return AppResult.CONTINUE;
    }

    @Override
    public AppResult iterate() {
        try (var commandBuffer = this.device.acquireCommandBuffer()) {
            var swapchain = commandBuffer.waitAndAcquireSwapchainTexture(this.window);
            if (swapchain == null) {
                return AppResult.CONTINUE;
            }
            var clearColor = new Vector4f(0.12f, 0.15f, 0.2f, 1.0f);
            var colorTarget0 = RenderPass.ColorTargetInfo.clear(
                    swapchain,
                    clearColor,
                    Cycle.FALSE
            );
            try (var renderPass = commandBuffer.beginRenderPass(null, colorTarget0)) {
                // just clear
            }
        }
        return AppResult.CONTINUE;
    }

    @Override
    public void quit(AppResult result) {
        if (this.claim != null) {
            this.claim.close();
        }
    }



}
