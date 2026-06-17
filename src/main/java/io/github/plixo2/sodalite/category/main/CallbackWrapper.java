package io.github.plixo2.sodalite.category.main;

import io.github.plixo2.sodalite.category.events.EventConsumer;
import io.github.plixo2.sodalite.category.events.Events;
import io.github.plixo2.sodalite.category.init.AppResult;
import io.github.plixo2.sodalite.category.init.Init;
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

/// Eventloop for [Callbacks]
///
/// Also converts [EventConsumer] calls to [EventCallbacks]
class CallbackWrapper implements EventConsumer {
    private final Callbacks callbacks;
    private AppResult reference = null;

    CallbackWrapper(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    /// @return true for success, false for failure
    boolean run(String[] args) throws Exception {
        Exception failure = null;
        AppResult result;

        try {
            result = spin(args);
        } catch (Exception e) {
            failure = e;
            result = AppResult.FAILURE;
        }

        try {
            this.callbacks.quit(result);
        } catch (Exception inner) {
            if (failure != null) {
                failure.addSuppressed(inner);
            } else {
                failure = inner;
            }
        }

        try {
            Init.quit();
        } catch (Exception inner) {
            if (failure != null) {
                failure.addSuppressed(inner);
            } else {
                failure = inner;
            }
        }

        if (failure != null) {
            throw failure;
        }

        return result == AppResult.SUCCESS;
    }
    private AppResult spin(String[] args) throws Exception {
        AppResult result = this.callbacks.init(args);
        while (result == AppResult.CONTINUE) {
            while (Events.pollEvent(this)) {
                if (this.reference != AppResult.CONTINUE) {
                    return this.reference;
                }
            }
            result = this.callbacks.iterate();
        }
        return result;
    }

    /// @sdlAPI SDL_QuitEvent
    public void onQuit                       (long timestamp) { this.reference = this.callbacks.onQuit(timestamp); }

    /// @sdlAPI SDL_CommonEvent
    public void onTerminating                (long timestamp) { this.reference = this.callbacks.onTerminating(timestamp); }
    public void onLowMemory                  (long timestamp) { this.reference = this.callbacks.onLowMemory(timestamp); }
    public void onWillEnterBackground        (long timestamp) { this.reference = this.callbacks.onWillEnterBackground(timestamp); }
    public void onDidEnterBackground         (long timestamp) { this.reference = this.callbacks.onDidEnterBackground(timestamp); }
    public void onWillEnterForeground        (long timestamp) { this.reference = this.callbacks.onWillEnterForeground(timestamp); }
    public void onDidEnterForeground         (long timestamp) { this.reference = this.callbacks.onDidEnterForeground(timestamp); }
    public void onLocaleChanged              (long timestamp) { this.reference = this.callbacks.onLocaleChanged(timestamp); }
    public void onSystemThemeChanged         (long timestamp) { this.reference = this.callbacks.onSystemThemeChanged(timestamp); }

    /// @sdlAPI SDL_DisplayEvent
    public void onDisplayOrientation         (long timestamp, int displayID, DisplayOrientation newOrientation) { this.reference = this.callbacks.onDisplayOrientation(timestamp, displayID, newOrientation); }
    public void onDisplayAdded               (long timestamp, int displayID) { this.reference = this.callbacks.onDisplayAdded(timestamp, displayID); }
    public void onDisplayRemoved             (long timestamp, int displayID) { this.reference = this.callbacks.onDisplayRemoved(timestamp, displayID); }
    public void onDisplayMoved               (long timestamp, int displayID) { this.reference = this.callbacks.onDisplayMoved(timestamp, displayID); }
    public void onDisplayDesktopModeChanged  (long timestamp, int displayID) { this.reference = this.callbacks.onDisplayDesktopModeChanged(timestamp, displayID); }
    public void onDisplayCurrentModeChanged  (long timestamp, int displayID) { this.reference = this.callbacks.onDisplayCurrentModeChanged(timestamp, displayID); }
    public void onDisplayContentScaleChanged (long timestamp, int displayID) { this.reference = this.callbacks.onDisplayContentScaleChanged(timestamp, displayID); }
    public void onDisplayUsableBoundsChanged (long timestamp, int displayID) { this.reference = this.callbacks.onDisplayUsableBoundsChanged(timestamp, displayID); }

    /// @sdlAPI SDL_WindowEvent
    public void onWindowShown                (long timestamp, int windowID) { this.reference = this.callbacks.onWindowShown(timestamp, windowID); }
    public void onWindowHidden               (long timestamp, int windowID) { this.reference = this.callbacks.onWindowHidden(timestamp, windowID); }
    public void onWindowExposed              (long timestamp, int windowID, boolean liveResize) { this.reference = this.callbacks.onWindowExposed(timestamp, windowID, liveResize); }
    public void onWindowMoved                (long timestamp, int windowID, int x, int y) { this.reference = this.callbacks.onWindowMoved(timestamp, windowID, x, y); }
    public void onWindowResized              (long timestamp, int windowID, int width, int height) { this.reference = this.callbacks.onWindowResized(timestamp, windowID, width, height); }
    public void onWindowPixelSizeChanged     (long timestamp, int windowID, int width, int height) { this.reference = this.callbacks.onWindowPixelSizeChanged(timestamp, windowID, width, height); }
    public void onWindowMetalViewResized     (long timestamp, int windowID) { this.reference = this.callbacks.onWindowMetalViewResized(timestamp, windowID); }
    public void onWindowMinimized            (long timestamp, int windowID) { this.reference = this.callbacks.onWindowMinimized(timestamp, windowID); }
    public void onWindowMaximized            (long timestamp, int windowID) { this.reference = this.callbacks.onWindowMaximized(timestamp, windowID); }
    public void onWindowRestored             (long timestamp, int windowID) { this.reference = this.callbacks.onWindowRestored(timestamp, windowID); }
    public void onWindowMouseEnter           (long timestamp, int windowID) { this.reference = this.callbacks.onWindowMouseEnter(timestamp, windowID); }
    public void onWindowMouseLeave           (long timestamp, int windowID) { this.reference = this.callbacks.onWindowMouseLeave(timestamp, windowID); }
    public void onWindowFocusGained          (long timestamp, int windowID) { this.reference = this.callbacks.onWindowFocusGained(timestamp, windowID); }
    public void onWindowFocusLost            (long timestamp, int windowID) { this.reference = this.callbacks.onWindowFocusLost(timestamp, windowID); }
    public void onWindowCloseRequested       (long timestamp, int windowID) { this.reference = this.callbacks.onWindowCloseRequested(timestamp, windowID); }
    public void onWindowHitTest              (long timestamp, int windowID) { this.reference = this.callbacks.onWindowHitTest(timestamp, windowID); }
    public void onWindowIccprofChanged       (long timestamp, int windowID) { this.reference = this.callbacks.onWindowIccprofChanged(timestamp, windowID); }
    public void onWindowDisplayChanged       (long timestamp, int windowID, int displayID) { this.reference = this.callbacks.onWindowDisplayChanged(timestamp, windowID, displayID); }
    public void onWindowDisplayScaleChanged  (long timestamp, int windowID) { this.reference = this.callbacks.onWindowDisplayScaleChanged(timestamp, windowID); }
    public void onWindowSafeAreaChanged      (long timestamp, int windowID) { this.reference = this.callbacks.onWindowSafeAreaChanged(timestamp, windowID); }
    public void onWindowOccluded             (long timestamp, int windowID) { this.reference = this.callbacks.onWindowOccluded(timestamp, windowID); }
    public void onWindowEnterFullscreen      (long timestamp, int windowID) { this.reference = this.callbacks.onWindowEnterFullscreen(timestamp, windowID); }
    public void onWindowLeaveFullscreen      (long timestamp, int windowID) { this.reference = this.callbacks.onWindowLeaveFullscreen(timestamp, windowID); }
    public void onWindowDestroyed            (long timestamp, int windowID) { this.reference = this.callbacks.onWindowDestroyed(timestamp, windowID); }
    public void onWindowHdrStateChanged      (long timestamp, int windowID) { this.reference = this.callbacks.onWindowHdrStateChanged(timestamp, windowID); }

    /// @sdlAPI SDL_KeyboardEvent
    public void onKeyDown                    (long timestamp, int windowID, int keyboardID, Scancode scancode, Keycode key, @Keymod int keymod, short raw, boolean repeat) { this.reference = this.callbacks.onKeyDown(timestamp, windowID, keyboardID, scancode, key, keymod, raw, repeat); }
    public void onKeyUp                      (long timestamp, int windowID, int keyboardID, Scancode scancode, Keycode key, @Keymod int keymod, short raw) { this.reference = this.callbacks.onKeyUp(timestamp, windowID, keyboardID, scancode, key, keymod, raw); }

    /// @sdlAPI SDL_TextEditingEvent
    public void onTextEditing                (long timestamp, int windowID, String text, int start, int length) { this.reference = this.callbacks.onTextEditing(timestamp, windowID, text, start, length); }

    /// @sdlAPI SDL_TextInputEvent
    public void onTextInput                  (long timestamp, int windowID, String text) { this.reference = this.callbacks.onTextInput(timestamp, windowID, text); }

    /// @sdlAPI SDL_CommonEvent
    public void onKeymapChanged              (long timestamp) { this.reference = this.callbacks.onKeymapChanged(timestamp); }

    /// @sdlAPI SDL_KeyboardDeviceEvent
    public void onKeyboardAdded              (long timestamp, int keyboardID) { this.reference = this.callbacks.onKeyboardAdded(timestamp, keyboardID); }
    public void onKeyboardRemoved            (long timestamp, int keyboardID) { this.reference = this.callbacks.onKeyboardRemoved(timestamp, keyboardID); }

    /// @sdlAPI SDL_TextEditingCandidatesEvent
    public void onTextEditingCandidates      (long timestamp, int windowID, List<String> candidates, int selectedCandidate, boolean horizontal) { this.reference = this.callbacks.onTextEditingCandidates(timestamp, windowID, candidates, selectedCandidate, horizontal); }

    /// @sdlAPI SDL_CommonEvent
    public void onScreenKeyboardShown        (long timestamp) { this.reference = this.callbacks.onScreenKeyboardShown(timestamp); }
    public void onScreenKeyboardHidden       (long timestamp) { this.reference = this.callbacks.onScreenKeyboardHidden(timestamp); }

    /// @sdlAPI SDL_MouseMotionEvent
    public void onMouseMotion                (long timestamp, int windowID, int mouseID, @MouseButtonFlags int state, float x, float y, float xrel, float yrel) { this.reference = this.callbacks.onMouseMotion(timestamp, windowID, mouseID, state, x, y, xrel, yrel); }

    /// @sdlAPI SDL_MouseButtonEvent
    public void onMouseButtonDown            (long timestamp, int windowID, int mouseID, @MouseButton int button, int clicks) { this.reference = this.callbacks.onMouseButtonDown(timestamp, windowID, mouseID, button, clicks); }
    public void onMouseButtonUp              (long timestamp, int windowID, int mouseID, @MouseButton int button, int clicks) { this.reference = this.callbacks.onMouseButtonUp(timestamp, windowID, mouseID, button, clicks); }

    /// @sdlAPI SDL_MouseWheelEvent
    public void onMouseWheel                 (long timestamp, int windowID, int mouseID, float x, float y, MouseWheelDirection direction, float mouseX, float mouseY, int integerX, int integerY) { this.reference = this.callbacks.onMouseWheel(timestamp, windowID, mouseID, x, y, direction, mouseX, mouseY, integerX, integerY); }

    /// @sdlAPI SDL_MouseDeviceEvent
    public void onMouseAdded                 (long timestamp, int mouseID) { this.reference = this.callbacks.onMouseAdded(timestamp, mouseID); }
    public void onMouseRemoved               (long timestamp, int mouseID) { this.reference = this.callbacks.onMouseRemoved(timestamp, mouseID); }

    /// @sdlAPI SDL_JoyAxisEvent
    public void onJoystickAxisMotion         (long timestamp, int joystickID, int axis, int value) { this.reference = this.callbacks.onJoystickAxisMotion(timestamp, joystickID, axis, value); }

    /// @sdlAPI SDL_JoyBallEvent
    public void onJoystickBallMotion         (long timestamp, int joystickID, int ball, int xrel, int yrel) { this.reference = this.callbacks.onJoystickBallMotion(timestamp, joystickID, ball, xrel, yrel); }

    /// @sdlAPI SDL_JoyHatEvent
    public void onJoystickHatMotion          (long timestamp, int joystickID, int hat, int value) { this.reference = this.callbacks.onJoystickHatMotion(timestamp, joystickID, hat, value); }

    /// @sdlAPI SDL_JoyButtonEvent
    public void onJoystickButtonDown         (long timestamp, int joystickID, int button) { this.reference = this.callbacks.onJoystickButtonDown(timestamp, joystickID, button); }
    public void onJoystickButtonUp           (long timestamp, int joystickID, int button) { this.reference = this.callbacks.onJoystickButtonUp(timestamp, joystickID, button); }

    /// @sdlAPI SDL_JoyDeviceEvent
    public void onJoystickAdded              (long timestamp, int joystickID) { this.reference = this.callbacks.onJoystickAdded(timestamp, joystickID); }
    public void onJoystickRemoved            (long timestamp, int joystickID) { this.reference = this.callbacks.onJoystickRemoved(timestamp, joystickID); }

    /// @sdlAPI SDL_JoyBatteryEvent
    public void onJoystickBatteryUpdated     (long timestamp, int joystickID, PowerState state, int percent) { this.reference = this.callbacks.onJoystickBatteryUpdated(timestamp, joystickID, state, percent); }

    /// @sdlAPI SDL_JoyDeviceEvent
    public void onJoystickUpdateComplete     (long timestamp, int joystickID) { this.reference = this.callbacks.onJoystickUpdateComplete(timestamp, joystickID); }

    /// @sdlAPI SDL_GamepadAxisEvent
    public void onGamepadAxisMotion          (long timestamp, int joystickID, int axis, int value) { this.reference = this.callbacks.onGamepadAxisMotion(timestamp, joystickID, axis, value); }

    /// @sdlAPI SDL_GamepadButtonEvent
    public void onGamepadButtonDown          (long timestamp, int joystickID, int button) { this.reference = this.callbacks.onGamepadButtonDown(timestamp, joystickID, button); }
    public void onGamepadButtonUp            (long timestamp, int joystickID, int button) { this.reference = this.callbacks.onGamepadButtonUp(timestamp, joystickID, button); }

    /// @sdlAPI SDL_GamepadDeviceEvent
    public void onGamepadAdded               (long timestamp, int joystickID) { this.reference = this.callbacks.onGamepadAdded(timestamp, joystickID); }
    public void onGamepadRemoved             (long timestamp, int joystickID) { this.reference = this.callbacks.onGamepadRemoved(timestamp, joystickID); }
    public void onGamepadRemapped            (long timestamp, int joystickID) { this.reference = this.callbacks.onGamepadRemapped(timestamp, joystickID); }

    /// @sdlAPI SDL_GamepadTouchpadEvent
    public void onGamepadTouchpadDown        (long timestamp, int joystickID, int touchpad, int finger, float x, float y, float pressure) { this.reference = this.callbacks.onGamepadTouchpadDown(timestamp, joystickID, touchpad, finger, x, y, pressure); }
    public void onGamepadTouchpadMotion      (long timestamp, int joystickID, int touchpad, int finger, float x, float y, float pressure) { this.reference = this.callbacks.onGamepadTouchpadMotion(timestamp, joystickID, touchpad, finger, x, y, pressure); }
    public void onGamepadTouchpadUp          (long timestamp, int joystickID, int touchpad, int finger, float x, float y, float pressure) { this.reference = this.callbacks.onGamepadTouchpadUp(timestamp, joystickID, touchpad, finger, x, y, pressure); }

    /// @sdlAPI SDL_GamepadSensorEvent
    public void onGamepadSensorUpdate        (long timestamp, int joystickID, int sensor, @ArrayLength(3) float[] data, long sensorTimestamp) { this.reference = this.callbacks.onGamepadSensorUpdate(timestamp, joystickID, sensor, data, sensorTimestamp); }

    /// @sdlAPI SDL_GamepadDeviceEvent
    public void onGamepadUpdateComplete      (long timestamp, int joystickID) { this.reference = this.callbacks.onGamepadUpdateComplete(timestamp, joystickID); }
    public void onGamepadSteamHandleUpdated  (long timestamp, int joystickID) { this.reference = this.callbacks.onGamepadSteamHandleUpdated(timestamp, joystickID); }

    /// @sdlAPI SDL_TouchFingerEvent
    public void onFingerDown                 (long timestamp, long touchID, long fingerID, float x, float y, float dx, float dy, float pressure, int windowID) { this.reference = this.callbacks.onFingerDown(timestamp, touchID, fingerID, x, y, dx, dy, pressure, windowID); }
    public void onFingerUp                   (long timestamp, long touchID, long fingerID, float x, float y, float dx, float dy, float pressure, int windowID) { this.reference = this.callbacks.onFingerUp(timestamp, touchID, fingerID, x, y, dx, dy, pressure, windowID); }
    public void onFingerMotion               (long timestamp, long touchID, long fingerID, float x, float y, float dx, float dy, float pressure, int windowID) { this.reference = this.callbacks.onFingerMotion(timestamp, touchID, fingerID, x, y, dx, dy, pressure, windowID); }
    public void onFingerCanceled             (long timestamp, long touchID, long fingerID, float x, float y, float dx, float dy, float pressure, int windowID) { this.reference = this.callbacks.onFingerCanceled(timestamp, touchID, fingerID, x, y, dx, dy, pressure, windowID); }

    /// @sdlAPI SDL_PinchFingerEvent
    public void onPinchBegin                 (long timestamp, float scale, int windowID) { this.reference = this.callbacks.onPinchBegin(timestamp, scale, windowID); }
    public void onPinchUpdate                (long timestamp, float scale, int windowID) { this.reference = this.callbacks.onPinchUpdate(timestamp, scale, windowID); }
    public void onPinchEnd                   (long timestamp, float scale, int windowID) { this.reference = this.callbacks.onPinchEnd(timestamp, scale, windowID); }

    /// @sdlAPI SDL_ClipboardEvent
    public void onClipboardUpdate            (long timestamp, boolean owner, List<String> mime_types) { this.reference = this.callbacks.onClipboardUpdate(timestamp, owner, mime_types); }

    /// @sdlAPI SDL_DropEvent
    public void onDropFile                   (long timestamp, int windowID, float x, float y, @Nullable String source, String fileName) { this.reference = this.callbacks.onDropFile(timestamp, windowID, x, y, source, fileName); }
    public void onDropText                   (long timestamp, int windowID, float x, float y, @Nullable String source, String text) { this.reference = this.callbacks.onDropText(timestamp, windowID, x, y, source, text); }
    public void onDropBegin                  (long timestamp, int windowID, float x, float y, @Nullable String source) { this.reference = this.callbacks.onDropBegin(timestamp, windowID, x, y, source); }
    public void onDropComplete               (long timestamp, int windowID, float x, float y, @Nullable String source) { this.reference = this.callbacks.onDropComplete(timestamp, windowID, x, y, source); }
    public void onDropPosition               (long timestamp, int windowID, float x, float y, @Nullable String source) { this.reference = this.callbacks.onDropPosition(timestamp, windowID, x, y, source); }

    /// @sdlAPI SDL_AudioDeviceEvent
    public void onAudioDeviceAdded           (long timestamp, int audioDeviceID, boolean recording) { this.reference = this.callbacks.onAudioDeviceAdded(timestamp, audioDeviceID, recording); }
    public void onAudioDeviceRemoved         (long timestamp, int audioDeviceID, boolean recording) { this.reference = this.callbacks.onAudioDeviceRemoved(timestamp, audioDeviceID, recording); }
    public void onAudioDeviceFormatChanged   (long timestamp, int audioDeviceID, boolean recording) { this.reference = this.callbacks.onAudioDeviceFormatChanged(timestamp, audioDeviceID, recording); }

    /// @sdlAPI SDL_SensorEvent
    public void onSensorUpdate               (long timestamp, int sensorID, @ArrayLength(6) float[] data, long sensorTimestamp) { this.reference = this.callbacks.onSensorUpdate(timestamp, sensorID, data, sensorTimestamp); }

    /// @sdlAPI SDL_PenProximityEvent
    public void onPenProximityIn             (long timestamp, int windowID, int penID) { this.reference = this.callbacks.onPenProximityIn(timestamp, windowID, penID); }
    public void onPenProximityOut            (long timestamp, int windowID, int penID) { this.reference = this.callbacks.onPenProximityOut(timestamp, windowID, penID); }

    /// @sdlAPI SDL_PenTouchEvent
    public void onPenDown                    (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y, boolean eraser) { this.reference = this.callbacks.onPenDown(timestamp, windowID, penID, penState, x, y, eraser); }
    public void onPenUp                      (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y, boolean eraser) { this.reference = this.callbacks.onPenUp(timestamp, windowID, penID, penState, x, y, eraser); }

    /// @sdlAPI SDL_PenButtonEvent
    public void onPenButtonDown              (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y, int button) { this.reference = this.callbacks.onPenButtonDown(timestamp, windowID, penID, penState, x, y, button); }
    public void onPenButtonUp                (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y, int button) { this.reference = this.callbacks.onPenButtonUp(timestamp, windowID, penID, penState, x, y, button); }

    /// @sdlAPI SDL_PenMotionEvent
    public void onPenMotion                  (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y) { this.reference = this.callbacks.onPenMotion(timestamp, windowID, penID, penState, x, y); }

    /// @sdlAPI SDL_PenAxisEvent
    public void onPenAxis                    (long timestamp, int windowID, int penID, @PenInputFlags int penState, float x, float y, PenAxis axis, float value) { this.reference = this.callbacks.onPenAxis(timestamp, windowID, penID, penState, x, y, axis, value); }

    /// @sdlAPI SDL_CameraDeviceEvent
    public void onCameraDeviceAdded          (long timestamp, int cameraID) { this.reference = this.callbacks.onCameraDeviceAdded(timestamp, cameraID); }
    public void onCameraDeviceRemoved        (long timestamp, int cameraID) { this.reference = this.callbacks.onCameraDeviceRemoved(timestamp, cameraID); }
    public void onCameraDeviceApproved       (long timestamp, int cameraID) { this.reference = this.callbacks.onCameraDeviceApproved(timestamp, cameraID); }
    public void onCameraDeviceDenied         (long timestamp, int cameraID) { this.reference = this.callbacks.onCameraDeviceDenied(timestamp, cameraID); }

    /// @sdlAPI SDL_RenderEvent
    public void onRenderTargetsReset         (long timestamp, int windowID) { this.reference = this.callbacks.onRenderTargetsReset(timestamp, windowID); }
    public void onRenderDeviceReset          (long timestamp, int windowID) { this.reference = this.callbacks.onRenderDeviceReset(timestamp, windowID); }
    public void onRenderDeviceLost           (long timestamp, int windowID) { this.reference = this.callbacks.onRenderDeviceLost(timestamp, windowID); }

    /// @sdlAPI SDL_CommonEvent
    public void onPrivate0                   (long timestamp) { this.reference = this.callbacks.onPrivate0(timestamp); }
    public void onPrivate1                   (long timestamp) { this.reference = this.callbacks.onPrivate1(timestamp); }
    public void onPrivate2                   (long timestamp) { this.reference = this.callbacks.onPrivate2(timestamp); }
    public void onPrivate3                   (long timestamp) { this.reference = this.callbacks.onPrivate3(timestamp); }

    /// @sdlAPI SDL_UserEvent
    public void onUserEvent                  (long timestamp, int type, int windowID, int code, MemorySegment data1, MemorySegment data2) { this.reference = this.callbacks.onUserEvent(timestamp, type, windowID, code, data1, data2); }

}
