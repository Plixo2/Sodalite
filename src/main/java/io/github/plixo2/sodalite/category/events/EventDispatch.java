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
import org.jetbrains.annotations.Nullable;
import org.libsdl.sdl.*;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.libsdl.sdl.SDL3_h.*;

public class EventDispatch {
    private EventDispatch() {}

    static void dispatch(
            EventConsumer consumer,
            MemorySegment event
    ) {
        var type = SDL_Event.type(event);
        var timestamp = SDL_CommonEvent.timestamp(SDL_Event.common(event));

        switch (type) {
            case SDL_EVENT_QUIT -> { consumer.onQuit(timestamp); }
            case SDL_EVENT_TERMINATING -> { consumer.onTerminating(timestamp); }
            case SDL_EVENT_LOW_MEMORY -> { consumer.onLowMemory(timestamp); }
            case SDL_EVENT_WILL_ENTER_BACKGROUND -> { consumer.onWillEnterBackground(timestamp); }
            case SDL_EVENT_DID_ENTER_BACKGROUND -> { consumer.onDidEnterBackground(timestamp); }
            case SDL_EVENT_WILL_ENTER_FOREGROUND -> { consumer.onWillEnterForeground(timestamp); }
            case SDL_EVENT_DID_ENTER_FOREGROUND -> { consumer.onDidEnterForeground(timestamp); }
            case SDL_EVENT_LOCALE_CHANGED -> { consumer.onLocaleChanged(timestamp); }
            case SDL_EVENT_SYSTEM_THEME_CHANGED -> { consumer.onSystemThemeChanged(timestamp); }
            case SDL_EVENT_DISPLAY_ORIENTATION -> { consumer.onDisplayOrientation(timestamp, SDL_DisplayEvent_displayID(event), SDL_DisplayEvent_newOrientation(event)); }
            case SDL_EVENT_DISPLAY_ADDED -> { consumer.onDisplayAdded(timestamp, SDL_DisplayEvent_displayID(event)); }
            case SDL_EVENT_DISPLAY_REMOVED -> { consumer.onDisplayRemoved(timestamp, SDL_DisplayEvent_displayID(event)); }
            case SDL_EVENT_DISPLAY_MOVED -> { consumer.onDisplayMoved(timestamp, SDL_DisplayEvent_displayID(event)); }
            case SDL_EVENT_DISPLAY_DESKTOP_MODE_CHANGED -> { consumer.onDisplayDesktopModeChanged(timestamp, SDL_DisplayEvent_displayID(event)); }
            case SDL_EVENT_DISPLAY_CURRENT_MODE_CHANGED -> { consumer.onDisplayCurrentModeChanged(timestamp, SDL_DisplayEvent_displayID(event)); }
            case SDL_EVENT_DISPLAY_CONTENT_SCALE_CHANGED -> { consumer.onDisplayContentScaleChanged(timestamp, SDL_DisplayEvent_displayID(event)); }
            case SDL_EVENT_DISPLAY_USABLE_BOUNDS_CHANGED -> { consumer.onDisplayUsableBoundsChanged(timestamp, SDL_DisplayEvent_displayID(event)); }
            case SDL_EVENT_WINDOW_SHOWN -> { consumer.onWindowShown(timestamp, SDL_WindowEvent_windowID(event)); }
            case SDL_EVENT_WINDOW_HIDDEN -> { consumer.onWindowHidden(timestamp, SDL_WindowEvent_windowID(event)); }
            case SDL_EVENT_WINDOW_EXPOSED -> { consumer.onWindowExposed(timestamp, SDL_WindowEvent_windowID(event), SDL_WindowEvent_liveResize(event)); }
            case SDL_EVENT_WINDOW_MOVED -> { consumer.onWindowMoved(timestamp, SDL_WindowEvent_windowID(event), SDL_WindowEvent_x(event), SDL_WindowEvent_y(event)); }
            case SDL_EVENT_WINDOW_RESIZED -> { consumer.onWindowResized(timestamp, SDL_WindowEvent_windowID(event), SDL_WindowEvent_width(event), SDL_WindowEvent_height(event)); }
            case SDL_EVENT_WINDOW_PIXEL_SIZE_CHANGED -> { consumer.onWindowPixelSizeChanged(timestamp, SDL_WindowEvent_windowID(event), SDL_WindowEvent_width(event), SDL_WindowEvent_height(event)); }
            case SDL_EVENT_WINDOW_METAL_VIEW_RESIZED -> { consumer.onWindowMetalViewResized(timestamp, SDL_WindowEvent_windowID(event)); }
            case SDL_EVENT_WINDOW_MINIMIZED -> { consumer.onWindowMinimized(timestamp, SDL_WindowEvent_windowID(event)); }
            case SDL_EVENT_WINDOW_MAXIMIZED -> { consumer.onWindowMaximized(timestamp, SDL_WindowEvent_windowID(event)); }
            case SDL_EVENT_WINDOW_RESTORED -> { consumer.onWindowRestored(timestamp, SDL_WindowEvent_windowID(event)); }
            case SDL_EVENT_WINDOW_MOUSE_ENTER -> { consumer.onWindowMouseEnter(timestamp, SDL_WindowEvent_windowID(event)); }
            case SDL_EVENT_WINDOW_MOUSE_LEAVE -> { consumer.onWindowMouseLeave(timestamp, SDL_WindowEvent_windowID(event)); }
            case SDL_EVENT_WINDOW_FOCUS_GAINED -> { consumer.onWindowFocusGained(timestamp, SDL_WindowEvent_windowID(event)); }
            case SDL_EVENT_WINDOW_FOCUS_LOST -> { consumer.onWindowFocusLost(timestamp, SDL_WindowEvent_windowID(event)); }
            case SDL_EVENT_WINDOW_CLOSE_REQUESTED -> { consumer.onWindowCloseRequested(timestamp, SDL_WindowEvent_windowID(event)); }
            case SDL_EVENT_WINDOW_HIT_TEST -> { consumer.onWindowHitTest(timestamp, SDL_WindowEvent_windowID(event)); }
            case SDL_EVENT_WINDOW_ICCPROF_CHANGED -> { consumer.onWindowIccprofChanged(timestamp, SDL_WindowEvent_windowID(event)); }
            case SDL_EVENT_WINDOW_DISPLAY_CHANGED -> { consumer.onWindowDisplayChanged(timestamp, SDL_WindowEvent_windowID(event), SDL_WindowEvent_displayID(event)); }
            case SDL_EVENT_WINDOW_DISPLAY_SCALE_CHANGED -> { consumer.onWindowDisplayScaleChanged(timestamp, SDL_WindowEvent_windowID(event)); }
            case SDL_EVENT_WINDOW_SAFE_AREA_CHANGED -> { consumer.onWindowSafeAreaChanged(timestamp, SDL_WindowEvent_windowID(event)); }
            case SDL_EVENT_WINDOW_OCCLUDED -> { consumer.onWindowOccluded(timestamp, SDL_WindowEvent_windowID(event)); }
            case SDL_EVENT_WINDOW_ENTER_FULLSCREEN -> { consumer.onWindowEnterFullscreen(timestamp, SDL_WindowEvent_windowID(event)); }
            case SDL_EVENT_WINDOW_LEAVE_FULLSCREEN -> { consumer.onWindowLeaveFullscreen(timestamp, SDL_WindowEvent_windowID(event)); }
            case SDL_EVENT_WINDOW_DESTROYED -> { consumer.onWindowDestroyed(timestamp, SDL_WindowEvent_windowID(event)); }
            case SDL_EVENT_WINDOW_HDR_STATE_CHANGED -> { consumer.onWindowHdrStateChanged(timestamp, SDL_WindowEvent_windowID(event)); }
            case SDL_EVENT_KEY_DOWN -> { consumer.onKeyDown(timestamp, SDL_KeyboardEvent_windowID(event), SDL_KeyboardEvent_keyboardID(event), SDL_KeyboardEvent_scancode(event), SDL_KeyboardEvent_keycode(event), SDL_KeyboardEvent_keymod(event), SDL_KeyboardEvent_raw(event), SDL_KeyboardEvent_repeat(event)); }
            case SDL_EVENT_KEY_UP -> { consumer.onKeyUp(timestamp, SDL_KeyboardEvent_windowID(event), SDL_KeyboardEvent_keyboardID(event), SDL_KeyboardEvent_scancode(event), SDL_KeyboardEvent_keycode(event), SDL_KeyboardEvent_keymod(event), SDL_KeyboardEvent_raw(event)); }
            case SDL_EVENT_TEXT_EDITING -> { consumer.onTextEditing(timestamp, SDL_TextEditingEvent_windowID(event), SDL_TextEditingEvent_text(event), SDL_TextEditingEvent_start(event), SDL_TextEditingEvent_length(event)); }
            case SDL_EVENT_TEXT_INPUT -> { consumer.onTextInput(timestamp, SDL_TextInputEvent_windowID(event), SDL_TextInputEvent_text(event)); }
            case SDL_EVENT_KEYMAP_CHANGED -> { consumer.onKeymapChanged(timestamp); }
            case SDL_EVENT_KEYBOARD_ADDED -> { consumer.onKeyboardAdded(timestamp, SDL_KeyboardDeviceEvent_keyboardID(event)); }
            case SDL_EVENT_KEYBOARD_REMOVED -> { consumer.onKeyboardRemoved(timestamp, SDL_KeyboardDeviceEvent_keyboardID(event)); }
            case SDL_EVENT_TEXT_EDITING_CANDIDATES -> { consumer.onTextEditingCandidates(timestamp, SDL_TextEditingCandidatesEvent_windowID(event), SDL_TextEditingCandidatesEvent_candidates(event), SDL_TextEditingCandidatesEvent_selectedCandidate(event), SDL_TextEditingCandidatesEvent_horizontal(event)); }
            case SDL_EVENT_SCREEN_KEYBOARD_SHOWN -> { consumer.onScreenKeyboardShown(timestamp); }
            case SDL_EVENT_SCREEN_KEYBOARD_HIDDEN -> { consumer.onScreenKeyboardHidden(timestamp); }
            case SDL_EVENT_MOUSE_MOTION -> { consumer.onMouseMotion(timestamp, SDL_MouseMotionEvent_windowID(event), SDL_MouseMotionEvent_mouseID(event), SDL_MouseMotionEvent_state(event), SDL_MouseMotionEvent_x(event), SDL_MouseMotionEvent_y(event), SDL_MouseMotionEvent_xrel(event), SDL_MouseMotionEvent_yrel(event)); }
            case SDL_EVENT_MOUSE_BUTTON_DOWN -> { consumer.onMouseButtonDown(timestamp, SDL_MouseButtonEvent_windowID(event), SDL_MouseButtonEvent_mouseID(event), SDL_MouseButtonEvent_button(event), SDL_MouseButtonEvent_clicks(event)); }
            case SDL_EVENT_MOUSE_BUTTON_UP -> { consumer.onMouseButtonUp(timestamp, SDL_MouseButtonEvent_windowID(event), SDL_MouseButtonEvent_mouseID(event), SDL_MouseButtonEvent_button(event), SDL_MouseButtonEvent_clicks(event)); }
            case SDL_EVENT_MOUSE_WHEEL -> { consumer.onMouseWheel(timestamp, SDL_MouseWheelEvent_windowID(event), SDL_MouseWheelEvent_mouseID(event), SDL_MouseWheelEvent_x(event), SDL_MouseWheelEvent_y(event), SDL_MouseWheelEvent_direction(event), SDL_MouseWheelEvent_mouseX(event), SDL_MouseWheelEvent_mouseY(event), SDL_MouseWheelEvent_integerX(event), SDL_MouseWheelEvent_integerY(event)); }
            case SDL_EVENT_MOUSE_ADDED -> { consumer.onMouseAdded(timestamp, SDL_MouseDeviceEvent_mouseID(event)); }
            case SDL_EVENT_MOUSE_REMOVED -> { consumer.onMouseRemoved(timestamp, SDL_MouseDeviceEvent_mouseID(event)); }
            case SDL_EVENT_JOYSTICK_AXIS_MOTION -> { consumer.onJoystickAxisMotion(timestamp, SDL_JoyAxisEvent_joystickID(event), SDL_JoyAxisEvent_axis(event), SDL_JoyAxisEvent_value(event)); }
            case SDL_EVENT_JOYSTICK_BALL_MOTION -> { consumer.onJoystickBallMotion(timestamp, SDL_JoyBallEvent_joystickID(event), SDL_JoyBallEvent_ball(event), SDL_JoyBallEvent_xrel(event), SDL_JoyBallEvent_yrel(event)); }
            case SDL_EVENT_JOYSTICK_HAT_MOTION -> { consumer.onJoystickHatMotion(timestamp, SDL_JoyHatEvent_joystickID(event), SDL_JoyHatEvent_hat(event), SDL_JoyHatEvent_value(event)); }
            case SDL_EVENT_JOYSTICK_BUTTON_DOWN -> { consumer.onJoystickButtonDown(timestamp, SDL_JoyButtonEvent_joystickID(event), SDL_JoyButtonEvent_button(event)); }
            case SDL_EVENT_JOYSTICK_BUTTON_UP -> { consumer.onJoystickButtonUp(timestamp, SDL_JoyButtonEvent_joystickID(event), SDL_JoyButtonEvent_button(event)); }
            case SDL_EVENT_JOYSTICK_ADDED -> { consumer.onJoystickAdded(timestamp, SDL_JoyDeviceEvent_joystickID(event)); }
            case SDL_EVENT_JOYSTICK_REMOVED -> { consumer.onJoystickRemoved(timestamp, SDL_JoyDeviceEvent_joystickID(event)); }
            case SDL_EVENT_JOYSTICK_BATTERY_UPDATED -> { consumer.onJoystickBatteryUpdated(timestamp, SDL_JoyBatteryEvent_joystickID(event), SDL_JoyBatteryEvent_state(event), SDL_JoyBatteryEvent_percent(event)); }
            case SDL_EVENT_JOYSTICK_UPDATE_COMPLETE -> { consumer.onJoystickUpdateComplete(timestamp, SDL_JoyDeviceEvent_joystickID(event)); }
            case SDL_EVENT_GAMEPAD_AXIS_MOTION -> { consumer.onGamepadAxisMotion(timestamp, SDL_GamepadAxisEvent_joystickID(event), SDL_GamepadAxisEvent_axis(event), SDL_GamepadAxisEvent_value(event)); }
            case SDL_EVENT_GAMEPAD_BUTTON_DOWN -> { consumer.onGamepadButtonDown(timestamp, SDL_GamepadButtonEvent_joystickID(event), SDL_GamepadButtonEvent_button(event)); }
            case SDL_EVENT_GAMEPAD_BUTTON_UP -> { consumer.onGamepadButtonUp(timestamp, SDL_GamepadButtonEvent_joystickID(event), SDL_GamepadButtonEvent_button(event)); }
            case SDL_EVENT_GAMEPAD_ADDED -> { consumer.onGamepadAdded(timestamp, SDL_GamepadDeviceEvent_joystickID(event)); }
            case SDL_EVENT_GAMEPAD_REMOVED -> { consumer.onGamepadRemoved(timestamp, SDL_GamepadDeviceEvent_joystickID(event)); }
            case SDL_EVENT_GAMEPAD_REMAPPED -> { consumer.onGamepadRemapped(timestamp, SDL_GamepadDeviceEvent_joystickID(event)); }
            case SDL_EVENT_GAMEPAD_TOUCHPAD_DOWN -> { consumer.onGamepadTouchpadDown(timestamp, SDL_GamepadTouchpadEvent_joystickID(event), SDL_GamepadTouchpadEvent_touchpad(event), SDL_GamepadTouchpadEvent_finger(event), SDL_GamepadTouchpadEvent_x(event), SDL_GamepadTouchpadEvent_y(event), SDL_GamepadTouchpadEvent_pressure(event)); }
            case SDL_EVENT_GAMEPAD_TOUCHPAD_MOTION -> { consumer.onGamepadTouchpadMotion(timestamp, SDL_GamepadTouchpadEvent_joystickID(event), SDL_GamepadTouchpadEvent_touchpad(event), SDL_GamepadTouchpadEvent_finger(event), SDL_GamepadTouchpadEvent_x(event), SDL_GamepadTouchpadEvent_y(event), SDL_GamepadTouchpadEvent_pressure(event)); }
            case SDL_EVENT_GAMEPAD_TOUCHPAD_UP -> { consumer.onGamepadTouchpadUp(timestamp, SDL_GamepadTouchpadEvent_joystickID(event), SDL_GamepadTouchpadEvent_touchpad(event), SDL_GamepadTouchpadEvent_finger(event), SDL_GamepadTouchpadEvent_x(event), SDL_GamepadTouchpadEvent_y(event), SDL_GamepadTouchpadEvent_pressure(event)); }
            case SDL_EVENT_GAMEPAD_SENSOR_UPDATE -> { consumer.onGamepadSensorUpdate(timestamp, SDL_GamepadSensorEvent_joystickID(event), SDL_GamepadSensorEvent_sensor(event), SDL_GamepadSensorEvent_data(event), SDL_GamepadSensorEvent_sensorTimestamp(event)); }
            case SDL_EVENT_GAMEPAD_UPDATE_COMPLETE -> { consumer.onGamepadUpdateComplete(timestamp, SDL_GamepadDeviceEvent_joystickID(event)); }
            case SDL_EVENT_GAMEPAD_STEAM_HANDLE_UPDATED -> { consumer.onGamepadSteamHandleUpdated(timestamp, SDL_GamepadDeviceEvent_joystickID(event)); }
            case SDL_EVENT_FINGER_DOWN -> { consumer.onFingerDown(timestamp, SDL_TouchFingerEvent_touchID(event), SDL_TouchFingerEvent_fingerID(event), SDL_TouchFingerEvent_x(event), SDL_TouchFingerEvent_y(event), SDL_TouchFingerEvent_dx(event), SDL_TouchFingerEvent_dy(event), SDL_TouchFingerEvent_pressure(event), SDL_TouchFingerEvent_windowID(event)); }
            case SDL_EVENT_FINGER_UP -> { consumer.onFingerUp(timestamp, SDL_TouchFingerEvent_touchID(event), SDL_TouchFingerEvent_fingerID(event), SDL_TouchFingerEvent_x(event), SDL_TouchFingerEvent_y(event), SDL_TouchFingerEvent_dx(event), SDL_TouchFingerEvent_dy(event), SDL_TouchFingerEvent_pressure(event), SDL_TouchFingerEvent_windowID(event)); }
            case SDL_EVENT_FINGER_MOTION -> { consumer.onFingerMotion(timestamp, SDL_TouchFingerEvent_touchID(event), SDL_TouchFingerEvent_fingerID(event), SDL_TouchFingerEvent_x(event), SDL_TouchFingerEvent_y(event), SDL_TouchFingerEvent_dx(event), SDL_TouchFingerEvent_dy(event), SDL_TouchFingerEvent_pressure(event), SDL_TouchFingerEvent_windowID(event)); }
            case SDL_EVENT_FINGER_CANCELED -> { consumer.onFingerCanceled(timestamp, SDL_TouchFingerEvent_touchID(event), SDL_TouchFingerEvent_fingerID(event), SDL_TouchFingerEvent_x(event), SDL_TouchFingerEvent_y(event), SDL_TouchFingerEvent_dx(event), SDL_TouchFingerEvent_dy(event), SDL_TouchFingerEvent_pressure(event), SDL_TouchFingerEvent_windowID(event)); }
            case SDL_EVENT_PINCH_BEGIN -> { consumer.onPinchBegin(timestamp, SDL_PinchFingerEvent_scale(event), SDL_PinchFingerEvent_windowID(event)); }
            case SDL_EVENT_PINCH_UPDATE -> { consumer.onPinchUpdate(timestamp, SDL_PinchFingerEvent_scale(event), SDL_PinchFingerEvent_windowID(event)); }
            case SDL_EVENT_PINCH_END -> { consumer.onPinchEnd(timestamp, SDL_PinchFingerEvent_scale(event), SDL_PinchFingerEvent_windowID(event)); }
            case SDL_EVENT_CLIPBOARD_UPDATE -> { consumer.onClipboardUpdate(timestamp, SDL_ClipboardEvent_owner(event), SDL_ClipboardEvent_mimeTypes(event)); }
            case SDL_EVENT_DROP_FILE -> { consumer.onDropFile(timestamp, SDL_DropEvent_windowID(event), SDL_DropEvent_x(event), SDL_DropEvent_y(event), SDL_DropEvent_source(event), SDL_DropEvent_data(event)); }
            case SDL_EVENT_DROP_TEXT -> { consumer.onDropText(timestamp, SDL_DropEvent_windowID(event), SDL_DropEvent_x(event), SDL_DropEvent_y(event), SDL_DropEvent_source(event), SDL_DropEvent_data(event)); }
            case SDL_EVENT_DROP_BEGIN -> { consumer.onDropBegin(timestamp, SDL_DropEvent_windowID(event), SDL_DropEvent_x(event), SDL_DropEvent_y(event), SDL_DropEvent_source(event)); }
            case SDL_EVENT_DROP_COMPLETE -> { consumer.onDropComplete(timestamp, SDL_DropEvent_windowID(event), SDL_DropEvent_x(event), SDL_DropEvent_y(event), SDL_DropEvent_source(event)); }
            case SDL_EVENT_DROP_POSITION -> { consumer.onDropPosition(timestamp, SDL_DropEvent_windowID(event), SDL_DropEvent_x(event), SDL_DropEvent_y(event), SDL_DropEvent_source(event)); }
            case SDL_EVENT_AUDIO_DEVICE_ADDED -> { consumer.onAudioDeviceAdded(timestamp, SDL_AudioDeviceEvent_audioDeviceID(event), SDL_AudioDeviceEvent_recording(event)); }
            case SDL_EVENT_AUDIO_DEVICE_REMOVED -> { consumer.onAudioDeviceRemoved(timestamp, SDL_AudioDeviceEvent_audioDeviceID(event), SDL_AudioDeviceEvent_recording(event)); }
            case SDL_EVENT_AUDIO_DEVICE_FORMAT_CHANGED -> { consumer.onAudioDeviceFormatChanged(timestamp, SDL_AudioDeviceEvent_audioDeviceID(event), SDL_AudioDeviceEvent_recording(event)); }
            case SDL_EVENT_SENSOR_UPDATE -> { consumer.onSensorUpdate(timestamp, SDL_SensorEvent_sensorID(event), SDL_SensorEvent_data(event), SDL_SensorEvent_sensorTimestamp(event)); }
            case SDL_EVENT_PEN_PROXIMITY_IN -> { consumer.onPenProximityIn(timestamp, SDL_PenProximityEvent_windowID(event), SDL_PenProximityEvent_penID(event)); }
            case SDL_EVENT_PEN_PROXIMITY_OUT -> { consumer.onPenProximityOut(timestamp, SDL_PenProximityEvent_windowID(event), SDL_PenProximityEvent_penID(event)); }
            case SDL_EVENT_PEN_DOWN -> { consumer.onPenDown(timestamp, SDL_PenTouchEvent_windowID(event), SDL_PenTouchEvent_penID(event), SDL_PenTouchEvent_penState(event), SDL_PenTouchEvent_x(event), SDL_PenTouchEvent_y(event), SDL_PenTouchEvent_eraser(event)); }
            case SDL_EVENT_PEN_UP -> { consumer.onPenUp(timestamp, SDL_PenTouchEvent_windowID(event), SDL_PenTouchEvent_penID(event), SDL_PenTouchEvent_penState(event), SDL_PenTouchEvent_x(event), SDL_PenTouchEvent_y(event), SDL_PenTouchEvent_eraser(event)); }
            case SDL_EVENT_PEN_BUTTON_DOWN -> { consumer.onPenButtonDown(timestamp, SDL_PenButtonEvent_windowID(event), SDL_PenButtonEvent_penID(event), SDL_PenButtonEvent_penState(event), SDL_PenButtonEvent_x(event), SDL_PenButtonEvent_y(event), SDL_PenButtonEvent_button(event)); }
            case SDL_EVENT_PEN_BUTTON_UP -> { consumer.onPenButtonUp(timestamp, SDL_PenButtonEvent_windowID(event), SDL_PenButtonEvent_penID(event), SDL_PenButtonEvent_penState(event), SDL_PenButtonEvent_x(event), SDL_PenButtonEvent_y(event), SDL_PenButtonEvent_button(event)); }
            case SDL_EVENT_PEN_MOTION -> { consumer.onPenMotion(timestamp, SDL_PenMotionEvent_windowID(event), SDL_PenMotionEvent_penID(event), SDL_PenMotionEvent_penState(event), SDL_PenMotionEvent_x(event), SDL_PenMotionEvent_y(event)); }
            case SDL_EVENT_PEN_AXIS -> { consumer.onPenAxis(timestamp, SDL_PenAxisEvent_windowID(event), SDL_PenAxisEvent_penID(event), SDL_PenAxisEvent_penState(event), SDL_PenAxisEvent_x(event), SDL_PenAxisEvent_y(event), SDL_PenAxisEvent_axis(event), SDL_PenAxisEvent_value(event)); }
            case SDL_EVENT_CAMERA_DEVICE_ADDED -> { consumer.onCameraDeviceAdded(timestamp, SDL_CameraDeviceEvent_cameraID(event)); }
            case SDL_EVENT_CAMERA_DEVICE_REMOVED -> { consumer.onCameraDeviceRemoved(timestamp, SDL_CameraDeviceEvent_cameraID(event)); }
            case SDL_EVENT_CAMERA_DEVICE_APPROVED -> { consumer.onCameraDeviceApproved(timestamp, SDL_CameraDeviceEvent_cameraID(event)); }
            case SDL_EVENT_CAMERA_DEVICE_DENIED -> { consumer.onCameraDeviceDenied(timestamp, SDL_CameraDeviceEvent_cameraID(event)); }
            case SDL_EVENT_RENDER_TARGETS_RESET -> { consumer.onRenderTargetsReset(timestamp, SDL_RenderEvent_windowID(event)); }
            case SDL_EVENT_RENDER_DEVICE_RESET -> { consumer.onRenderDeviceReset(timestamp, SDL_RenderEvent_windowID(event)); }
            case SDL_EVENT_RENDER_DEVICE_LOST -> { consumer.onRenderDeviceLost(timestamp, SDL_RenderEvent_windowID(event)); }
            case SDL_EVENT_PRIVATE0 -> { consumer.onPrivate0(timestamp); }
            case SDL_EVENT_PRIVATE1 -> { consumer.onPrivate1(timestamp); }
            case SDL_EVENT_PRIVATE2 -> { consumer.onPrivate2(timestamp); }
            case SDL_EVENT_PRIVATE3 -> { consumer.onPrivate3(timestamp); }
            default -> {
                if (type >= SDL_EVENT_USER && type <= SDL_EVENT_LAST) {
                    consumer.onUserEvent(
                            timestamp,
                            type,
                            SDL_UserEvent.windowID(event),
                            SDL_UserEvent.code(event),
                            SDL_UserEvent.data1(event),
                            SDL_UserEvent.data2(event)
                    );
                }
            }
        }
    }

    /// `SDL_DisplayEvent.displayID`
    private static int SDL_DisplayEvent_displayID(MemorySegment segment) {
        return SDL_DisplayEvent.displayID(segment);
    }

    /// `SDL_DisplayEvent.data1`
    private static DisplayOrientation SDL_DisplayEvent_newOrientation(MemorySegment segment) {
        var code = SDL_DisplayEvent.data1(segment);
        return DisplayOrientation.fromCode(code);
    }

    /// `SDL_WindowEvent.windowID`
    private static int SDL_WindowEvent_windowID(MemorySegment segment) {
        return SDL_WindowEvent.windowID(segment);
    }

    /// `SDL_WindowEvent.data1`
    private static boolean SDL_WindowEvent_liveResize(MemorySegment segment) {
        return SDL_WindowEvent.data1(segment) != 0;
    }

    /// `SDL_WindowEvent.data1`
    private static int SDL_WindowEvent_x(MemorySegment segment) {
        return SDL_WindowEvent.data1(segment);
    }

    /// `SDL_WindowEvent.data2`
    private static int SDL_WindowEvent_y(MemorySegment segment) {
        return SDL_WindowEvent.data2(segment);
    }

    /// `SDL_WindowEvent.data1`
    private static int SDL_WindowEvent_width(MemorySegment segment) {
        return SDL_WindowEvent.data1(segment);
    }

    /// `SDL_WindowEvent.data2`
    private static int SDL_WindowEvent_height(MemorySegment segment) {
        return SDL_WindowEvent.data2(segment);
    }

    /// `SDL_WindowEvent.data1`
    private static int SDL_WindowEvent_displayID(MemorySegment segment) {
        return SDL_WindowEvent.data1(segment);
    }

    /// `SDL_KeyboardEvent.windowID`
    private static int SDL_KeyboardEvent_windowID(MemorySegment segment) {
        return SDL_KeyboardEvent.windowID(segment);
    }

    /// `SDL_KeyboardEvent.which`
    private static int SDL_KeyboardEvent_keyboardID(MemorySegment segment) {
        return SDL_KeyboardEvent.which(segment);
    }

    /// `SDL_KeyboardEvent.scancode`
    private static Scancode SDL_KeyboardEvent_scancode(MemorySegment segment) {
        var code = SDL_KeyboardEvent.scancode(segment);
        return Scancode.fromCode(code);
    }

    /// `SDL_KeyboardEvent.key`
    private static Keycode SDL_KeyboardEvent_keycode(MemorySegment segment) {
        var code = SDL_KeyboardEvent.key(segment);
        return Keycode.fromCode(code);
    }

    /// `SDL_KeyboardEvent.mod`
    private static @Keymod int SDL_KeyboardEvent_keymod(MemorySegment segment) {
        return SDL_KeyboardEvent.mod(segment);
    }

    /// `SDL_KeyboardEvent.raw`
    private static short SDL_KeyboardEvent_raw(MemorySegment segment) {
        return SDL_KeyboardEvent.raw(segment);
    }

    /// `SDL_KeyboardEvent.repeat`
    private static boolean SDL_KeyboardEvent_repeat(MemorySegment segment) {
        return SDL_KeyboardEvent.repeat(segment);
    }

    /// `SDL_TextEditingEvent.windowID`
    private static int SDL_TextEditingEvent_windowID(MemorySegment segment) {
        return SDL_TextEditingEvent.windowID(segment);
    }

    /// `SDL_TextEditingEvent.text`
    private static String SDL_TextEditingEvent_text(MemorySegment segment) {
        return toString(SDL_TextEditingEvent.text(segment));
    }

    /// `SDL_TextEditingEvent.start`
    private static int SDL_TextEditingEvent_start(MemorySegment segment) {
        return SDL_TextEditingEvent.start(segment);
    }

    /// `SDL_TextEditingEvent.length`
    private static int SDL_TextEditingEvent_length(MemorySegment segment) {
        return SDL_TextEditingEvent.length(segment);
    }

    /// `SDL_TextInputEvent.windowID`
    private static int SDL_TextInputEvent_windowID(MemorySegment segment) {
        return SDL_TextInputEvent.windowID(segment);
    }

    /// `SDL_TextInputEvent.text`
    private static String SDL_TextInputEvent_text(MemorySegment segment) {
        return toString(SDL_TextInputEvent.text(segment));
    }

    /// `SDL_KeyboardDeviceEvent.which`
    private static int SDL_KeyboardDeviceEvent_keyboardID(MemorySegment segment) {
        return SDL_KeyboardDeviceEvent.which(segment);
    }

    /// `SDL_TextEditingCandidatesEvent.windowID`
    private static int SDL_TextEditingCandidatesEvent_windowID(MemorySegment segment) {
        return SDL_TextEditingCandidatesEvent.windowID(segment);
    }

    /// `SDL_TextEditingCandidatesEvent.candidates`
    private static List<String> SDL_TextEditingCandidatesEvent_candidates(MemorySegment segment) {
        return toStringList(
                SDL_TextEditingCandidatesEvent.candidates(segment),
                SDL_TextEditingCandidatesEvent.num_candidates(segment)
        );
    }

    /// `SDL_TextEditingCandidatesEvent.selected_candidate`
    private static int SDL_TextEditingCandidatesEvent_selectedCandidate(MemorySegment segment) {
        return SDL_TextEditingCandidatesEvent.selected_candidate(segment);
    }

    /// `SDL_TextEditingCandidatesEvent.horizontal`
    private static boolean SDL_TextEditingCandidatesEvent_horizontal(MemorySegment segment) {
        return SDL_TextEditingCandidatesEvent.horizontal(segment);
    }

    /// `SDL_MouseMotionEvent.windowID`
    private static int SDL_MouseMotionEvent_windowID(MemorySegment segment) {
        return SDL_MouseMotionEvent.windowID(segment);
    }

    /// `SDL_MouseMotionEvent.which`
    private static int SDL_MouseMotionEvent_mouseID(MemorySegment segment) {
        return SDL_MouseMotionEvent.which(segment);
    }

    /// `SDL_MouseMotionEvent.state`
    private static @MouseButtonFlags int SDL_MouseMotionEvent_state(MemorySegment segment) {
        return (Integer) u8(SDL_MouseMotionEvent.state(segment));
    }

    /// `SDL_MouseMotionEvent.x`
    private static float SDL_MouseMotionEvent_x(MemorySegment segment) {
        return SDL_MouseMotionEvent.x(segment);
    }

    /// `SDL_MouseMotionEvent.y`
    private static float SDL_MouseMotionEvent_y(MemorySegment segment) {
        return SDL_MouseMotionEvent.y(segment);
    }

    /// `SDL_MouseMotionEvent.xrel`
    private static float SDL_MouseMotionEvent_xrel(MemorySegment segment) {
        return SDL_MouseMotionEvent.xrel(segment);
    }

    /// `SDL_MouseMotionEvent.yrel`
    private static float SDL_MouseMotionEvent_yrel(MemorySegment segment) {
        return SDL_MouseMotionEvent.yrel(segment);
    }

    /// `SDL_MouseButtonEvent.windowID`
    private static int SDL_MouseButtonEvent_windowID(MemorySegment segment) {
        return SDL_MouseButtonEvent.windowID(segment);
    }

    /// `SDL_MouseButtonEvent.which`
    private static int SDL_MouseButtonEvent_mouseID(MemorySegment segment) {
        return SDL_MouseButtonEvent.which(segment);
    }

    /// `SDL_MouseButtonEvent.button`
    private static @MouseButton int SDL_MouseButtonEvent_button(MemorySegment segment) {
        return (Integer) u8(SDL_MouseButtonEvent.button(segment));
    }

    /// `SDL_MouseButtonEvent.clicks`
    private static int SDL_MouseButtonEvent_clicks(MemorySegment segment) {
        return u8(SDL_MouseButtonEvent.clicks(segment));
    }

    /// `SDL_MouseWheelEvent.windowID`
    private static int SDL_MouseWheelEvent_windowID(MemorySegment segment) {
        return SDL_MouseWheelEvent.windowID(segment);
    }

    /// `SDL_MouseWheelEvent.which`
    private static int SDL_MouseWheelEvent_mouseID(MemorySegment segment) {
        return SDL_MouseWheelEvent.which(segment);
    }

    /// `SDL_MouseWheelEvent.x`
    private static float SDL_MouseWheelEvent_x(MemorySegment segment) {
        return SDL_MouseWheelEvent.x(segment);
    }

    /// `SDL_MouseWheelEvent.y`
    private static float SDL_MouseWheelEvent_y(MemorySegment segment) {
        return SDL_MouseWheelEvent.y(segment);
    }

    /// `SDL_MouseWheelEvent.direction`
    private static MouseWheelDirection SDL_MouseWheelEvent_direction(MemorySegment segment) {
        var code = SDL_MouseWheelEvent.direction(segment);
        return MouseWheelDirection.fromCode(code);
    }

    /// `SDL_MouseWheelEvent.mouse_x`
    private static float SDL_MouseWheelEvent_mouseX(MemorySegment segment) {
        return SDL_MouseWheelEvent.mouse_x(segment);
    }

    /// `SDL_MouseWheelEvent.mouse_y`
    private static float SDL_MouseWheelEvent_mouseY(MemorySegment segment) {
        return SDL_MouseWheelEvent.mouse_y(segment);
    }

    /// `SDL_MouseWheelEvent.integer_x`
    private static int SDL_MouseWheelEvent_integerX(MemorySegment segment) {
        return SDL_MouseWheelEvent.integer_x(segment);
    }

    /// `SDL_MouseWheelEvent.integer_y`
    private static int SDL_MouseWheelEvent_integerY(MemorySegment segment) {
        return SDL_MouseWheelEvent.integer_y(segment);
    }

    /// `SDL_MouseDeviceEvent.which`
    private static int SDL_MouseDeviceEvent_mouseID(MemorySegment segment) {
        return SDL_MouseDeviceEvent.which(segment);
    }

    /// `SDL_JoyAxisEvent.which`
    private static int SDL_JoyAxisEvent_joystickID(MemorySegment segment) {
        return SDL_JoyAxisEvent.which(segment);
    }

    /// `SDL_JoyAxisEvent.axis`
    private static int SDL_JoyAxisEvent_axis(MemorySegment segment) {
        return u8(SDL_JoyAxisEvent.axis(segment));
    }

    /// `SDL_JoyAxisEvent.value`
    private static int SDL_JoyAxisEvent_value(MemorySegment segment) {
        return SDL_JoyAxisEvent.value(segment);
    }

    /// `SDL_JoyBallEvent.which`
    private static int SDL_JoyBallEvent_joystickID(MemorySegment segment) {
        return SDL_JoyBallEvent.which(segment);
    }

    /// `SDL_JoyBallEvent.ball`
    private static int SDL_JoyBallEvent_ball(MemorySegment segment) {
        return u8(SDL_JoyBallEvent.ball(segment));
    }

    /// `SDL_JoyBallEvent.xrel`
    private static int SDL_JoyBallEvent_xrel(MemorySegment segment) {
        return SDL_JoyBallEvent.xrel(segment);
    }

    /// `SDL_JoyBallEvent.yrel`
    private static int SDL_JoyBallEvent_yrel(MemorySegment segment) {
        return SDL_JoyBallEvent.yrel(segment);
    }

    /// `SDL_JoyHatEvent.which`
    private static int SDL_JoyHatEvent_joystickID(MemorySegment segment) {
        return SDL_JoyHatEvent.which(segment);
    }

    /// `SDL_JoyHatEvent.hat`
    private static int SDL_JoyHatEvent_hat(MemorySegment segment) {
        return u8(SDL_JoyHatEvent.hat(segment));
    }

    /// `SDL_JoyHatEvent.value`
    private static int SDL_JoyHatEvent_value(MemorySegment segment) {
        return u8(SDL_JoyHatEvent.value(segment));
    }

    /// `SDL_JoyButtonEvent.which`
    private static int SDL_JoyButtonEvent_joystickID(MemorySegment segment) {
        return SDL_JoyButtonEvent.which(segment);
    }

    /// `SDL_JoyButtonEvent.button`
    private static int SDL_JoyButtonEvent_button(MemorySegment segment) {
        return u8(SDL_JoyButtonEvent.button(segment));
    }

    /// `SDL_JoyDeviceEvent.which`
    private static int SDL_JoyDeviceEvent_joystickID(MemorySegment segment) {
        return SDL_JoyDeviceEvent.which(segment);
    }

    /// `SDL_JoyBatteryEvent.which`
    private static int SDL_JoyBatteryEvent_joystickID(MemorySegment segment) {
        return SDL_JoyBatteryEvent.which(segment);
    }

    /// `SDL_JoyBatteryEvent.state`
    private static PowerState SDL_JoyBatteryEvent_state(MemorySegment segment) {
        var code = SDL_JoyBatteryEvent.state(segment);
        return PowerState.fromCode(code);
    }

    /// `SDL_JoyBatteryEvent.percent`
    private static int SDL_JoyBatteryEvent_percent(MemorySegment segment) {
        return SDL_JoyBatteryEvent.percent(segment);
    }

    /// `SDL_GamepadAxisEvent.which`
    private static int SDL_GamepadAxisEvent_joystickID(MemorySegment segment) {
        return SDL_GamepadAxisEvent.which(segment);
    }

    /// `SDL_GamepadAxisEvent.axis`
    private static int SDL_GamepadAxisEvent_axis(MemorySegment segment) {
        return u8(SDL_GamepadAxisEvent.axis(segment));
    }

    /// `SDL_GamepadAxisEvent.value`
    private static int SDL_GamepadAxisEvent_value(MemorySegment segment) {
        return SDL_GamepadAxisEvent.value(segment);
    }

    /// `SDL_GamepadButtonEvent.which`
    private static int SDL_GamepadButtonEvent_joystickID(MemorySegment segment) {
        return SDL_GamepadButtonEvent.which(segment);
    }

    /// `SDL_GamepadButtonEvent.button`
    private static int SDL_GamepadButtonEvent_button(MemorySegment segment) {
        return u8(SDL_GamepadButtonEvent.button(segment));
    }

    /// `SDL_GamepadDeviceEvent.which`
    private static int SDL_GamepadDeviceEvent_joystickID(MemorySegment segment) {
        return SDL_GamepadDeviceEvent.which(segment);
    }

    /// `SDL_GamepadTouchpadEvent.which`
    private static int SDL_GamepadTouchpadEvent_joystickID(MemorySegment segment) {
        return SDL_GamepadTouchpadEvent.which(segment);
    }

    /// `SDL_GamepadTouchpadEvent.touchpad`
    private static int SDL_GamepadTouchpadEvent_touchpad(MemorySegment segment) {
        return SDL_GamepadTouchpadEvent.touchpad(segment);
    }

    /// `SDL_GamepadTouchpadEvent.finger`
    private static int SDL_GamepadTouchpadEvent_finger(MemorySegment segment) {
        return SDL_GamepadTouchpadEvent.finger(segment);
    }

    /// `SDL_GamepadTouchpadEvent.x`
    private static float SDL_GamepadTouchpadEvent_x(MemorySegment segment) {
        return SDL_GamepadTouchpadEvent.x(segment);
    }

    /// `SDL_GamepadTouchpadEvent.y`
    private static float SDL_GamepadTouchpadEvent_y(MemorySegment segment) {
        return SDL_GamepadTouchpadEvent.y(segment);
    }

    /// `SDL_GamepadTouchpadEvent.pressure`
    private static float SDL_GamepadTouchpadEvent_pressure(MemorySegment segment) {
        return SDL_GamepadTouchpadEvent.pressure(segment);
    }

    /// `SDL_GamepadSensorEvent.which`
    private static int SDL_GamepadSensorEvent_joystickID(MemorySegment segment) {
        return SDL_GamepadSensorEvent.which(segment);
    }

    /// `SDL_GamepadSensorEvent.sensor`
    private static int SDL_GamepadSensorEvent_sensor(MemorySegment segment) {
        return SDL_GamepadSensorEvent.sensor(segment);
    }

    /// `SDL_GamepadSensorEvent.data`
    private static float[] SDL_GamepadSensorEvent_data(MemorySegment segment) {
        return toFloatArray(SDL_GamepadSensorEvent.data(segment), 3);
    }

    /// `SDL_GamepadSensorEvent.sensor_timestamp`
    private static long SDL_GamepadSensorEvent_sensorTimestamp(MemorySegment segment) {
        return SDL_GamepadSensorEvent.sensor_timestamp(segment);
    }

    /// `SDL_TouchFingerEvent.touchID`
    private static long SDL_TouchFingerEvent_touchID(MemorySegment segment) {
        return SDL_TouchFingerEvent.touchID(segment);
    }

    /// `SDL_TouchFingerEvent.fingerID`
    private static long SDL_TouchFingerEvent_fingerID(MemorySegment segment) {
        return SDL_TouchFingerEvent.fingerID(segment);
    }

    /// `SDL_TouchFingerEvent.x`
    private static float SDL_TouchFingerEvent_x(MemorySegment segment) {
        return SDL_TouchFingerEvent.x(segment);
    }

    /// `SDL_TouchFingerEvent.y`
    private static float SDL_TouchFingerEvent_y(MemorySegment segment) {
        return SDL_TouchFingerEvent.y(segment);
    }

    /// `SDL_TouchFingerEvent.dx`
    private static float SDL_TouchFingerEvent_dx(MemorySegment segment) {
        return SDL_TouchFingerEvent.dx(segment);
    }

    /// `SDL_TouchFingerEvent.dy`
    private static float SDL_TouchFingerEvent_dy(MemorySegment segment) {
        return SDL_TouchFingerEvent.dy(segment);
    }

    /// `SDL_TouchFingerEvent.pressure`
    private static float SDL_TouchFingerEvent_pressure(MemorySegment segment) {
        return SDL_TouchFingerEvent.pressure(segment);
    }

    /// `SDL_TouchFingerEvent.windowID`
    private static int SDL_TouchFingerEvent_windowID(MemorySegment segment) {
        return SDL_TouchFingerEvent.windowID(segment);
    }

    /// `SDL_PinchFingerEvent.scale`
    private static float SDL_PinchFingerEvent_scale(MemorySegment segment) {
        return SDL_PinchFingerEvent.scale(segment);
    }

    /// `SDL_PinchFingerEvent.windowID`
    private static int SDL_PinchFingerEvent_windowID(MemorySegment segment) {
        return SDL_PinchFingerEvent.windowID(segment);
    }

    /// `SDL_ClipboardEvent.owner`
    private static boolean SDL_ClipboardEvent_owner(MemorySegment segment) {
        return SDL_ClipboardEvent.owner(segment);
    }

    /// `SDL_ClipboardEvent.mime_types`
    private static List<String> SDL_ClipboardEvent_mimeTypes(MemorySegment segment) {
        return toStringList(
                SDL_ClipboardEvent.mime_types(segment),
                SDL_ClipboardEvent.num_mime_types(segment)
        );
    }

    /// `SDL_DropEvent.windowID`
    private static int SDL_DropEvent_windowID(MemorySegment segment) {
        return SDL_DropEvent.windowID(segment);
    }

    /// `SDL_DropEvent.x`
    private static float SDL_DropEvent_x(MemorySegment segment) {
        return SDL_DropEvent.x(segment);
    }

    /// `SDL_DropEvent.y`
    private static float SDL_DropEvent_y(MemorySegment segment) {
        return SDL_DropEvent.y(segment);
    }

    /// `SDL_DropEvent.source`
    private static @Nullable String SDL_DropEvent_source(MemorySegment segment) {
        return toStringOrNull(SDL_DropEvent.source(segment));
    }

    /// `SDL_DropEvent.data`
    private static String SDL_DropEvent_data(MemorySegment segment) {
        return toString(SDL_DropEvent.data(segment));
    }

    /// `SDL_AudioDeviceEvent.which`
    private static int SDL_AudioDeviceEvent_audioDeviceID(MemorySegment segment) {
        return SDL_AudioDeviceEvent.which(segment);
    }

    /// `SDL_AudioDeviceEvent.recording`
    private static boolean SDL_AudioDeviceEvent_recording(MemorySegment segment) {
        return SDL_AudioDeviceEvent.recording(segment);
    }

    /// `SDL_SensorEvent.which`
    private static int SDL_SensorEvent_sensorID(MemorySegment segment) {
        return SDL_SensorEvent.which(segment);
    }

    /// `SDL_SensorEvent.data`
    private static float[] SDL_SensorEvent_data(MemorySegment segment) {
        return toFloatArray(SDL_SensorEvent.data(segment), 6);
    }

    /// `SDL_SensorEvent.sensor_timestamp`
    private static long SDL_SensorEvent_sensorTimestamp(MemorySegment segment) {
        return SDL_SensorEvent.sensor_timestamp(segment);
    }

    /// `SDL_PenProximityEvent.windowID`
    private static int SDL_PenProximityEvent_windowID(MemorySegment segment) {
        return SDL_PenProximityEvent.windowID(segment);
    }

    /// `SDL_PenProximityEvent.which`
    private static int SDL_PenProximityEvent_penID(MemorySegment segment) {
        return SDL_PenProximityEvent.which(segment);
    }

    /// `SDL_PenTouchEvent.windowID`
    private static int SDL_PenTouchEvent_windowID(MemorySegment segment) {
        return SDL_PenTouchEvent.windowID(segment);
    }

    /// `SDL_PenTouchEvent.which`
    private static int SDL_PenTouchEvent_penID(MemorySegment segment) {
        return SDL_PenTouchEvent.which(segment);
    }

    /// `SDL_PenTouchEvent.pen_state`
    private static @PenInputFlags int SDL_PenTouchEvent_penState(MemorySegment segment) {
        return (Integer) SDL_PenTouchEvent.pen_state(segment);
    }

    /// `SDL_PenTouchEvent.x`
    private static float SDL_PenTouchEvent_x(MemorySegment segment) {
        return SDL_PenTouchEvent.x(segment);
    }

    /// `SDL_PenTouchEvent.y`
    private static float SDL_PenTouchEvent_y(MemorySegment segment) {
        return SDL_PenTouchEvent.y(segment);
    }

    /// `SDL_PenTouchEvent.eraser`
    private static boolean SDL_PenTouchEvent_eraser(MemorySegment segment) {
        return SDL_PenTouchEvent.eraser(segment);
    }

    /// `SDL_PenButtonEvent.windowID`
    private static int SDL_PenButtonEvent_windowID(MemorySegment segment) {
        return SDL_PenButtonEvent.windowID(segment);
    }

    /// `SDL_PenButtonEvent.which`
    private static int SDL_PenButtonEvent_penID(MemorySegment segment) {
        return SDL_PenButtonEvent.which(segment);
    }

    /// `SDL_PenButtonEvent.pen_state`
    private static @PenInputFlags int SDL_PenButtonEvent_penState(MemorySegment segment) {
        return (Integer) SDL_PenButtonEvent.pen_state(segment);
    }

    /// `SDL_PenButtonEvent.x`
    private static float SDL_PenButtonEvent_x(MemorySegment segment) {
        return SDL_PenButtonEvent.x(segment);
    }

    /// `SDL_PenButtonEvent.y`
    private static float SDL_PenButtonEvent_y(MemorySegment segment) {
        return SDL_PenButtonEvent.y(segment);
    }

    /// `SDL_PenButtonEvent.button`
    private static int SDL_PenButtonEvent_button(MemorySegment segment) {
        return u8(SDL_PenButtonEvent.button(segment));
    }

    /// `SDL_PenMotionEvent.windowID`
    private static int SDL_PenMotionEvent_windowID(MemorySegment segment) {
        return SDL_PenMotionEvent.windowID(segment);
    }

    /// `SDL_PenMotionEvent.which`
    private static int SDL_PenMotionEvent_penID(MemorySegment segment) {
        return SDL_PenMotionEvent.which(segment);
    }

    /// `SDL_PenMotionEvent.pen_state`
    private static @PenInputFlags int SDL_PenMotionEvent_penState(MemorySegment segment) {
        return (Integer) SDL_PenMotionEvent.pen_state(segment);
    }

    /// `SDL_PenMotionEvent.x`
    private static float SDL_PenMotionEvent_x(MemorySegment segment) {
        return SDL_PenMotionEvent.x(segment);
    }

    /// `SDL_PenMotionEvent.y`
    private static float SDL_PenMotionEvent_y(MemorySegment segment) {
        return SDL_PenMotionEvent.y(segment);
    }

    /// `SDL_PenAxisEvent.windowID`
    private static int SDL_PenAxisEvent_windowID(MemorySegment segment) {
        return SDL_PenAxisEvent.windowID(segment);
    }

    /// `SDL_PenAxisEvent.which`
    private static int SDL_PenAxisEvent_penID(MemorySegment segment) {
        return SDL_PenAxisEvent.which(segment);
    }

    /// `SDL_PenAxisEvent.pen_state`
    private static @PenInputFlags int SDL_PenAxisEvent_penState(MemorySegment segment) {
        return (Integer) SDL_PenAxisEvent.pen_state(segment);
    }

    /// `SDL_PenAxisEvent.x`
    private static float SDL_PenAxisEvent_x(MemorySegment segment) {
        return SDL_PenAxisEvent.x(segment);
    }

    /// `SDL_PenAxisEvent.y`
    private static float SDL_PenAxisEvent_y(MemorySegment segment) {
        return SDL_PenAxisEvent.y(segment);
    }

    /// `SDL_PenAxisEvent.axis`
    private static PenAxis SDL_PenAxisEvent_axis(MemorySegment segment) {
        var code = SDL_PenAxisEvent.axis(segment);
        return PenAxis.fromCode(code);
    }

    /// `SDL_PenAxisEvent.value`
    private static float SDL_PenAxisEvent_value(MemorySegment segment) {
        return SDL_PenAxisEvent.value(segment);
    }

    /// `SDL_CameraDeviceEvent.which`
    private static int SDL_CameraDeviceEvent_cameraID(MemorySegment segment) {
        return SDL_CameraDeviceEvent.which(segment);
    }

    /// `SDL_RenderEvent.windowID`
    private static int SDL_RenderEvent_windowID(MemorySegment segment) {
        return SDL_RenderEvent.windowID(segment);
    }

    /// `SDL_UserEvent.windowID`
    private static int SDL_UserEvent_windowID(MemorySegment segment) {
        return SDL_UserEvent.windowID(segment);
    }

    /// `SDL_UserEvent.code`
    private static int SDL_UserEvent_code(MemorySegment segment) {
        return SDL_UserEvent.code(segment);
    }

    /// `SDL_UserEvent.data1`
    private static MemorySegment SDL_UserEvent_data1(MemorySegment segment) {
        return SDL_UserEvent.data1(segment);
    }

    /// `SDL_UserEvent.data2`
    private static MemorySegment SDL_UserEvent_data2(MemorySegment segment) {
        return SDL_UserEvent.data2(segment);
    }



    private static int u8(int value) {
        return value;
    }

    private static String toString(MemorySegment segment) {
        if (segment.address() == 0) {
            return "";
        }
        return segment.getString(0, StandardCharsets.UTF_8);
    }

    private static @Nullable String toStringOrNull(MemorySegment segment) {
        if (segment.address() == 0) {
            return null;
        }
        return segment.getString(0, StandardCharsets.UTF_8);
    }

    private static List<String> toStringList(MemorySegment segment, int count) {
        if (count <= 0 || segment.address() == 0) {
            return List.of();
        }
        var result = new ArrayList<String>(count);
        for (var i = 0; i < count; i++) {
            var entry = segment.getAtIndex(ValueLayout.ADDRESS, i).reinterpret(Integer.MAX_VALUE);
            result.add(toString(entry));
        }
        return List.copyOf(result); // make immutable
    }

    private static float[] toFloatArray(MemorySegment segment, int count) {
        if (count <= 0) {
            return new float[0];
        }
        var values = new float[count];
        if (segment.address() == 0) {
            return values;
        }
        for (var i = 0; i < count; i++) {
            values[i] = segment.getAtIndex(ValueLayout.JAVA_FLOAT, i);
        }
        return values;
    }

}
