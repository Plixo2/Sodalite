package io.github.plixo2.sodalite.category.events;

import io.github.plixo2.sodalite.category.keycode.Keycode;
import io.github.plixo2.sodalite.category.keycode.Keymod;
import io.github.plixo2.sodalite.category.mouse.MouseButton;
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

/// @sdlAPI SDL_Event
public interface EventConsumer {

    /// @sdlAPI SDL_QuitEvent
    default void onQuit                       (long timestamp) {}

    /// @sdlAPI SDL_CommonEvent
    default void onTerminating                (long timestamp) {}
    default void onLowMemory                  (long timestamp) {}
    default void onWillEnterBackground        (long timestamp) {}
    default void onDidEnterBackground         (long timestamp) {}
    default void onWillEnterForeground        (long timestamp) {}
    default void onDidEnterForeground         (long timestamp) {}
    default void onLocaleChanged              (long timestamp) {}
    default void onSystemThemeChanged         (long timestamp) {}

    /// @sdlAPI SDL_DisplayEvent
    default void onDisplayOrientation         (long timestamp, int displayID, DisplayOrientation newOrientation) {}
    default void onDisplayAdded               (long timestamp, int displayID) {}
    default void onDisplayRemoved             (long timestamp, int displayID) {}
    default void onDisplayMoved               (long timestamp, int displayID) {}
    default void onDisplayDesktopModeChanged  (long timestamp, int displayID) {}
    default void onDisplayCurrentModeChanged  (long timestamp, int displayID) {}
    default void onDisplayContentScaleChanged (long timestamp, int displayID) {}
    default void onDisplayUsableBoundsChanged (long timestamp, int displayID) {}

    /// @sdlAPI SDL_WindowEvent
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

    /// @sdlAPI SDL_KeyboardEvent
    default void onKeyDown                    (long timestamp, int windowID, int keyboardID, Scancode scancode, Keycode key, @Keymod int keymod, short raw, boolean repeat) {}
    default void onKeyUp                      (long timestamp, int windowID, int keyboardID, Scancode scancode, Keycode key, @Keymod int keymod, short raw) {}

    /// @sdlAPI SDL_TextEditingEvent
    default void onTextEditing                (long timestamp, int windowID, String text, int start, int length) {}

    /// @sdlAPI SDL_TextInputEvent
    default void onTextInput                  (long timestamp, int windowID, String text) {}

    /// @sdlAPI SDL_CommonEvent
    default void onKeymapChanged              (long timestamp) {}

    /// @sdlAPI SDL_KeyboardDeviceEvent
    default void onKeyboardAdded              (long timestamp, int keyboardID) {}
    default void onKeyboardRemoved            (long timestamp, int keyboardID) {}

    /// @sdlAPI SDL_TextEditingCandidatesEvent
    default void onTextEditingCandidates      (long timestamp, int windowID, List<String> candidates, int selectedCandidate, boolean horizontal) {}

    /// @sdlAPI SDL_CommonEvent
    default void onScreenKeyboardShown        (long timestamp) {}
    default void onScreenKeyboardHidden       (long timestamp) {}

    /// @sdlAPI SDL_MouseMotionEvent
    default void onMouseMotion                (long timestamp, int windowID, int mouseID, @MouseButtonFlags int state, float x, float y, float xrel, float yrel) {}

    /// @sdlAPI SDL_MouseButtonEvent
    default void onMouseButtonDown            (long timestamp, int windowID, int mouseID, @MouseButton int button, int clicks) {}
    default void onMouseButtonUp              (long timestamp, int windowID, int mouseID, @MouseButton int button, int clicks) {}

    /// @sdlAPI SDL_MouseWheelEvent
    default void onMouseWheel                 (long timestamp, int windowID, int mouseID, float x, float y, MouseWheelDirection direction, float mouseX, float mouseY, int integerX, int integerY) {}

    /// @sdlAPI SDL_MouseDeviceEvent
    default void onMouseAdded                 (long timestamp, int mouseID) {}
    default void onMouseRemoved               (long timestamp, int mouseID) {}

    /// @sdlAPI SDL_JoyAxisEvent
    default void onJoystickAxisMotion         (long timestamp, int joystickID, int axis, int value) {}

    /// @sdlAPI SDL_JoyBallEvent
    default void onJoystickBallMotion         (long timestamp, int joystickID, int ball, int xrel, int yrel) {}

    /// @sdlAPI SDL_JoyHatEvent
    default void onJoystickHatMotion          (long timestamp, int joystickID, int hat, int value) {}

    /// @sdlAPI SDL_JoyButtonEvent
    default void onJoystickButtonDown         (long timestamp, int joystickID, int button) {}
    default void onJoystickButtonUp           (long timestamp, int joystickID, int button) {}

    /// @sdlAPI SDL_JoyDeviceEvent
    default void onJoystickAdded              (long timestamp, int joystickID) {}
    default void onJoystickRemoved            (long timestamp, int joystickID) {}

    /// @sdlAPI SDL_JoyBatteryEvent
    default void onJoystickBatteryUpdated     (long timestamp, int joystickID, PowerState state, int percent) {}

    /// @sdlAPI SDL_JoyDeviceEvent
    default void onJoystickUpdateComplete     (long timestamp, int joystickID) {}

    /// @sdlAPI SDL_GamepadAxisEvent
    default void onGamepadAxisMotion          (long timestamp, int joystickID, int axis, int value) {}

    /// @sdlAPI SDL_GamepadButtonEvent
    default void onGamepadButtonDown          (long timestamp, int joystickID, int button) {}
    default void onGamepadButtonUp            (long timestamp, int joystickID, int button) {}

    /// @sdlAPI SDL_GamepadDeviceEvent
    default void onGamepadAdded               (long timestamp, int joystickID) {}
    default void onGamepadRemoved             (long timestamp, int joystickID) {}
    default void onGamepadRemapped            (long timestamp, int joystickID) {}

    /// @sdlAPI SDL_GamepadTouchpadEvent
    default void onGamepadTouchpadDown        (long timestamp, int joystickID, int touchpad, int finger, float x, float y, float pressure) {}
    default void onGamepadTouchpadMotion      (long timestamp, int joystickID, int touchpad, int finger, float x, float y, float pressure) {}
    default void onGamepadTouchpadUp          (long timestamp, int joystickID, int touchpad, int finger, float x, float y, float pressure) {}

    /// @sdlAPI SDL_GamepadSensorEvent
    default void onGamepadSensorUpdate        (long timestamp, int joystickID, int sensor, @ArrayLength(3) float[] data, long sensorTimestamp) {}

    /// @sdlAPI SDL_GamepadDeviceEvent
    default void onGamepadUpdateComplete      (long timestamp, int joystickID) {}
    default void onGamepadSteamHandleUpdated  (long timestamp, int joystickID) {}

    /// @sdlAPI SDL_TouchFingerEvent
    default void onFingerDown                 (long timestamp, long touchID, long fingerID, float x, float y, float dx, float dy, float pressure, int windowID) {}
    default void onFingerUp                   (long timestamp, long touchID, long fingerID, float x, float y, float dx, float dy, float pressure, int windowID) {}
    default void onFingerMotion               (long timestamp, long touchID, long fingerID, float x, float y, float dx, float dy, float pressure, int windowID) {}
    default void onFingerCanceled             (long timestamp, long touchID, long fingerID, float x, float y, float dx, float dy, float pressure, int windowID) {}

    /// @sdlAPI SDL_PinchFingerEvent
    default void onPinchBegin                 (long timestamp, float scale, int windowID) {}
    default void onPinchUpdate                (long timestamp, float scale, int windowID) {}
    default void onPinchEnd                   (long timestamp, float scale, int windowID) {}

    /// @sdlAPI SDL_ClipboardEvent
    default void onClipboardUpdate            (long timestamp, boolean owner, List<String> mime_types) {}

    /// @sdlAPI SDL_DropEvent
    default void onDropFile                   (long timestamp, int windowID, float x, float y, @Nullable String source, String fileName) {}
    default void onDropText                   (long timestamp, int windowID, float x, float y, @Nullable String source, String text) {}
    default void onDropBegin                  (long timestamp, int windowID, float x, float y, @Nullable String source) {}
    default void onDropComplete               (long timestamp, int windowID, float x, float y, @Nullable String source) {}
    default void onDropPosition               (long timestamp, int windowID, float x, float y, @Nullable String source) {}

    /// @sdlAPI SDL_AudioDeviceEvent
    default void onAudioDeviceAdded           (long timestamp, int audioDeviceID, boolean recording) {}
    default void onAudioDeviceRemoved         (long timestamp, int audioDeviceID, boolean recording) {}
    default void onAudioDeviceFormatChanged   (long timestamp, int audioDeviceID, boolean recording) {}

    /// @sdlAPI SDL_SensorEvent
    default void onSensorUpdate               (long timestamp, int sensorID, @ArrayLength(6) float[] data, long sensorTimestamp) {}

    /// @sdlAPI SDL_PenProximityEvent
    default void onPenProximityIn             (long timestamp, int windowID, int penID) {}
    default void onPenProximityOut            (long timestamp, int windowID, int penID) {}

    /// @sdlAPI SDL_PenTouchEvent
    default void onPenDown                    (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y, boolean eraser) {}
    default void onPenUp                      (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y, boolean eraser) {}

    /// @sdlAPI SDL_PenButtonEvent
    default void onPenButtonDown              (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y, int button) {}
    default void onPenButtonUp                (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y, int button) {}

    /// @sdlAPI SDL_PenMotionEvent
    default void onPenMotion                  (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y) {}

    /// @sdlAPI SDL_PenAxisEvent
    default void onPenAxis                    (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y, PenAxis axis, float value) {}

    /// @sdlAPI SDL_CameraDeviceEvent
    default void onCameraDeviceAdded          (long timestamp, int cameraID) {}
    default void onCameraDeviceRemoved        (long timestamp, int cameraID) {}
    default void onCameraDeviceApproved       (long timestamp, int cameraID) {}
    default void onCameraDeviceDenied         (long timestamp, int cameraID) {}

    /// @sdlAPI SDL_RenderEvent
    default void onRenderTargetsReset         (long timestamp, int windowID) {}
    default void onRenderDeviceReset          (long timestamp, int windowID) {}
    default void onRenderDeviceLost           (long timestamp, int windowID) {}

    /// @sdlAPI SDL_CommonEvent
    default void onPrivate0                   (long timestamp) {}
    default void onPrivate1                   (long timestamp) {}
    default void onPrivate2                   (long timestamp) {}
    default void onPrivate3                   (long timestamp) {}

    /// @sdlAPI SDL_UserEvent
    default void onUserEvent                  (long timestamp, int type, int windowID, int code, MemorySegment data1, MemorySegment data2) {}

}
