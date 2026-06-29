package io.github.plixo2.sodalite.category.video;


import io.github.plixo2.sodalite.SDLException;
import io.github.plixo2.sodalite.category.pixels.PixelFormat;
import io.github.plixo2.sodalite.category.properties.PropertyGroup;
import io.github.plixo2.sodalite.category.rect.Rect;
import io.github.plixo2.sodalite.category.surface.SurfaceObject;
import io.github.plixo2.sodalite.memory.ReadBuffer;
import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4i;

import java.lang.foreign.MemorySegment;
import java.util.List;

/// @sdlAPI SDL_Window
public class Window extends ResourceObject {
    private final MemorySegment segment;

    Window(
            ResourceSet resources,
            MemorySegment segment
    ) {
        resources.register(this, () -> Video.destroyWindow(segment));
        this.segment = segment;
    }

    private Window(
            MemorySegment segment
    ) {
        this.segment = segment;
    }

    public static Window newUnchecked(MemorySegment windowPointer) {
        return new Window(windowPointer);
    }

    public MemorySegment segment() {
        ensureNotReleased();
        return this.segment;
    }

    public void setIcon(SurfaceObject surface) throws SDLException {
        Video.setWindowIcon(this, surface);
    }

    public WindowID id() throws SDLException {
        return Video.getWindowID(this);
    }

    public void setTitle(String title) throws SDLException {
        Video.setWindowTitle(this, title);
    }

    public String getTitle() throws SDLException {
        return Video.getWindowTitle(this);
    }

    public @Nullable PropertyGroup getProperties() throws SDLException {
        return Video.getWindowProperties(this);
    }

    public @WindowFlags long getFlags() {
        return Video.getWindowFlags(this);
    }

    public @Nullable Window getParent() {
        return Video.getWindowParent(this);
    }

    public DisplayID getDisplay() throws SDLException {
        return Video.getDisplayForWindow(this);
    }

    public Vector2i getSize(Vector2i in) throws SDLException {
        return Video.getWindowSize(this, in);
    }

    public Vector2i getSizeInPixels(Vector2i in) throws SDLException {
        return Video.getWindowSizeInPixels(this, in);
    }

    public float getDisplayScale() {
        return Video.getWindowDisplayScale(this);
    }

    public float getPixelDensity() {
        return Video.getWindowPixelDensity(this);
    }


    public void setPosition(int x, int y) throws SDLException {
        Video.setWindowPosition(this, x, y);
    }

    public void setPosition(Vector2i position) throws SDLException {
        Video.setWindowPosition(this, position.x, position.y);
    }

    public Vector2i getPosition(Vector2i in) throws SDLException {
        return Video.getWindowPosition(this, in);
    }

    public void setSize(int width, int height) throws SDLException {
        Video.setWindowSize(this, width, height);
    }

    public void setSize(Vector2i size) throws SDLException {
        Video.setWindowSize(this, size.x, size.y);
    }

    public Rect getSafeArea() throws SDLException {
        return Video.getWindowSafeArea(this);
    }

    public void setAspectRatio(float minAspect, float maxAspect) throws SDLException {
        Video.setWindowAspectRatio(this, minAspect, maxAspect);
    }

    public Vector2f getAspectRatio(Vector2f in) throws SDLException {
        return Video.getWindowAspectRatio(this, in);
    }

    public Vector4i getBordersSize(Vector4i in) throws SDLException {
        return Video.getWindowBordersSize(this, in);
    }

    public void setMinimumSize(int width, int height) throws SDLException {
        Video.setWindowMinimumSize(this, width, height);
    }
    public void setMinimumSize(Vector2i size) throws SDLException {
        Video.setWindowMinimumSize(this, size.x, size.y);
    }
    public Vector2i getMinimumSize(Vector2i in) throws SDLException {
        return Video.getWindowMinimumSize(this, in);
    }

    public void setMaximumSize(int width, int height) throws SDLException {
        Video.setWindowMaximumSize(this, width, height);
    }
    public void setMaximumSize(Vector2i size) throws SDLException {
        Video.setWindowMaximumSize(this, size.x, size.y);
    }
    public Vector2i getMaximumSize(Vector2i in) throws SDLException {
        return Video.getWindowMaximumSize(this, in);
    }

    public void setBordered(boolean bordered) throws SDLException {
        Video.setWindowBordered(this, bordered);
    }

    public void setResizable(boolean resizable) throws SDLException {
        Video.setWindowResizable(this, resizable);
    }

    public void setAlwaysOnTop(boolean alwaysOnTop) throws SDLException {
        Video.setWindowAlwaysOnTop(this, alwaysOnTop);
    }

    public void setFullscreen(boolean fullscreen) throws SDLException {
        Video.setWindowFullscreen(this, fullscreen);
    }

    public void setFullscreenMode(@Nullable DisplayMode mode) throws SDLException {
        Video.setWindowFullscreenMode(this, mode);
    }
    public void setFullscreenBorderless() throws SDLException {
        Video.setWindowFullscreenMode(this, null);
    }

    public @Nullable DisplayMode getFullscreenMode() {
        return Video.getWindowFullscreenMode(this);
    }

    public ReadBuffer getICCProfile(ResourceSet resources) throws SDLException {
        return Video.getWindowICCProfile(resources, this);
    }

    public PixelFormat getPixelFormat() {
        return Video.getWindowPixelFormat(this);
    }

    public void show() throws SDLException {
        Video.showWindow(this);
    }

    public void hide() throws SDLException {
        Video.hideWindow(this);
    }

    public void raise() throws SDLException {
        Video.raiseWindow(this);
    }

    public void maximize() throws SDLException {
        Video.maximizeWindow(this);
    }

    public void minimize() throws SDLException {
        Video.minimizeWindow(this);
    }

    public void restore() throws SDLException {
        Video.restoreWindow(this);
    }

    public void setKeyboardGrab(boolean grabbed) throws SDLException {
        Video.setWindowKeyboardGrab(this, grabbed);
    }
    public boolean isKeyboardGrabbed() {
        return Video.getWindowKeyboardGrab(this);
    }

    public void setMouseGrab(boolean grabbed) throws SDLException {
        Video.setWindowMouseGrab(this, grabbed);
    }
    public boolean isMouseGrabbed() {
        return Video.getWindowMouseGrab(this);
    }
    public void setMouseRect(@Nullable Rect rect) throws SDLException {
        Video.setWindowMouseRect(this, rect);
    }
    public void removeMouseRect() throws SDLException {
        Video.setWindowMouseRect(this, null);
    }

    public @Nullable Rect getMouseRect() {
        return Video.getWindowMouseRect(this);
    }

    public void setOpacity(float opacity) throws SDLException {
        Video.setWindowOpacity(this, opacity);
    }
    public float getOpacity() {
        return Video.getWindowOpacity(this);
    }

    public void setParent(@Nullable Window parent) throws SDLException {
        Video.setWindowParent(this, parent);
    }

    public void setWindowModal(boolean modal) throws SDLException {
        Video.setWindowModal(this, modal);
    }

    public void setFocusable(boolean focusable) throws SDLException {
        Video.setWindowFocusable(this, focusable);
    }

    public void setShape(SurfaceObject surface) throws SDLException {
        Video.setWindowShape(this, surface);
    }

    public void flash(FlashOperation mode) throws SDLException {
        Video.flashWindow(this, mode);
    }

    public void flash() throws SDLException {
        Video.flashWindow(this, FlashOperation.BRIEFLY);
    }

    public void setProgressState(ProgressState state) throws SDLException {
        Video.setWindowProgressState(this, state);
    }
    public ProgressState getProgressState() {
        return Video.getWindowProgressState(this);
    }
    public void setProgressValue(float value) throws SDLException {
        Video.setWindowProgressValue(this, value);
    }
    public float getProgressValue() {
        return Video.getWindowProgressValue(this);
    }
    public boolean hasSurface() {
        return Video.windowHasSurface(this);
    }

    public SurfaceObject getSurface() throws SDLException {
        return Video.getWindowSurface(this);
    }
    public void setSurfaceVSync(int vSync) throws SDLException {
        Video.setWindowSurfaceVSync(this, vSync);
    }
    public void setSurfaceVSyncAdaptive() throws SDLException {
        Video.setWindowSurfaceVSync(this, -1);
    }
    public int getSurfaceVSync() throws SDLException {
        return Video.getWindowSurfaceVSync(this);
    }
    public void updateSurface() throws SDLException {
        Video.updateWindowSurface(this);
    }
    public void updateSurface(List<Rect> rects) throws SDLException {
        Video.updateWindowSurfaceRects(this, rects);
    }
    public void updateSurface(Rect... rects) throws SDLException {
        Video.updateWindowSurfaceRects(this, rects);
    }
    public void showSystemMenu(int x, int y) throws SDLException {
        Video.showWindowSystemMenu(this, x, y);
    }
    public void showSystemMenu(Vector2i position) throws SDLException {
        Video.showWindowSystemMenu(this, position.x, position.y);
    }
    public void sync() throws SDLException {
        Video.syncWindow(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Window other && this.segment.address() == other.segment.address();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.segment.address());
    }

    @Override
    public String toString() {
        return "Window{" +
                "segment=" + this.segment.address() +
                '}';
    }
}
