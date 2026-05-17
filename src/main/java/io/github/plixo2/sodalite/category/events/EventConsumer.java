package io.github.plixo2.sodalite.category.events;

import io.github.plixo2.sodalite.category.keycode.Keycode;
import io.github.plixo2.sodalite.category.keycode.Keymod;
import io.github.plixo2.sodalite.category.mouse.MouseButtonFlags;
import io.github.plixo2.sodalite.category.mouse.MouseWheelDirection;
import io.github.plixo2.sodalite.category.pen.PenAxis;
import io.github.plixo2.sodalite.category.pen.PenInputFlags;
import io.github.plixo2.sodalite.category.power.PowerState;
import io.github.plixo2.sodalite.category.scancode.Scancode;
import io.github.plixo2.sodalite.category.video.DisplayOrientation;
import io.github.plixo2.sodalite.memory.ArrayLength;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.MemorySegment;
import java.util.List;

public interface EventConsumer {

    /// @apiNote SDL_QuitEvent
    default void onQuit                       (long timestamp) {}

    /// @apiNote SDL_CommonEvent
    default void onTerminating                (long timestamp) {}
    default void onLowMemory                  (long timestamp) {}
    default void onWillEnterBackground        (long timestamp) {}
    default void onDidEnterBackground         (long timestamp) {}
    default void onWillEnterForeground        (long timestamp) {}
    default void onDidEnterForeground         (long timestamp) {}
    default void onLocaleChanged              (long timestamp) {}
    default void onSystemThemeChanged         (long timestamp) {}

    /// @apiNote SDL_DisplayEvent
    default void onDisplayOrientation         (long timestamp, DisplayOrientation newOrientation) {}
    default void onDisplayAdded               (long timestamp, int displayID) {}
    default void onDisplayRemoved             (long timestamp, int displayID) {}
    default void onDisplayMoved               (long timestamp, int displayID) {}
    default void onDisplayDesktopModeChanged  (long timestamp, int displayID) {}
    default void onDisplayCurrentModeChanged  (long timestamp, int displayID) {}
    default void onDisplayContentScaleChanged (long timestamp, int displayID) {}
    default void onDisplayUsableBoundsChanged (long timestamp, int displayID) {}

    /// @apiNote SDL_WindowEvent
    default void onWindowShown                (long timestamp, int windowID) {}
    default void onWindowHidden               (long timestamp, int windowID) {}
    default void onWindowExposed              (long timestamp, int windowID, boolean liveResize) {}
    default void onWindowMoved                (long timestamp, int windowID, int x, int y) {}
    default void onWindowResized              (long timestamp, int windowID, int width, int height) {}
    default void onWindowPixelSizeChanged     (long timestamp, int windowID, int width, int height) {}
    default void onWindowMetalViewResized     (long timestamp, int windowID) {}
    default void onWindowMinimized            (long timestamp, int windowID) {}
    default void onWindowMaximized            (long timestamp, int windowID) {}
    default void onWindowRestored             (long timestamp, int windowID) {}
    default void onWindowMouseEnter           (long timestamp, int windowID) {}
    default void onWindowMouseLeave           (long timestamp, int windowID) {}
    default void onWindowFocusGained          (long timestamp, int windowID) {}
    default void onWindowFocusLost            (long timestamp, int windowID) {}
    default void onWindowCloseRequested       (long timestamp, int windowID) {}
    default void onWindowHitTest              (long timestamp, int windowID) {}
    default void onWindowIccprofChanged       (long timestamp, int windowID) {}
    default void onWindowDisplayChanged       (long timestamp, int windowID, int displayID) {}
    default void onWindowDisplayScaleChanged  (long timestamp, int windowID) {}
    default void onWindowSafeAreaChanged      (long timestamp, int windowID) {}
    default void onWindowOccluded             (long timestamp, int windowID) {}
    default void onWindowEnterFullscreen      (long timestamp, int windowID) {}
    default void onWindowLeaveFullscreen      (long timestamp, int windowID) {}
    default void onWindowDestroyed            (long timestamp, int windowID) {}
    default void onWindowHdrStateChanged      (long timestamp, int windowID) {}

    /// @apiNote SDL_KeyboardEvent
    default void onKeyDown                    (long timestamp, int windowID, int keyboardID, Scancode scancode, Keycode keycode, @Keymod int keymod, short raw, boolean repeat) {}
    default void onKeyUp                      (long timestamp, int windowID, int keyboardID, Scancode scancode, Keycode keycode, @Keymod int keymod, short raw) {}

    /// @apiNote SDL_TextEditingEvent
    default void onTextEditing                (long timestamp, int windowID, String text, int start, int length) {}

    /// @apiNote SDL_TextInputEvent
    default void onTextInput                  (long timestamp, int windowID, String text) {}

    /// @apiNote SDL_CommonEvent
    default void onKeymapChanged              (long timestamp) {}

    /// @apiNote SDL_KeyboardDeviceEvent
    default void onKeyboardAdded              (long timestamp, int keyboardID) {}
    default void onKeyboardRemoved            (long timestamp, int keyboardID) {}

    /// @apiNote SDL_TextEditingCandidatesEvent
    default void onTextEditingCandidates      (long timestamp, int windowID, List<String> candidates, int selectedCandidate, boolean horizontal) {}

    /// @apiNote SDL_CommonEvent
    default void onScreenKeyboardShown        (long timestamp) {}
    default void onScreenKeyboardHidden       (long timestamp) {}

    /// @apiNote SDL_MouseMotionEvent
    default void onMouseMotion                (long timestamp, int windowID, int mouseID, @MouseButtonFlags int state, float x, float y, float xrel, float yrel) {}

    /// @apiNote SDL_MouseButtonEvent
    default void onMouseButtonDown            (long timestamp, int windowID, int mouseID, @MouseButtonFlags int button, int clicks) {}
    default void onMouseButtonUp              (long timestamp, int windowID, int mouseID, @MouseButtonFlags int button, int clicks) {}

    /// @apiNote SDL_MouseWheelEvent
    default void onMouseWheel                 (long timestamp, int windowID, int mouseID, float x, float y, MouseWheelDirection direction, float mouseX, float mouseY, int integerX, int integerY) {}

    /// @apiNote SDL_MouseDeviceEvent
    default void onMouseAdded                 (long timestamp, int mouseID) {}
    default void onMouseRemoved               (long timestamp, int mouseID) {}

    /// @apiNote SDL_JoyAxisEvent
    default void onJoystickAxisMotion         (long timestamp, int joystickID, int axis, int value) {}

    /// @apiNote SDL_JoyBallEvent
    default void onJoystickBallMotion         (long timestamp, int joystickID, int ball, int xrel, int yrel) {}

    /// @apiNote SDL_JoyHatEvent
    default void onJoystickHatMotion          (long timestamp, int joystickID, int hat, int value) {}

    /// @apiNote SDL_JoyButtonEvent
    default void onJoystickButtonDown         (long timestamp, int joystickID, int button) {}
    default void onJoystickButtonUp           (long timestamp, int joystickID, int button) {}

    /// @apiNote SDL_JoyDeviceEvent
    default void onJoystickAdded              (long timestamp, int joystickID) {}
    default void onJoystickRemoved            (long timestamp, int joystickID) {}

    /// @apiNote SDL_JoyBatteryEvent
    default void onJoystickBatteryUpdated     (long timestamp, int joystickID, PowerState state, int percent) {}

    /// @apiNote SDL_JoyDeviceEvent
    default void onJoystickUpdateComplete     (long timestamp, int joystickID) {}

    /// @apiNote SDL_GamepadAxisEvent
    default void onGamepadAxisMotion          (long timestamp, int joystickID, int axis, int value) {}

    /// @apiNote SDL_GamepadButtonEvent
    default void onGamepadButtonDown          (long timestamp, int joystickID, int button) {}
    default void onGamepadButtonUp            (long timestamp, int joystickID, int button) {}

    /// @apiNote SDL_GamepadDeviceEvent
    default void onGamepadAdded               (long timestamp, int joystickID) {}
    default void onGamepadRemoved             (long timestamp, int joystickID) {}
    default void onGamepadRemapped            (long timestamp, int joystickID) {}

    /// @apiNote SDL_GamepadTouchpadEvent
    default void onGamepadTouchpadDown        (long timestamp, int joystickID, int touchpad, int finger, float x, float y, float pressure) {}
    default void onGamepadTouchpadMotion      (long timestamp, int joystickID, int touchpad, int finger, float x, float y, float pressure) {}
    default void onGamepadTouchpadUp          (long timestamp, int joystickID, int touchpad, int finger, float x, float y, float pressure) {}

    /// @apiNote SDL_GamepadSensorEvent
    default void onGamepadSensorUpdate        (long timestamp, int joystickID, int sensor, @ArrayLength(3) float[] data, long sensorTimestamp) {}

    /// @apiNote SDL_GamepadDeviceEvent
    default void onGamepadUpdateComplete      (long timestamp, int joystickID) {}
    default void onGamepadSteamHandleUpdated  (long timestamp, int joystickID) {}

    /// @apiNote SDL_TouchFingerEvent
    default void onFingerDown                 (long timestamp, int touchID, int fingerID, float x, float y, float dx, float dy, float pressure, int windowID) {}
    default void onFingerUp                   (long timestamp, int touchID, int fingerID, float x, float y, float dx, float dy, float pressure, int windowID) {}
    default void onFingerMotion               (long timestamp, int touchID, int fingerID, float x, float y, float dx, float dy, float pressure, int windowID) {}
    default void onFingerCanceled             (long timestamp, int touchID, int fingerID, float x, float y, float dx, float dy, float pressure, int windowID) {}

    /// @apiNote SDL_PinchFingerEvent
    default void onPinchBegin                 (long timestamp, float scale, int windowID) {}
    default void onPinchUpdate                (long timestamp, float scale, int windowID) {}
    default void onPinchEnd                   (long timestamp, float scale, int windowID) {}

    /// @apiNote SDL_ClipboardEvent
    default void onClipboardUpdate            (long timestamp, boolean owner, List<String> mime_types) {}

    /// @apiNote SDL_DropEvent
    default void onDropFile                   (long timestamp, int windowID, float x, float y, @Nullable String source, String fileName) {}
    default void onDropText                   (long timestamp, int windowID, float x, float y, @Nullable String source, String text) {}
    default void onDropBegin                  (long timestamp, int windowID, float x, float y, @Nullable String source) {}
    default void onDropComplete               (long timestamp, int windowID, float x, float y, @Nullable String source) {}
    default void onDropPosition               (long timestamp, int windowID, float x, float y, @Nullable String source) {}

    /// @apiNote SDL_AudioDeviceEvent
    default void onAudioDeviceAdded           (long timestamp, boolean recording) {}
    default void onAudioDeviceRemoved         (long timestamp, boolean recording) {}
    default void onAudioDeviceFormatChanged   (long timestamp, boolean recording) {}

    /// @apiNote SDL_SensorEvent
    default void onSensorUpdate               (long timestamp, int sensorID, @ArrayLength(6) float[] data, long sensorTimestamp) {}

    /// @apiNote SDL_PenProximityEvent
    default void onPenProximityIn             (long timestamp, int windowID, int penID) {}
    default void onPenProximityOut            (long timestamp, int windowID, int penID) {}

    /// @apiNote SDL_PenTouchEvent
    default void onPenDown                    (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y, boolean eraser) {}
    default void onPenUp                      (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y, boolean eraser) {}

    /// @apiNote SDL_PenButtonEvent
    default void onPenButtonDown              (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y, int button) {}
    default void onPenButtonUp                (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y, int button) {}

    /// @apiNote SDL_PenMotionEvent
    default void onPenMotion                  (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y) {}

    /// @apiNote SDL_PenAxisEvent
    default void onPenAxis                    (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y, PenAxis axis, float value) {}

    /// @apiNote SDL_CameraDeviceEvent
    default void onCameraDeviceAdded          (long timestamp, int cameraID) {}
    default void onCameraDeviceRemoved        (long timestamp, int cameraID) {}
    default void onCameraDeviceApproved       (long timestamp, int cameraID) {}
    default void onCameraDeviceDenied         (long timestamp, int cameraID) {}

    /// @apiNote SDL_RenderEvent
    default void onRenderTargetsReset         (long timestamp, int windowID) {}
    default void onRenderDeviceReset          (long timestamp, int windowID) {}
    default void onRenderDeviceLost           (long timestamp, int windowID) {}

    /// @apiNote SDL_CommonEvent
    default void onPrivate0                   (long timestamp) {}
    default void onPrivate1                   (long timestamp) {}
    default void onPrivate2                   (long timestamp) {}
    default void onPrivate3                   (long timestamp) {}

    /// @apiNote SDL_UserEvent
    default void onUser                       (long timestamp, int type, int windowID, int code, MemorySegment data1, MemorySegment data2) {}


/*
    sealed interface EventObject {}

    /// @apiNote
    record Empty(
            Empty.Type type

    ) implements EventObject {
        public enum Type {

            ;
        }
    }

    /// @apiNote SDL_DisplayEvent
    record DisplayEvent(
            DisplayEvent.Type type,
            int displayID,
            int data1,
            int data2
    ) implements EventObject {
        public enum Type {
            ORIENTATION,
            ADDED,
            REMOVED,
            MOVED,
            DESKTOP_MODE_CHANGED,
            CURRENT_MODE_CHANGED,
            CONTENT_SCALE_CHANGED,
            USABLE_BOUNDS_CHANGED,

            ;
        }
    }

    /// @apiNote SDL_WindowEvent
    record WindowEvent(
            WindowEvent.Type type,
            int windowID,
            int data1,
            int data2
    ) implements EventObject {
        public enum Type {
            SHOWN,
            HIDDEN,
            EXPOSED,
            MOVED,
            RESIZED,
            PIXEL_SIZE_CHANGED,
            METAL_VIEW_RESIZED,
            MINIMIZED,
            MAXIMIZED,
            RESTORED,
            MOUSE_ENTER,
            MOUSE_LEAVE,
            FOCUS_GAINED,
            FOCUS_LOST,
            CLOSE_REQUESTED,
            HIT_TEST,
            ICCPROF_CHANGED,
            DISPLAY_CHANGED,
            DISPLAY_SCALE_CHANGED,
            SAFE_AREA_CHANGED,
            OCCLUDED,
            ENTER_FULLSCREEN,
            LEAVE_FULLSCREEN,
            DESTROYED,
            HDR_STATE_CHANGED,

            ;
        }
    }

    /// @apiNote SDL_KeyboardEvent
    record KeyboardEvent(
            KeyboardEvent.Type type,
            int windowID,
            int keyboardID,
            Scancode scancode,
            Keycode keycode,
            @Keymod int keymod,
            short raw,
            boolean down,
            boolean repeat
    ) implements EventObject {
        public enum Type {
            KEY_DOWN,
            KEY_UP,

            ;
        }
    }

    /// @apiNote SDL_TextEditingEvent
    record TextEditingEvent(
            TextEditingEvent.Type type,
            int windowID,
            String text,
            int start,
            int length
    ) implements EventObject {
        public enum Type {
            TEXT_EDITING,

            ;
        }
    }

    /// @apiNote SDL_TextInputEvent
    record TextInputEvent(
            TextInputEvent.Type type,
            int windowID,
            String text
    ) implements EventObject {
        public enum Type {
            TEXT_INPUT,

            ;
        }
    }

    /// @apiNote SDL_KeyboardDeviceEvent
    record KeyboardDeviceEvent(
            KeyboardDeviceEvent.Type type,
            int keyboardID
    ) implements EventObject {
        public enum Type {
            KEYBOARD_ADDED,
            KEYBOARD_REMOVED,

            ;
        }
    }

    /// @apiNote SDL_TextEditingCandidatesEvent
    record TextEditingCandidatesEvent(
            TextEditingCandidatesEvent.Type type,
            int windowID,
            List<String> candidates,
            int selectedCandidate,
            boolean horizontal
    ) implements EventObject {
        public enum Type {
            TEXT_EDITING_CANDIDATES,

            ;
        }
    }

    /// @apiNote SDL_MouseMotionEvent
    record MouseMotionEvent(
            MouseMotionEvent.Type type,
            int windowID,
            int mouseID,
            @MouseButtonFlags int state,
            float x,
            float y,
            float xrel,
            float yrel
    ) implements EventObject {
        public enum Type {
            MOUSE_MOTION,

            ;
        }
    }

    /// @apiNote SDL_MouseButtonEvent
    record MouseButtonEvent(
            MouseButtonEvent.Type type,
            int windowID,
            int mouseID,
            @MouseButtonFlags int button,
            boolean down,
            int clicks
    ) implements EventObject {
        public enum Type {
            MOUSE_BUTTON_DOWN,
            MOUSE_BUTTON_UP,

            ;
        }
    }

    /// @apiNote SDL_MouseWheelEvent
    record MouseWheelEvent(
            MouseWheelEvent.Type type,
            int windowID,
            int mouseID,
            float x,
            float y,
            MouseWheelDirection direction,
            float mouseX,
            float mouseY,
            int integerX,
            int integerY
    ) implements EventObject {
        public enum Type {
            MOUSE_WHEEL,

            ;
        }
    }

    /// @apiNote SDL_MouseDeviceEvent
    record MouseDeviceEvent(
            MouseDeviceEvent.Type type,
            int mouseID
    ) implements EventObject {
        public enum Type {
            MOUSE_ADDED,
            MOUSE_REMOVED,

            ;
        }
    }
*/

}
