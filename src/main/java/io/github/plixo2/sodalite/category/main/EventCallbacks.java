package io.github.plixo2.sodalite.category.main;

import io.github.plixo2.sodalite.category.init.AppResult;
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

/// Copy & edit of [io.github.plixo2.sodalite.category.events.EventConsumer]
/// Unsed in [Callbacks]
/// 
/// @see io.github.plixo2.sodalite.category.events.EventConsumer for the original
/// @sdlAPI SDL_AppEvent
public interface EventCallbacks {

    /// @sdlAPI SDL_QuitEvent
    default AppResult onQuit                       (long timestamp) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_CommonEvent
    default AppResult onTerminating                (long timestamp) { return AppResult.CONTINUE; }
    default AppResult onLowMemory                  (long timestamp) { return AppResult.CONTINUE; }
    default AppResult onWillEnterBackground        (long timestamp) { return AppResult.CONTINUE; }
    default AppResult onDidEnterBackground         (long timestamp) { return AppResult.CONTINUE; }
    default AppResult onWillEnterForeground        (long timestamp) { return AppResult.CONTINUE; }
    default AppResult onDidEnterForeground         (long timestamp) { return AppResult.CONTINUE; }
    default AppResult onLocaleChanged              (long timestamp) { return AppResult.CONTINUE; }
    default AppResult onSystemThemeChanged         (long timestamp) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_DisplayEvent
    default AppResult onDisplayOrientation         (long timestamp, int displayID, DisplayOrientation newOrientation) { return AppResult.CONTINUE; }
    default AppResult onDisplayAdded               (long timestamp, int displayID) { return AppResult.CONTINUE; }
    default AppResult onDisplayRemoved             (long timestamp, int displayID) { return AppResult.CONTINUE; }
    default AppResult onDisplayMoved               (long timestamp, int displayID) { return AppResult.CONTINUE; }
    default AppResult onDisplayDesktopModeChanged  (long timestamp, int displayID) { return AppResult.CONTINUE; }
    default AppResult onDisplayCurrentModeChanged  (long timestamp, int displayID) { return AppResult.CONTINUE; }
    default AppResult onDisplayContentScaleChanged (long timestamp, int displayID) { return AppResult.CONTINUE; }
    default AppResult onDisplayUsableBoundsChanged (long timestamp, int displayID) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_WindowEvent
    default AppResult onWindowShown                (long timestamp, int windowID) { return AppResult.CONTINUE; }
    default AppResult onWindowHidden               (long timestamp, int windowID) { return AppResult.CONTINUE; }
    default AppResult onWindowExposed              (long timestamp, int windowID, boolean liveResize) { return AppResult.CONTINUE; }
    default AppResult onWindowMoved                (long timestamp, int windowID, int x, int y) { return AppResult.CONTINUE; }
    default AppResult onWindowResized              (long timestamp, int windowID, int width, int height) { return AppResult.CONTINUE; }
    default AppResult onWindowPixelSizeChanged     (long timestamp, int windowID, int width, int height) { return AppResult.CONTINUE; }
    default AppResult onWindowMetalViewResized     (long timestamp, int windowID) { return AppResult.CONTINUE; }
    default AppResult onWindowMinimized            (long timestamp, int windowID) { return AppResult.CONTINUE; }
    default AppResult onWindowMaximized            (long timestamp, int windowID) { return AppResult.CONTINUE; }
    default AppResult onWindowRestored             (long timestamp, int windowID) { return AppResult.CONTINUE; }
    default AppResult onWindowMouseEnter           (long timestamp, int windowID) { return AppResult.CONTINUE; }
    default AppResult onWindowMouseLeave           (long timestamp, int windowID) { return AppResult.CONTINUE; }
    default AppResult onWindowFocusGained          (long timestamp, int windowID) { return AppResult.CONTINUE; }
    default AppResult onWindowFocusLost            (long timestamp, int windowID) { return AppResult.CONTINUE; }
    default AppResult onWindowCloseRequested       (long timestamp, int windowID) { return AppResult.CONTINUE; }
    default AppResult onWindowHitTest              (long timestamp, int windowID) { return AppResult.CONTINUE; }
    default AppResult onWindowIccprofChanged       (long timestamp, int windowID) { return AppResult.CONTINUE; }
    default AppResult onWindowDisplayChanged       (long timestamp, int windowID, int displayID) { return AppResult.CONTINUE; }
    default AppResult onWindowDisplayScaleChanged  (long timestamp, int windowID) { return AppResult.CONTINUE; }
    default AppResult onWindowSafeAreaChanged      (long timestamp, int windowID) { return AppResult.CONTINUE; }
    default AppResult onWindowOccluded             (long timestamp, int windowID) { return AppResult.CONTINUE; }
    default AppResult onWindowEnterFullscreen      (long timestamp, int windowID) { return AppResult.CONTINUE; }
    default AppResult onWindowLeaveFullscreen      (long timestamp, int windowID) { return AppResult.CONTINUE; }
    default AppResult onWindowDestroyed            (long timestamp, int windowID) { return AppResult.CONTINUE; }
    default AppResult onWindowHdrStateChanged      (long timestamp, int windowID) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_KeyboardEvent
    default AppResult onKeyDown                    (long timestamp, int windowID, int keyboardID, Scancode scancode, Keycode key, @Keymod int keymod, short raw, boolean repeat) { return AppResult.CONTINUE; }
    default AppResult onKeyUp                      (long timestamp, int windowID, int keyboardID, Scancode scancode, Keycode key, @Keymod int keymod, short raw) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_TextEditingEvent
    default AppResult onTextEditing                (long timestamp, int windowID, String text, int start, int length) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_TextInputEvent
    default AppResult onTextInput                  (long timestamp, int windowID, String text) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_CommonEvent
    default AppResult onKeymapChanged              (long timestamp) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_KeyboardDeviceEvent
    default AppResult onKeyboardAdded              (long timestamp, int keyboardID) { return AppResult.CONTINUE; }
    default AppResult onKeyboardRemoved            (long timestamp, int keyboardID) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_TextEditingCandidatesEvent
    default AppResult onTextEditingCandidates      (long timestamp, int windowID, List<String> candidates, int selectedCandidate, boolean horizontal) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_CommonEvent
    default AppResult onScreenKeyboardShown        (long timestamp) { return AppResult.CONTINUE; }
    default AppResult onScreenKeyboardHidden       (long timestamp) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_MouseMotionEvent
    default AppResult onMouseMotion                (long timestamp, int windowID, int mouseID, @MouseButtonFlags int state, float x, float y, float xrel, float yrel) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_MouseButtonEvent
    default AppResult onMouseButtonDown            (long timestamp, int windowID, int mouseID, @MouseButton int button, int clicks, float x, float y) { return AppResult.CONTINUE; }
    default AppResult onMouseButtonUp              (long timestamp, int windowID, int mouseID, @MouseButton int button, int clicks, float x, float y) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_MouseWheelEvent
    default AppResult onMouseWheel                 (long timestamp, int windowID, int mouseID, float x, float y, MouseWheelDirection direction, float mouseX, float mouseY, int integerX, int integerY) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_MouseDeviceEvent
    default AppResult onMouseAdded                 (long timestamp, int mouseID) { return AppResult.CONTINUE; }
    default AppResult onMouseRemoved               (long timestamp, int mouseID) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_JoyAxisEvent
    default AppResult onJoystickAxisMotion         (long timestamp, int joystickID, int axis, int value) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_JoyBallEvent
    default AppResult onJoystickBallMotion         (long timestamp, int joystickID, int ball, int xrel, int yrel) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_JoyHatEvent
    default AppResult onJoystickHatMotion          (long timestamp, int joystickID, int hat, int value) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_JoyButtonEvent
    default AppResult onJoystickButtonDown         (long timestamp, int joystickID, int button) { return AppResult.CONTINUE; }
    default AppResult onJoystickButtonUp           (long timestamp, int joystickID, int button) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_JoyDeviceEvent
    default AppResult onJoystickAdded              (long timestamp, int joystickID) { return AppResult.CONTINUE; }
    default AppResult onJoystickRemoved            (long timestamp, int joystickID) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_JoyBatteryEvent
    default AppResult onJoystickBatteryUpdated     (long timestamp, int joystickID, PowerState state, int percent) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_JoyDeviceEvent
    default AppResult onJoystickUpdateComplete     (long timestamp, int joystickID) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_GamepadAxisEvent
    default AppResult onGamepadAxisMotion          (long timestamp, int joystickID, int axis, int value) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_GamepadButtonEvent
    default AppResult onGamepadButtonDown          (long timestamp, int joystickID, int button) { return AppResult.CONTINUE; }
    default AppResult onGamepadButtonUp            (long timestamp, int joystickID, int button) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_GamepadDeviceEvent
    default AppResult onGamepadAdded               (long timestamp, int joystickID) { return AppResult.CONTINUE; }
    default AppResult onGamepadRemoved             (long timestamp, int joystickID) { return AppResult.CONTINUE; }
    default AppResult onGamepadRemapped            (long timestamp, int joystickID) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_GamepadTouchpadEvent
    default AppResult onGamepadTouchpadDown        (long timestamp, int joystickID, int touchpad, int finger, float x, float y, float pressure) { return AppResult.CONTINUE; }
    default AppResult onGamepadTouchpadMotion      (long timestamp, int joystickID, int touchpad, int finger, float x, float y, float pressure) { return AppResult.CONTINUE; }
    default AppResult onGamepadTouchpadUp          (long timestamp, int joystickID, int touchpad, int finger, float x, float y, float pressure) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_GamepadSensorEvent
    default AppResult onGamepadSensorUpdate        (long timestamp, int joystickID, int sensor, @ArrayLength(3) float[] data, long sensorTimestamp) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_GamepadDeviceEvent
    default AppResult onGamepadUpdateComplete      (long timestamp, int joystickID) { return AppResult.CONTINUE; }
    default AppResult onGamepadSteamHandleUpdated  (long timestamp, int joystickID) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_TouchFingerEvent
    default AppResult onFingerDown                 (long timestamp, long touchID, long fingerID, float x, float y, float dx, float dy, float pressure, int windowID) { return AppResult.CONTINUE; }
    default AppResult onFingerUp                   (long timestamp, long touchID, long fingerID, float x, float y, float dx, float dy, float pressure, int windowID) { return AppResult.CONTINUE; }
    default AppResult onFingerMotion               (long timestamp, long touchID, long fingerID, float x, float y, float dx, float dy, float pressure, int windowID) { return AppResult.CONTINUE; }
    default AppResult onFingerCanceled             (long timestamp, long touchID, long fingerID, float x, float y, float dx, float dy, float pressure, int windowID) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_PinchFingerEvent
    default AppResult onPinchBegin                 (long timestamp, float scale, int windowID) { return AppResult.CONTINUE; }
    default AppResult onPinchUpdate                (long timestamp, float scale, int windowID) { return AppResult.CONTINUE; }
    default AppResult onPinchEnd                   (long timestamp, float scale, int windowID) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_ClipboardEvent
    default AppResult onClipboardUpdate            (long timestamp, boolean owner, List<String> mime_types) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_DropEvent
    default AppResult onDropFile                   (long timestamp, int windowID, float x, float y, @Nullable String source, String fileName) { return AppResult.CONTINUE; }
    default AppResult onDropText                   (long timestamp, int windowID, float x, float y, @Nullable String source, String text) { return AppResult.CONTINUE; }
    default AppResult onDropBegin                  (long timestamp, int windowID, float x, float y, @Nullable String source) { return AppResult.CONTINUE; }
    default AppResult onDropComplete               (long timestamp, int windowID, float x, float y, @Nullable String source) { return AppResult.CONTINUE; }
    default AppResult onDropPosition               (long timestamp, int windowID, float x, float y, @Nullable String source) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_AudioDeviceEvent
    default AppResult onAudioDeviceAdded           (long timestamp, int audioDeviceID, boolean recording) { return AppResult.CONTINUE; }
    default AppResult onAudioDeviceRemoved         (long timestamp, int audioDeviceID, boolean recording) { return AppResult.CONTINUE; }
    default AppResult onAudioDeviceFormatChanged   (long timestamp, int audioDeviceID, boolean recording) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_SensorEvent
    default AppResult onSensorUpdate               (long timestamp, int sensorID, @ArrayLength(6) float[] data, long sensorTimestamp) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_PenProximityEvent
    default AppResult onPenProximityIn             (long timestamp, int windowID, int penID) { return AppResult.CONTINUE; }
    default AppResult onPenProximityOut            (long timestamp, int windowID, int penID) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_PenTouchEvent
    default AppResult onPenDown                    (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y, boolean eraser) { return AppResult.CONTINUE; }
    default AppResult onPenUp                      (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y, boolean eraser) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_PenButtonEvent
    default AppResult onPenButtonDown              (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y, int button) { return AppResult.CONTINUE; }
    default AppResult onPenButtonUp                (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y, int button) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_PenMotionEvent
    default AppResult onPenMotion                  (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_PenAxisEvent
    default AppResult onPenAxis                    (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y, PenAxis axis, float value) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_CameraDeviceEvent
    default AppResult onCameraDeviceAdded          (long timestamp, int cameraID) { return AppResult.CONTINUE; }
    default AppResult onCameraDeviceRemoved        (long timestamp, int cameraID) { return AppResult.CONTINUE; }
    default AppResult onCameraDeviceApproved       (long timestamp, int cameraID) { return AppResult.CONTINUE; }
    default AppResult onCameraDeviceDenied         (long timestamp, int cameraID) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_RenderEvent
    default AppResult onRenderTargetsReset         (long timestamp, int windowID) { return AppResult.CONTINUE; }
    default AppResult onRenderDeviceReset          (long timestamp, int windowID) { return AppResult.CONTINUE; }
    default AppResult onRenderDeviceLost           (long timestamp, int windowID) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_CommonEvent
    default AppResult onPrivate0                   (long timestamp) { return AppResult.CONTINUE; }
    default AppResult onPrivate1                   (long timestamp) { return AppResult.CONTINUE; }
    default AppResult onPrivate2                   (long timestamp) { return AppResult.CONTINUE; }
    default AppResult onPrivate3                   (long timestamp) { return AppResult.CONTINUE; }

    /// @sdlAPI SDL_UserEvent
    default AppResult onUserEvent                  (long timestamp, int type, int windowID, int code, MemorySegment data1, MemorySegment data2) { return AppResult.CONTINUE; }

}
