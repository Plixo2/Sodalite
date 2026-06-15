package io.github.plixo2.sodalite.examples;

import io.github.plixo2.sodalite.category.events.EventConsumer;
import io.github.plixo2.sodalite.category.events.Events;
import io.github.plixo2.sodalite.category.init.Init;
import io.github.plixo2.sodalite.category.video.Video;
import io.github.plixo2.sodalite.category.video.Window;
import io.github.plixo2.sodalite.category.video.WindowFlags;
import io.github.plixo2.sodalite.resource.ResourceSet;

/// A minimal example that creates a window and listens for the close event.
/// This example doesn't do any rendering
class MinimalWindow implements EventConsumer {
    boolean running = true;
    Window window;

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
                "Window Demo",
                800, 600,
                WindowFlags.RESIZABLE
        );

    }

    void run() {
        while (this.running) {
            Events.pollEvents(this);
        }
    }

    void main() {
        // Provide metadata about your app
        // "This is not required, but strongly encouraged"
        Init.setAppMetaData("Sodalite App", "0.0.1", "com.example.sodalite");

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
