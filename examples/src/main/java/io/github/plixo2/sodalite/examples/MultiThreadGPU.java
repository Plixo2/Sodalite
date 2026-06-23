import io.github.plixo2.sodalite.category.events.EventConsumer;
import io.github.plixo2.sodalite.category.events.Events;
import io.github.plixo2.sodalite.category.gpu.*;
import io.github.plixo2.sodalite.category.init.Init;
import io.github.plixo2.sodalite.category.video.Video;
import io.github.plixo2.sodalite.category.video.Window;
import io.github.plixo2.sodalite.category.video.WindowFlags;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

/// A multi-thread version of [io.github.plixo2.sodalite.examples.MinimalGPU]
static class GpuWindowRenderer implements EventConsumer {
    Vector4f clearColor;
    Window window;
    Device device;
    volatile boolean running = true;

    GpuWindowRenderer(
            Vector4f clearColor,
            Window window,
            Device device
    ) {
        this.clearColor = clearColor;
        this.window = window;
        this.device = device;
    }

    /// Called from the main thread
    @Override
    public void onWindowCloseRequested(long timestamp, int windowID) {
        if (this.window.id().value() != windowID) {
            return;
        }
        this.running = false;
    }

    void frame() {
        try (var commandBuffer = this.device.acquireCommandBuffer()) {
            var swapchain = commandBuffer.waitAndAcquireSwapchainTexture(this.window);
            if (swapchain == null) {
                return;
            }

            try (var renderPass = commandBuffer.beginRenderPass(
                    null,
                    RenderPass.ColorTargetInfo.clear(swapchain, this.clearColor, Cycle.FALSE)
            )) {
                // just clear
            }
        }
    }

    void run() {
        try (var _ = this.device.claimWindow(this.window)) {
            while (this.running) {
                frame();
            }
        }
    }
}
record Session(
        GpuWindowRenderer instance,
        ResourceSet resources,
        Thread thread
) {

    static Session create(
            ResourceSet appLifeResources,
            Device device,
            Vector4f clearColor,
            String name
    ) {
        var resources = ResourceSet.ofConfined(appLifeResources);
        var window = Video.createWindow(
                resources,
                name,
                800, 600,
                WindowFlags.RESIZABLE
        );
        var renderer = new GpuWindowRenderer(clearColor, window, device);
        var thread = new Thread(() -> {
            renderer.run();
            System.out.println(name + " exited");
        }, name);
        return new Session(renderer, resources, thread);
    }

    /// @return true if the session has exited, false otherwise
    boolean checkExit() {
        if (!this.thread.isAlive()) {
            this.resources.close();
            return true;
        }
        return false;
    }
}

void main() throws InterruptedException {
    Init.setAppMetaData("GpuWindowRenderer", "0.0.1", "com.example.sodalite");

    try (var appLifeResources = ResourceSet.ofConfined()) {
        var device = GPU.createDevice(
                appLifeResources,
                ShaderFormat.SPIRV | ShaderFormat.DXIL | ShaderFormat.MSL,
                true,
                GPUDriver.optimal()
        );

        var colors = List.of(
                new Vector4f(0.1f, 0.2f, 0.3f, 1.0f),
                new Vector4f(0.3f, 0.1f, 0.2f, 1.0f),
                new Vector4f(0.2f, 0.3f, 0.1f, 1.0f)
        );

        var sessions = new ArrayList<Session>();
        for (int i = 0; i < colors.size(); i++) {
            sessions.add(Session.create(
                    appLifeResources,
                    device,
                    colors.get(i),
                    "Renderer-" + i
            ));
        }
        sessions.forEach(s -> s.thread().start());

        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

        var activeSessions = new ArrayList<>(sessions);
        while (!activeSessions.isEmpty()) {
            Events.pollEvents(activeSessions.stream().map(Session::instance).toList());

            activeSessions.removeIf(Session::checkExit);

            Thread.onSpinWait();
        }

        for (var session : sessions) {
            session.thread().join();
        }

    } finally {
        Init.quit();
    }
}