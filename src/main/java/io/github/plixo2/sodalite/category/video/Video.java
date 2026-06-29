package io.github.plixo2.sodalite.category.video;


import io.github.plixo2.sodalite.SDLException;
import io.github.plixo2.sodalite.category.pixels.PixelFormat;
import io.github.plixo2.sodalite.category.properties.PropertyGroup;
import io.github.plixo2.sodalite.category.rect.Rect;
import io.github.plixo2.sodalite.category.surface.SurfaceObject;
import io.github.plixo2.sodalite.memory.ReadBuffer;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4i;
import org.libsdl.sdl.SDL_DisplayMode;
import org.libsdl.sdl.SDL_Point;
import org.libsdl.sdl.SDL_Rect;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.ArrayList;
import java.util.List;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlCategory CategoryVideo
public class Video {
    private Video() {}

    /// Will implicitly initialize the video subsystem if needed
    /// (default `SDL_CreateWindow` behavior).
    ///
    /// @sdlAPI SDL_CreateWindow
    public static Window createWindow(
            ResourceSet resources,
            String title,
            int width,
            int height,
            @WindowFlags long flags
    ) throws SDLException {
        MemorySegment window;
        try (var arena = Arena.ofConfined()) {
            var titleCStr = arena.allocateFrom(title);
            window = check(SDL_CreateWindow(titleCStr, width, height, flags));
        }

        return new Window(resources, window);
    }

    /// @sdlAPI SDL_CreatePopupWindow
    public static Window createPopupWindow(
            ResourceSet resources,
            Window parent,
            int offsetX,
            int offsetY,
            int width,
            int height,
            @WindowFlags long flags
    ) throws SDLException {
        var isTooltop = (flags & WindowFlags.TOOLTIP) != 0;
        var isPopup = (flags & WindowFlags.POPUP_MENU) != 0;
        if (!isTooltop && !isPopup) {
            throw new IllegalArgumentException("Flags must include either WindowFlags.TOOLTIP or WindowFlags.POPUP_MENU");
        }

        var window = check(SDL_CreatePopupWindow(parent.segment(), offsetX, offsetY, width, height, flags));

        return new Window(resources, window);
    }

    /// @sdlAPI SDL_CreateWindowWithProperties
    public static Window createWindowWithProperties(
            ResourceSet resources,
            PropertyGroup props
    ) throws SDLException {
        var window = check(SDL_CreateWindowWithProperties(props.id()));
        return new Window(resources, window);
    }

    /// @sdlAPI SDL_GetVideoDriver
    /// @sdlOther SDL_GetNumVideoDrivers
    public static List<String> getVideoDrivers() throws SDLException {
        var count = SDL_GetNumVideoDrivers();
        var list = new ArrayList<String>(count);
        for (var i = 0; i < count; i++) {
            list.add(check(SDL_GetVideoDriver(i)).getString(0));
        }
        return List.copyOf(list);
    }

    /// @sdlAPI SDL_GetCurrentVideoDriver
    public static @Nullable String getCurrentVideoDriver() {
        return getNullString(SDL_GetCurrentVideoDriver());
    }

    /// @sdlAPI SDL_GetSystemTheme
    public static SystemTheme getSystemTheme() {
        return SystemTheme.fromCode(SDL_GetSystemTheme());
    }

    /// @sdlAPI SDL_GetDisplays
    public static List<DisplayID> getDisplays() throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var countSegment = arena.allocate(ValueLayout.JAVA_INT);
            var ptr = check(SDL_GetDisplays(countSegment));
            var count = countSegment.get(ValueLayout.JAVA_INT, 0);
            var displayIDs = ptr.reinterpret((long) count * ValueLayout.JAVA_INT.byteSize());
            try {
                var displays = new ArrayList<DisplayID>(count);
                for (var i = 0; i < count; i++) {
                    displays.add(DisplayID.of(displayIDs.getAtIndex(ValueLayout.JAVA_INT, i)));
                }
                return displays;
            } finally {
                SDL_free(ptr);
            }
        }
    }

    /// @sdlAPI SDL_GetPrimaryDisplay
    public static DisplayID getPrimaryDisplay() throws SDLException {
        return DisplayID.of(check(SDL_GetPrimaryDisplay()));
    }

    /// @sdlAPI SDL_GetDisplayProperties
    static PropertyGroup getDisplayProperties(
            DisplayID displayID
    ) throws SDLException {
        return PropertyGroup.newUnchecked(check(SDL_GetDisplayProperties(displayID.value())));
    }

    /// @sdlAPI SDL_GetDisplayName
    static String getDisplayName(
            DisplayID displayID
    ) throws SDLException {
        return check(SDL_GetDisplayName(displayID.value())).getString(0);
    }

    /// @sdlAPI SDL_GetDisplayBounds
    static Rect getDisplayBounds(
            DisplayID displayID
    ) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var rect = SDL_Rect.allocate(arena);
            check(SDL_GetDisplayBounds(displayID.value(), rect));
            return getRect(rect);
        }
    }

    /// @sdlAPI SDL_GetDisplayUsableBounds
    static Rect getDisplayUsableBounds(
            DisplayID displayID
    ) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var rect = SDL_Rect.allocate(arena);
            check(SDL_GetDisplayUsableBounds(displayID.value(), rect));
            return getRect(rect);
        }
    }

    /// @sdlAPI SDL_GetNaturalDisplayOrientation
    static DisplayOrientation getNaturalDisplayOrientation(
            DisplayID displayID
    ) {
        return DisplayOrientation.fromCode(SDL_GetNaturalDisplayOrientation(displayID.value()));
    }

    /// @sdlAPI SDL_GetCurrentDisplayOrientation
    static DisplayOrientation getCurrentDisplayOrientation(
            DisplayID displayID
    ) {
        return DisplayOrientation.fromCode(SDL_GetCurrentDisplayOrientation(displayID.value()));
    }

    /// @sdlAPI SDL_GetDisplayContentScale
    static float getDisplayContentScale(
            DisplayID displayID
    ) {
        return SDL_GetDisplayContentScale(displayID.value());
    }

    /// @sdlAPI SDL_GetFullscreenDisplayModes
    static List<DisplayMode> getFullscreenDisplayModes(
            DisplayID displayID
    ) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var countSegment = arena.allocate(ValueLayout.JAVA_INT);
            var ptr = check(SDL_GetFullscreenDisplayModes(displayID.value(), countSegment));
            var count = countSegment.get(ValueLayout.JAVA_INT, 0);
            var modesSegment = ptr.reinterpret((long) count * ValueLayout.ADDRESS.byteSize());
            try {
                var modes = new ArrayList<DisplayMode>(count);
                for (var i = 0; i < count; i++) {
                    var mode = modesSegment.getAtIndex(ValueLayout.ADDRESS, i);
                    modes.add(DisplayMode.of(mode.reinterpret(SDL_DisplayMode.sizeof())));
                }
                return modes;
            } finally {
                SDL_free(ptr);
            }
        }
    }

    /// @sdlAPI SDL_GetClosestFullscreenDisplayMode
    static DisplayMode getClosestFullscreenDisplayMode(
            DisplayID displayID,
            int width,
            int height,
            float refreshRate,
            boolean includeHighDensityModes
    ) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var mode = SDL_DisplayMode.allocate(arena);
            check(SDL_GetClosestFullscreenDisplayMode(
                    displayID.value(),
                    width,
                    height,
                    refreshRate,
                    includeHighDensityModes,
                    mode
            ));
            return DisplayMode.of(mode);
        }
    }

    /// @sdlAPI SDL_GetDesktopDisplayMode
    static DisplayMode getDesktopDisplayMode(
            DisplayID displayID
    ) throws SDLException {
        return DisplayMode.of(check(SDL_GetDesktopDisplayMode(displayID.value())).reinterpret(SDL_DisplayMode.sizeof()));
    }

    /// @sdlAPI SDL_GetCurrentDisplayMode
    static DisplayMode getCurrentDisplayMode(
            DisplayID displayID
    ) throws SDLException {
        return DisplayMode.of(check(SDL_GetCurrentDisplayMode(displayID.value())).reinterpret(SDL_DisplayMode.sizeof()));
    }

    /// @sdlAPI SDL_GetDisplayForPoint
    public static @Nullable DisplayID getDisplayForPoint(
            Vector2i point
    ) {
        return getDisplayForPoint(point.x, point.y);
    }

    /// @sdlAPI SDL_GetDisplayForPoint
    public static @Nullable DisplayID getDisplayForPoint(
            int x,
            int y
    ) {
        try (var arena = Arena.ofConfined()) {
            var point = SDL_Point.allocate(arena);
            SDL_Point.initialize(point, x, y);
            var value = SDL_GetDisplayForPoint(point);
            if (value == 0) {
                return null;
            }
            return DisplayID.of(value);
        }
    }

    /// @sdlAPI SDL_GetDisplayForRect
    public static @Nullable DisplayID getDisplayForRect(
            Rect rect
    ) {
        try (var arena = Arena.ofConfined()) {
            var value = SDL_GetDisplayForRect(putRect(arena, rect));
            if (value == 0) {
                return null;
            }
            return DisplayID.of(value);
        }
    }

    /// @sdlAPI SDL_GetDisplayForWindow
    static DisplayID getDisplayForWindow(
            Window window
    ) throws SDLException {
        return DisplayID.of(check(SDL_GetDisplayForWindow(window.segment())));
    }

    /// @sdlAPI SDL_GetWindowID
    static WindowID getWindowID(Window window) throws SDLException {
        var value = check(SDL_GetWindowID(window.segment()));
        return WindowID.of(value);
    }

    /// @sdlAPI SDL_GetWindowFromID
    static @Nullable Window getWindowFromID(
            WindowID id
    ) {
        return wrapWindow(SDL_GetWindowFromID(id.value()));
    }

    /// @sdlAPI SDL_GetWindowParent
    static @Nullable Window getWindowParent(
            Window window
    ) {
        return wrapWindow(SDL_GetWindowParent(window.segment()));
    }

    /// @sdlAPI SDL_GetWindowProperties
    static PropertyGroup getWindowProperties(
            Window window
    ) throws SDLException {
        return PropertyGroup.newUnchecked(check(SDL_GetWindowProperties(window.segment())));
    }

    /// @sdlAPI SDL_GetWindowFlags
    static @WindowFlags long getWindowFlags(
            Window window
    ) {
        //noinspection MagicConstant
        return SDL_GetWindowFlags(window.segment());
    }

    /// @sdlAPI SDL_GetWindowTitle
    static String getWindowTitle(
            Window window
    ) throws SDLException {
        return check(SDL_GetWindowTitle(window.segment())).getString(0);
    }

    /// @sdlAPI SDL_DestroyWindow
    static void destroyWindow(MemorySegment window) {
        SDL_DestroyWindow(window);
    }

    /// @sdlAPI SDL_SetWindowTitle
    static void setWindowTitle(Window window, String title) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var titleCStr = arena.allocateFrom(title);
            check(SDL_SetWindowTitle(window.segment(), titleCStr));
        }
    }

    /// @sdlAPI SDL_SetWindowIcon
    static void setWindowIcon(
            Window window,
            SurfaceObject icon
    ) throws SDLException {
        check(SDL_SetWindowIcon(window.segment(), icon.segment()));
    }


    /// @sdlAPI SDL_SetWindowPosition
    static void setWindowPosition(
            Window window,
            int x,
            int y
    ) throws SDLException {
        check(SDL_SetWindowPosition(window.segment(), x, y));
    }

    /// @sdlAPI SDL_GetWindowPosition
    static Vector2i getWindowPosition(
            Window window,
            Vector2i in
    ) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var x = arena.allocate(ValueLayout.JAVA_INT);
            var y = arena.allocate(ValueLayout.JAVA_INT);
            check(SDL_GetWindowPosition(window.segment(), x, y));
            in.set(x.get(ValueLayout.JAVA_INT, 0), y.get(ValueLayout.JAVA_INT, 0));
            return in;
        }
    }

    /// @sdlAPI SDL_SetWindowSize
    static void setWindowSize(
            Window window,
            int width,
            int height
    ) throws SDLException {
        check(SDL_SetWindowSize(window.segment(), width, height));
    }

    /// @sdlAPI SDL_GetWindowSize
    static Vector2i getWindowSize(Window window, Vector2i in) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var w = arena.allocate(ValueLayout.JAVA_INT);
            var h = arena.allocate(ValueLayout.JAVA_INT);
            check(SDL_GetWindowSize(window.segment(), w, h));
            in.set(w.get(ValueLayout.JAVA_INT, 0), h.get(ValueLayout.JAVA_INT, 0));
            return in;
        }
    }

    /// @sdlAPI SDL_GetWindowSafeArea
    static Rect getWindowSafeArea(
            Window window
    ) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var rect = SDL_Rect.allocate(arena);
            check(SDL_GetWindowSafeArea(window.segment(), rect));
            return getRect(rect);
        }
    }

    /// @sdlAPI SDL_SetWindowAspectRatio
    static void setWindowAspectRatio(
            Window window,
            float minAspect,
            float maxAspect
    ) throws SDLException {
        check(SDL_SetWindowAspectRatio(window.segment(), minAspect, maxAspect));
    }

    /// @sdlAPI SDL_GetWindowAspectRatio
    static Vector2f getWindowAspectRatio(
            Window window,
            Vector2f in
    ) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var minAspect = arena.allocate(ValueLayout.JAVA_FLOAT);
            var maxAspect = arena.allocate(ValueLayout.JAVA_FLOAT);
            check(SDL_GetWindowAspectRatio(window.segment(), minAspect, maxAspect));
            in.set(
                    minAspect.get(ValueLayout.JAVA_FLOAT, 0),
                    maxAspect.get(ValueLayout.JAVA_FLOAT, 0)
            );
            return in;
        }
    }

    /// @sdlAPI SDL_GetWindowBordersSize
    static Vector4i getWindowBordersSize(
            Window window,
            Vector4i in
    ) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var top = arena.allocate(ValueLayout.JAVA_INT);
            var left = arena.allocate(ValueLayout.JAVA_INT);
            var bottom = arena.allocate(ValueLayout.JAVA_INT);
            var right = arena.allocate(ValueLayout.JAVA_INT);
            check(SDL_GetWindowBordersSize(window.segment(), top, left, bottom, right));
            in.set(
                    top.get(ValueLayout.JAVA_INT, 0),
                    left.get(ValueLayout.JAVA_INT, 0),
                    bottom.get(ValueLayout.JAVA_INT, 0),
                    right.get(ValueLayout.JAVA_INT, 0)
            );
            return in;
        }
    }

    /// @sdlAPI SDL_GetWindowSizeInPixels
    static Vector2i getWindowSizeInPixels(Window window, Vector2i in) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var w = arena.allocate(ValueLayout.JAVA_INT);
            var h = arena.allocate(ValueLayout.JAVA_INT);
            check(SDL_GetWindowSizeInPixels(window.segment(), w, h));
            in.set(w.get(ValueLayout.JAVA_INT, 0), h.get(ValueLayout.JAVA_INT, 0));
            return in;
        }
    }

    /// @sdlAPI SDL_SetWindowMinimumSize
    static void setWindowMinimumSize(
            Window window,
            int width,
            int height
    ) throws SDLException {
        check(SDL_SetWindowMinimumSize(window.segment(), width, height));
    }

    /// @sdlAPI SDL_GetWindowMinimumSize
    static Vector2i getWindowMinimumSize(
            Window window,
            Vector2i in
    ) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var width = arena.allocate(ValueLayout.JAVA_INT);
            var height = arena.allocate(ValueLayout.JAVA_INT);
            check(SDL_GetWindowMinimumSize(window.segment(), width, height));
            in.set(width.get(ValueLayout.JAVA_INT, 0), height.get(ValueLayout.JAVA_INT, 0));
            return in;
        }
    }

    /// @sdlAPI SDL_SetWindowMaximumSize
    static void setWindowMaximumSize(
            Window window,
            int width,
            int height
    ) throws SDLException {
        check(SDL_SetWindowMaximumSize(window.segment(), width, height));
    }

    /// @sdlAPI SDL_GetWindowMaximumSize
    static Vector2i getWindowMaximumSize(
            Window window,
            Vector2i in
    ) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var width = arena.allocate(ValueLayout.JAVA_INT);
            var height = arena.allocate(ValueLayout.JAVA_INT);
            check(SDL_GetWindowMaximumSize(window.segment(), width, height));
            in.set(width.get(ValueLayout.JAVA_INT, 0), height.get(ValueLayout.JAVA_INT, 0));
            return in;
        }
    }

    /// @sdlAPI SDL_SetWindowBordered
    static void setWindowBordered(
            Window window,
            boolean bordered
    ) throws SDLException {
        check(SDL_SetWindowBordered(window.segment(), bordered));
    }

    /// @sdlAPI SDL_SetWindowResizable
    static void setWindowResizable(
            Window window,
            boolean resizable
    ) throws SDLException {
        check(SDL_SetWindowResizable(window.segment(), resizable));
    }

    /// @sdlAPI SDL_SetWindowAlwaysOnTop
    static void setWindowAlwaysOnTop(
            Window window,
            boolean onTop
    ) throws SDLException {
        check(SDL_SetWindowAlwaysOnTop(window.segment(), onTop));
    }

    /// @sdlAPI SDL_SetWindowFullscreen
    public static void setWindowFullscreen(
            Window window,
            boolean fullscreen
    ) throws SDLException {
        check(SDL_SetWindowFullscreen(window.segment(), fullscreen));
    }

    /// @sdlAPI SDL_GetWindowDisplayScale
    static float getWindowDisplayScale(Window window) {
        return SDL_GetWindowDisplayScale(window.segment());
    }

    /// @sdlAPI SDL_GetWindowPixelDensity
    static float getWindowPixelDensity(
            Window window
    ) {
        return SDL_GetWindowPixelDensity(window.segment());
    }

    /// @sdlAPI SDL_SetWindowFullscreenMode
    static void setWindowFullscreenMode(
            Window window,
            @Nullable DisplayMode mode
    ) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var segment = mode == null ? MemorySegment.NULL : mode.put(arena);
            check(SDL_SetWindowFullscreenMode(window.segment(), segment));
        }
    }

    /// @sdlAPI SDL_GetWindowFullscreenMode
    static @Nullable DisplayMode getWindowFullscreenMode(
            Window window
    ) {
        var mode = SDL_GetWindowFullscreenMode(window.segment());
        if (mode.address() == 0) {
            return null;
        }
        return DisplayMode.of(mode.reinterpret(SDL_DisplayMode.sizeof()));
    }

    /// @sdlAPI SDL_GetWindowICCProfile
    static ReadBuffer getWindowICCProfile(
            ResourceSet resources,
            Window window
    ) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var sizeSegment = arena.allocate(ValueLayout.JAVA_LONG);
            var ptr = check(SDL_GetWindowICCProfile(window.segment(), sizeSegment));
            var size = sizeSegment.get(ValueLayout.JAVA_LONG, 0);
            try {
                var segment = resources.allocate(size);
                segment.copyFrom(ptr.reinterpret(size));
                return ReadBuffer.of(resources, segment);
            } finally {
                SDL_free(ptr);
            }
        }
    }

    /// @sdlAPI SDL_GetWindowPixelFormat
    static PixelFormat getWindowPixelFormat(
            Window window
    ) {
        return PixelFormat.fromCode(SDL_GetWindowPixelFormat(window.segment()));
    }

    /// @sdlAPI SDL_GetWindows
    public static List<Window> getWindows() throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var countSegment = arena.allocate(ValueLayout.JAVA_INT);
            var ptr = check(SDL_GetWindows(countSegment));
            var count = countSegment.get(ValueLayout.JAVA_INT, 0);
            var windowsSegment = ptr.reinterpret((long) count * ValueLayout.ADDRESS.byteSize());
            try {
                var windows = new ArrayList<Window>(count);
                for (var i = 0; i < count; i++) {
                    windows.add(Window.newUnchecked(windowsSegment.getAtIndex(ValueLayout.ADDRESS, i)));
                }
                return windows;
            } finally {
                SDL_free(ptr);
            }
        }
    }

    /// @sdlAPI SDL_ShowWindow
    static void showWindow(
            Window window
    ) throws SDLException {
        check(SDL_ShowWindow(window.segment()));
    }

    /// @sdlAPI SDL_HideWindow
    static void hideWindow(
            Window window
    ) throws SDLException {
        check(SDL_HideWindow(window.segment()));
    }

    /// @sdlAPI SDL_RaiseWindow
    static void raiseWindow(
            Window window
    ) throws SDLException {
        check(SDL_RaiseWindow(window.segment()));
    }

    /// @sdlAPI SDL_MaximizeWindow
    static void maximizeWindow(
            Window window
    ) throws SDLException {
        check(SDL_MaximizeWindow(window.segment()));
    }

    /// @sdlAPI SDL_MinimizeWindow
    static void minimizeWindow(
            Window window
    ) throws SDLException {
        check(SDL_MinimizeWindow(window.segment()));
    }

    /// @sdlAPI SDL_RestoreWindow
    public static void restoreWindow(
            Window window
    ) throws SDLException {
        check(SDL_RestoreWindow(window.segment()));
    }

    /// @sdlAPI SDL_SetWindowKeyboardGrab
    static void setWindowKeyboardGrab(
            Window window,
            boolean grabbed
    ) throws SDLException {
        check(SDL_SetWindowKeyboardGrab(window.segment(), grabbed));
    }

    /// @sdlAPI SDL_SetWindowMouseGrab
    static void setWindowMouseGrab(
            Window window,
            boolean grabbed
    ) throws SDLException {
        check(SDL_SetWindowMouseGrab(window.segment(), grabbed));
    }

    /// @sdlAPI SDL_GetWindowKeyboardGrab
    static boolean getWindowKeyboardGrab(
            Window window
    ) {
        return SDL_GetWindowKeyboardGrab(window.segment());
    }

    /// @sdlAPI SDL_GetWindowMouseGrab
    static boolean getWindowMouseGrab(
            Window window
    ) {
        return SDL_GetWindowMouseGrab(window.segment());
    }

    /// @sdlAPI SDL_GetGrabbedWindow
    public static @Nullable Window getGrabbedWindow() {
        return wrapWindow(SDL_GetGrabbedWindow());
    }

    /// @sdlAPI SDL_SetWindowMouseRect
    static void setWindowMouseRect(
            Window window,
            @Nullable Rect rect
    ) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var segment = rect == null ? MemorySegment.NULL : putRect(arena, rect);
            check(SDL_SetWindowMouseRect(window.segment(), segment));
        }
    }

    /// @sdlAPI SDL_GetWindowMouseRect
    static @Nullable Rect getWindowMouseRect(
            Window window
    ) {
        var rect = SDL_GetWindowMouseRect(window.segment());
        if (rect.address() == 0) {
            return null;
        }
        return getRect(rect.reinterpret(SDL_Rect.sizeof()));
    }

    /// @sdlAPI SDL_SetWindowOpacity
    static void setWindowOpacity(
            Window window,
            float opacity
    ) throws SDLException {
        check(SDL_SetWindowOpacity(window.segment(), opacity));
    }

    /// @sdlAPI SDL_GetWindowOpacity
    static float getWindowOpacity(
            Window window
    ) {
        return SDL_GetWindowOpacity(window.segment());
    }

    /// @sdlAPI SDL_SetWindowParent
    static void setWindowParent(
            Window window,
            @Nullable Window parent
    ) throws SDLException {
        var segment = parent == null ? MemorySegment.NULL : parent.segment();
        check(SDL_SetWindowParent(window.segment(), segment));
    }

    /// @sdlAPI SDL_SetWindowModal
    static void setWindowModal(
            Window window,
            boolean modal
    ) throws SDLException {
        check(SDL_SetWindowModal(window.segment(), modal));
    }

    /// @sdlAPI SDL_SetWindowFocusable
    static void setWindowFocusable(
            Window window,
            boolean focusable
    ) throws SDLException {
        check(SDL_SetWindowFocusable(window.segment(), focusable));
    }

    /// @sdlAPI SDL_SetWindowShape
    static void setWindowShape(
            Window window,
            @Nullable SurfaceObject shape
    ) throws SDLException {
        check(SDL_SetWindowShape(window.segment(), shape == null ? MemorySegment.NULL : shape.segment()));
    }

    /// @sdlAPI SDL_FlashWindow
    static void flashWindow(
            Window window,
            FlashOperation operation
    ) throws SDLException {
        check(SDL_FlashWindow(window.segment(), operation.code()));
    }

    /// @sdlAPI SDL_SetWindowProgressState
    static void setWindowProgressState(
            Window window,
            ProgressState state
    ) throws SDLException {
        check(SDL_SetWindowProgressState(window.segment(), state.code()));
    }

    /// @sdlAPI SDL_GetWindowProgressState
    static ProgressState getWindowProgressState(
            Window window
    ) {
        return ProgressState.fromCode(SDL_GetWindowProgressState(window.segment()));
    }

    /// @sdlAPI SDL_SetWindowProgressValue
    static void setWindowProgressValue(
            Window window,
            float value
    ) throws SDLException {
        check(SDL_SetWindowProgressValue(window.segment(), value));
    }

    /// @sdlAPI SDL_GetWindowProgressValue
    static float getWindowProgressValue(
            Window window
    ) {
        return SDL_GetWindowProgressValue(window.segment());
    }

    /// @sdlAPI SDL_WindowHasSurface
    static boolean windowHasSurface(
            Window window
    ) {
        return SDL_WindowHasSurface(window.segment());
    }

    /// @sdlAPI SDL_GetWindowSurface
    static SurfaceObject getWindowSurface(
            Window window
    ) throws SDLException {
        var surface = check(SDL_GetWindowSurface(window.segment()));
        return SurfaceObject.newUnchecked(surface);
    }

    /// @sdlAPI SDL_SetWindowSurfaceVSync
    static void setWindowSurfaceVSync(
            Window window,
            int vsync
    ) throws SDLException {
        check(SDL_SetWindowSurfaceVSync(window.segment(), vsync));
    }

    /// @sdlAPI SDL_GetWindowSurfaceVSync
    static int getWindowSurfaceVSync(
            Window window
    ) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var vsync = arena.allocate(ValueLayout.JAVA_INT);
            check(SDL_GetWindowSurfaceVSync(window.segment(), vsync));
            return vsync.get(ValueLayout.JAVA_INT, 0);
        }
    }

    /// @sdlAPI SDL_UpdateWindowSurface
    static void updateWindowSurface(
            Window window
    ) throws SDLException {
        check(SDL_UpdateWindowSurface(window.segment()));
    }

    /// @sdlAPI SDL_UpdateWindowSurfaceRects
    static void updateWindowSurfaceRects(
            Window window,
            List<Rect> rects
    ) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var rectsSegment = SDL_Rect.allocateArray(rects.size(), arena);
            for (var i = 0; i < rects.size(); i++) {
                putRect(SDL_Rect.asSlice(rectsSegment, i), rects.get(i));
            }
            check(SDL_UpdateWindowSurfaceRects(window.segment(), rectsSegment, rects.size()));
        }
    }

    /// @sdlAPI SDL_UpdateWindowSurfaceRects
    static void updateWindowSurfaceRects(
            Window window,
            Rect... rects
    ) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var rectsSegment = SDL_Rect.allocateArray(rects.length, arena);
            for (var i = 0; i < rects.length; i++) {
                putRect(SDL_Rect.asSlice(rectsSegment, i), rects[i]);
            }
            check(SDL_UpdateWindowSurfaceRects(window.segment(), rectsSegment, rects.length));
        }
    }

    /// @sdlAPI SDL_DestroyWindowSurface
    public static void destroyWindowSurface(
            Window window
    ) throws SDLException {
        check(SDL_DestroyWindowSurface(window.segment()));
    }

    /// @sdlAPI SDL_ShowWindowSystemMenu
    static void showWindowSystemMenu(
            Window window,
            int x,
            int y
    ) throws SDLException {
        check(SDL_ShowWindowSystemMenu(window.segment(), x, y));
    }

    /// @sdlAPI SDL_SyncWindow
    static void syncWindow(
            Window window
    ) throws SDLException {
        check(SDL_SyncWindow(window.segment()));
    }

    /// @sdlAPI SDL_ScreenSaverEnabled
    public static boolean isScreenSaverEnabled() {
        return SDL_ScreenSaverEnabled();
    }

    /// @sdlAPI SDL_EnableScreenSaver
    public static void enableScreenSaver() throws SDLException {
        check(SDL_EnableScreenSaver());
    }

    /// @sdlAPI SDL_DisableScreenSaver
    public static void disableScreenSaver() throws SDLException {
        check(SDL_DisableScreenSaver());
    }

    /// @sdlAPI SDL_GL_LoadLibrary
    public static void glLoadLibrary(
            @Nullable String path
    ) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            check(SDL_GL_LoadLibrary(allocNullString(arena, path)));
        }
    }

    /// @sdlAPI SDL_GL_GetProcAddress
    public static MemorySegment glGetProcAddress(
            String proc
    ) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            return check(SDL_GL_GetProcAddress(arena.allocateFrom(proc)));
        }
    }

    /// @sdlAPI SDL_GL_UnloadLibrary
    public static void glUnloadLibrary() {
        SDL_GL_UnloadLibrary();
    }

    /// @sdlAPI SDL_GL_ExtensionSupported
    public static boolean glExtensionSupported(
            String extension
    ) {
        try (var arena = Arena.ofConfined()) {
            return SDL_GL_ExtensionSupported(arena.allocateFrom(extension));
        }
    }

    /// @sdlAPI SDL_GL_ResetAttributes
    public static void glResetAttributes() {
        SDL_GL_ResetAttributes();
    }

    /// @sdlAPI SDL_GL_SetAttribute
    public static void glSetAttribute(
            GLAttr attr,
            int value
    ) throws SDLException {
        check(SDL_GL_SetAttribute(attr.code(), value));
    }

    /// @sdlAPI SDL_GL_GetAttribute
    public static int glGetAttribute(
            GLAttr attr
    ) throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var value = arena.allocate(ValueLayout.JAVA_INT);
            check(SDL_GL_GetAttribute(attr.code(), value));
            return value.get(ValueLayout.JAVA_INT, 0);
        }
    }

    /// @sdlAPI SDL_GL_CreateContext
    public static GLContextState glCreateContext(
            ResourceSet resources,
            Window window
    ) throws SDLException {
        return new GLContextState(resources, check(SDL_GL_CreateContext(window.segment())));
    }

    /// @sdlAPI SDL_GL_MakeCurrent
    public static void glMakeCurrent(
            Window window,
            GLContextState context
    ) throws SDLException {
        check(SDL_GL_MakeCurrent(window.segment(), context.segment()));
    }

    /// @sdlAPI SDL_GL_GetCurrentWindow
    public static @Nullable Window glGetCurrentWindow() {
        return wrapWindow(SDL_GL_GetCurrentWindow());
    }

    /// @sdlAPI SDL_GL_GetCurrentContext
    public static @Nullable GLContextState glGetCurrentContext() {
        var context = SDL_GL_GetCurrentContext();
        if (context.address() == 0) {
            return null;
        }
        return GLContextState.newUnchecked(context);
    }

    /// @sdlAPI SDL_GL_SetSwapInterval
    public static void glSetSwapInterval(
            int interval
    ) throws SDLException {
        check(SDL_GL_SetSwapInterval(interval));
    }

    /// @sdlAPI SDL_GL_GetSwapInterval
    public static int glGetSwapInterval() throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var interval = arena.allocate(ValueLayout.JAVA_INT);
            check(SDL_GL_GetSwapInterval(interval));
            return interval.get(ValueLayout.JAVA_INT, 0);
        }
    }

    /// @sdlAPI SDL_GL_SwapWindow
    public static void glSwapWindow(
            Window window
    ) throws SDLException {
        check(SDL_GL_SwapWindow(window.segment()));
    }

    /// @sdlAPI SDL_GL_DestroyContext
    static void destroyGLContext(
            MemorySegment context
    ) throws SDLException {
        check(SDL_GL_DestroyContext(context));
    }

    private static @Nullable Window wrapWindow(MemorySegment window) {
        if (window.address() == 0) {
            return null;
        }
        return Window.newUnchecked(window);
    }

    private static Rect getRect(MemorySegment segment) {
        return Rect.of(
                SDL_Rect.x(segment),
                SDL_Rect.y(segment),
                SDL_Rect.w(segment),
                SDL_Rect.h(segment)
        );
    }

    private static MemorySegment putRect(Arena arena, Rect rect) {
        var segment = SDL_Rect.allocate(arena);
        putRect(segment, rect);
        return segment;
    }

    private static void putRect(MemorySegment segment, Rect rect) {
        SDL_Rect.initialize(
                segment,
                rect.x,
                rect.y,
                rect.width,
                rect.height
        );
    }
}
