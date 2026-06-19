package io.github.plixo2.sodalite.examples;

import io.github.plixo2.sodalite.category.events.EventConsumer;
import io.github.plixo2.sodalite.category.events.Events;
import io.github.plixo2.sodalite.category.gpu.*;
import io.github.plixo2.sodalite.category.init.Init;
import io.github.plixo2.sodalite.category.video.Video;
import io.github.plixo2.sodalite.category.video.Window;
import io.github.plixo2.sodalite.category.video.WindowFlags;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.joml.Vector4f;

/// A minimal gpu example
/// @see MainCallbacks for the version using the main callbacks instead of manual event polling
class MinimalGPU implements EventConsumer {
    boolean running = true;
    Window window;
    Device device;

    @Override
    public void onWindowCloseRequested(long timestamp, int windowID) {
        if (this.window.id() != windowID) {
            return;
        }
        this.running = false;
    }

    void init(ResourceSet appLifeResources) {
        // Create a window
        // The window will be automatically destroyed when `appLifeResources` is closed
        this.window = Video.createWindow(
                appLifeResources,
                "GPU Example",
                800, 600,
                WindowFlags.RESIZABLE
        );
        // Create a GPU device
        this.device = GPU.createDevice(
                appLifeResources,
                // shader formats your app supports
                ShaderFormat.SPIRV | ShaderFormat.DXIL | ShaderFormat.MSL,
                // enable debug mode (validation layers, debug messages, etc.)
                true,
                // the SDL choose the best driver
                GPUDriver.optimal()
        );
        var _ = this.device.claimWindow(this.window);
    }

    void run() {
        // Associate the window with the GPU device, so that we can render to it.
        // The try-with-resources block will automatically release the claim
        while (this.running) {
            Events.pollEvents(this);
            frame();
        }
    }

    void frame() {
        // Acquire a command buffer to record commands.
        // The try-with-resources block will submit the command buffer when it goes out of scope
        try (var commandBuffer = this.device.acquireCommandBuffer()) {
            // Acquire the next swapchain texture to render to
            var swapchain = commandBuffer.waitAndAcquireSwapchainTexture(this.window);
            // The swapchain texture may be null under certain conditions,
            // e.g. if the window is minimized
            if (swapchain == null) {
                return;
            }
            var clearColor = new Vector4f(0.12f, 0.15f, 0.2f, 1.0f);
            // Define a color target that clears the swapchain texture
            var colorTarget0 = RenderPass.ColorTargetInfo.clear(
                    swapchain,
                    clearColor,
                    Cycle.FALSE
            );
            // Begin a render pass with the specified color target and no depth target
            try (var renderPass = commandBuffer.beginRenderPass(null, colorTarget0)) {
                // just clear
            }
        }
    }

    void main() {
        // Provide metadata about your app
        // "This is not required, but strongly encouraged"
        Init.setAppMetaData("MinimalGPU", "0.0.1", "com.example.sodalite");

        // A ResourceSet manages resource lifetimes
        try (var appLifeResources = ResourceSet.ofConfined()) {
            init(appLifeResources);
            run();
        } finally {
            // `Init.quit()` will free resources registered to
            // ResourceSet.global() and any resources registered to
            // ResourceSet.ofAuto() that haven't been freed yet
            Init.quit();
        }
    }

}
