package io.github.plixo2.sodalite.category.events;

import org.libsdl.sdl.SDL_CommonEvent;
import org.libsdl.sdl.SDL_Event;

import java.lang.foreign.MemorySegment;

import static org.libsdl.sdl.SDL3_h.*;

public class EventDispatch {

    static void dispatch(
            EventConsumer consumer,
            MemorySegment eventSegment
    ) {
        var type = SDL_Event.type(eventSegment);
        var timestamp = SDL_CommonEvent.timestamp(SDL_Event.common(eventSegment));

        switch (type) {
                case SDL_EVENT_QUIT -> {
                    consumer.onQuit(timestamp);
                }
                case SDL_EVENT_TERMINATING -> {
                    consumer.onTerminating(timestamp);
                }
                case SDL_EVENT_LOW_MEMORY -> {
                    consumer.onLowMemory(timestamp);
                }
                case SDL_EVENT_WILL_ENTER_BACKGROUND -> {
                    consumer.onWillEnterBackground(timestamp);
                }
                case SDL_EVENT_DID_ENTER_BACKGROUND -> {
                    consumer.onDidEnterBackground(timestamp);
                }
                case SDL_EVENT_WILL_ENTER_FOREGROUND -> {
                    consumer.onWillEnterForeground(timestamp);
                }
                case SDL_EVENT_DID_ENTER_FOREGROUND -> {
                    consumer.onDidEnterForeground(timestamp);
                }
                case SDL_EVENT_LOCALE_CHANGED -> {
                    consumer.onLocaleChanged(timestamp);
                }
                case SDL_EVENT_SYSTEM_THEME_CHANGED -> {
                    consumer.onSystemThemeChanged(timestamp);
                }
                case SDL_EVENT_DISPLAY_ORIENTATION -> {
                    consumer.onDisplayOrientation(timestamp);
                }
                case SDL_EVENT_DISPLAY_ADDED -> {
                    consumer.onDisplayAdded(timestamp);
                }
                case SDL_EVENT_DISPLAY_REMOVED -> {
                    consumer.onDisplayRemoved(timestamp);
                }
                case SDL_EVENT_DISPLAY_MOVED -> {
                    consumer.onDisplayMoved(timestamp);
                }
                case SDL_EVENT_DISPLAY_DESKTOP_MODE_CHANGED -> {
                    consumer.onDisplayDesktopModeChanged(timestamp);
                }
                case SDL_EVENT_DISPLAY_CURRENT_MODE_CHANGED -> {
                    consumer.onDisplayCurrentModeChanged(timestamp);
                }
                case SDL_EVENT_DISPLAY_CONTENT_SCALE_CHANGED -> {
                    consumer.onDisplayContentScaleChanged(timestamp);
                }
                case SDL_EVENT_DISPLAY_USABLE_BOUNDS_CHANGED -> {
                    consumer.onDisplayUsableBoundsChanged(timestamp);
                }
                case SDL_EVENT_WINDOW_SHOWN -> {
                    consumer.onWindowShown(timestamp);
                }
                case SDL_EVENT_WINDOW_HIDDEN -> {
                    consumer.onWindowHidden(timestamp);
                }
                case SDL_EVENT_WINDOW_EXPOSED -> {
                    consumer.onWindowExposed(timestamp);
                }
                case SDL_EVENT_WINDOW_MOVED -> {
                    consumer.onWindowMoved(timestamp);
                }
                case SDL_EVENT_WINDOW_RESIZED -> {
                    consumer.onWindowResized(timestamp);
                }
                case SDL_EVENT_WINDOW_PIXEL_SIZE_CHANGED -> {
                    consumer.onWindowPixelSizeChanged(timestamp);
                }
                case SDL_EVENT_WINDOW_METAL_VIEW_RESIZED -> {
                    consumer.onWindowMetalViewResized(timestamp);
                }
                case SDL_EVENT_WINDOW_MINIMIZED -> {
                    consumer.onWindowMinimized(timestamp);
                }
                case SDL_EVENT_WINDOW_MAXIMIZED -> {
                    consumer.onWindowMaximized(timestamp);
                }
                case SDL_EVENT_WINDOW_RESTORED -> {
                    consumer.onWindowRestored(timestamp);
                }
                case SDL_EVENT_WINDOW_MOUSE_ENTER -> {
                    consumer.onWindowMouseEnter(timestamp);
                }
                case SDL_EVENT_WINDOW_MOUSE_LEAVE -> {
                    consumer.onWindowMouseLeave(timestamp);
                }
                case SDL_EVENT_WINDOW_FOCUS_GAINED -> {
                    consumer.onWindowFocusGained(timestamp);
                }
                case SDL_EVENT_WINDOW_FOCUS_LOST -> {
                    consumer.onWindowFocusLost(timestamp);
                }
                case SDL_EVENT_WINDOW_CLOSE_REQUESTED -> {
                    consumer.onWindowCloseRequested(timestamp);
                }
                case SDL_EVENT_WINDOW_HIT_TEST -> {
                    consumer.onWindowHitTest(timestamp);
                }
                case SDL_EVENT_WINDOW_ICCPROF_CHANGED -> {
                    consumer.onWindowIccprofChanged(timestamp);
                }
                case SDL_EVENT_WINDOW_DISPLAY_CHANGED -> {
                    consumer.onWindowDisplayChanged(timestamp);
                }
                case SDL_EVENT_WINDOW_DISPLAY_SCALE_CHANGED -> {
                    consumer.onWindowDisplayScaleChanged(timestamp);
                }
                case SDL_EVENT_WINDOW_SAFE_AREA_CHANGED -> {
                    consumer.onWindowSafeAreaChanged(timestamp);
                }
                case SDL_EVENT_WINDOW_OCCLUDED -> {
                    consumer.onWindowOccluded(timestamp);
                }
                case SDL_EVENT_WINDOW_ENTER_FULLSCREEN -> {
                    consumer.onWindowEnterFullscreen(timestamp);
                }
                case SDL_EVENT_WINDOW_LEAVE_FULLSCREEN -> {
                    consumer.onWindowLeaveFullscreen(timestamp);
                }
                case SDL_EVENT_WINDOW_DESTROYED -> {
                    consumer.onWindowDestroyed(timestamp);
                }
                case SDL_EVENT_WINDOW_HDR_STATE_CHANGED -> {
                    consumer.onWindowHdrStateChanged(timestamp);
                }
                case SDL_EVENT_KEY_DOWN -> {
                    consumer.onKeyDown(timestamp);
                }
                case SDL_EVENT_KEY_UP -> {
                    consumer.onKeyUp(timestamp);
                }
                case SDL_EVENT_TEXT_EDITING -> {
                    consumer.onTextEditing(timestamp);
                }
                case SDL_EVENT_TEXT_INPUT -> {
                    consumer.onTextInput(timestamp);
                }
                case SDL_EVENT_KEYMAP_CHANGED -> {
                    consumer.onKeymapChanged(timestamp);
                }
                case SDL_EVENT_KEYBOARD_ADDED -> {
                    consumer.onKeyboardAdded(timestamp);
                }
                case SDL_EVENT_KEYBOARD_REMOVED -> {
                    consumer.onKeyboardRemoved(timestamp);
                }
                case SDL_EVENT_TEXT_EDITING_CANDIDATES -> {
                    consumer.onTextEditingCandidates(timestamp);
                }
                case SDL_EVENT_SCREEN_KEYBOARD_SHOWN -> {
                    consumer.onScreenKeyboardShown(timestamp);
                }
                case SDL_EVENT_SCREEN_KEYBOARD_HIDDEN -> {
                    consumer.onScreenKeyboardHidden(timestamp);
                }
                case SDL_EVENT_MOUSE_MOTION -> {
                    consumer.onMouseMotion(timestamp);
                }
                case SDL_EVENT_MOUSE_BUTTON_DOWN -> {
                    consumer.onMouseButtonDown(timestamp);
                }
                case SDL_EVENT_MOUSE_BUTTON_UP -> {
                    consumer.onMouseButtonUp(timestamp);
                }
                case SDL_EVENT_MOUSE_WHEEL -> {
                    consumer.onMouseWheel(timestamp);
                }
                case SDL_EVENT_MOUSE_ADDED -> {
                    consumer.onMouseAdded(timestamp);
                }
                case SDL_EVENT_MOUSE_REMOVED -> {
                    consumer.onMouseRemoved(timestamp);
                }
                case SDL_EVENT_JOYSTICK_AXIS_MOTION -> {
                    consumer.onJoystickAxisMotion(timestamp);
                }
                case SDL_EVENT_JOYSTICK_BALL_MOTION -> {
                    consumer.onJoystickBallMotion(timestamp);
                }
                case SDL_EVENT_JOYSTICK_HAT_MOTION -> {
                    consumer.onJoystickHatMotion(timestamp);
                }
                case SDL_EVENT_JOYSTICK_BUTTON_DOWN -> {
                    consumer.onJoystickButtonDown(timestamp);
                }
                case SDL_EVENT_JOYSTICK_BUTTON_UP -> {
                    consumer.onJoystickButtonUp(timestamp);
                }
                case SDL_EVENT_JOYSTICK_ADDED -> {
                    consumer.onJoystickAdded(timestamp);
                }
                case SDL_EVENT_JOYSTICK_REMOVED -> {
                    consumer.onJoystickRemoved(timestamp);
                }
                case SDL_EVENT_JOYSTICK_BATTERY_UPDATED -> {
                    consumer.onJoystickBatteryUpdated(timestamp);
                }
                case SDL_EVENT_JOYSTICK_UPDATE_COMPLETE -> {
                    consumer.onJoystickUpdateComplete(timestamp);
                }
                case SDL_EVENT_GAMEPAD_AXIS_MOTION -> {
                    consumer.onGamepadAxisMotion(timestamp);
                }
                case SDL_EVENT_GAMEPAD_BUTTON_DOWN -> {
                    consumer.onGamepadButtonDown(timestamp);
                }
                case SDL_EVENT_GAMEPAD_BUTTON_UP -> {
                    consumer.onGamepadButtonUp(timestamp);
                }
                case SDL_EVENT_GAMEPAD_ADDED -> {
                    consumer.onGamepadAdded(timestamp);
                }
                case SDL_EVENT_GAMEPAD_REMOVED -> {
                    consumer.onGamepadRemoved(timestamp);
                }
                case SDL_EVENT_GAMEPAD_REMAPPED -> {
                    consumer.onGamepadRemapped(timestamp);
                }
                case SDL_EVENT_GAMEPAD_TOUCHPAD_DOWN -> {
                    consumer.onGamepadTouchpadDown(timestamp);
                }
                case SDL_EVENT_GAMEPAD_TOUCHPAD_MOTION -> {
                    consumer.onGamepadTouchpadMotion(timestamp);
                }
                case SDL_EVENT_GAMEPAD_TOUCHPAD_UP -> {
                    consumer.onGamepadTouchpadUp(timestamp);
                }
                case SDL_EVENT_GAMEPAD_SENSOR_UPDATE -> {
                    consumer.onGamepadSensorUpdate(timestamp);
                }
                case SDL_EVENT_GAMEPAD_UPDATE_COMPLETE -> {
                    consumer.onGamepadUpdateComplete(timestamp);
                }
                case SDL_EVENT_GAMEPAD_STEAM_HANDLE_UPDATED -> {
                    consumer.onGamepadSteamHandleUpdated(timestamp);
                }
                case SDL_EVENT_FINGER_DOWN -> {
                    consumer.onFingerDown(timestamp);
                }
                case SDL_EVENT_FINGER_UP -> {
                    consumer.onFingerUp(timestamp);
                }
                case SDL_EVENT_FINGER_MOTION -> {
                    consumer.onFingerMotion(timestamp);
                }
                case SDL_EVENT_FINGER_CANCELED -> {
                    consumer.onFingerCanceled(timestamp);
                }
                case SDL_EVENT_PINCH_BEGIN -> {
                    consumer.onPinchBegin(timestamp);
                }
                case SDL_EVENT_PINCH_UPDATE -> {
                    consumer.onPinchUpdate(timestamp);
                }
                case SDL_EVENT_PINCH_END -> {
                    consumer.onPinchEnd(timestamp);
                }
                case SDL_EVENT_CLIPBOARD_UPDATE -> {
                    consumer.onClipboardUpdate(timestamp);
                }
                case SDL_EVENT_DROP_FILE -> {
                    consumer.onDropFile(timestamp);
                }
                case SDL_EVENT_DROP_TEXT -> {
                    consumer.onDropText(timestamp);
                }
                case SDL_EVENT_DROP_BEGIN -> {
                    consumer.onDropBegin(timestamp);
                }
                case SDL_EVENT_DROP_COMPLETE -> {
                    consumer.onDropComplete(timestamp);
                }
                case SDL_EVENT_DROP_POSITION -> {
                    consumer.onDropPosition(timestamp);
                }
                case SDL_EVENT_AUDIO_DEVICE_ADDED -> {
                    consumer.onAudioDeviceAdded(timestamp);
                }
                case SDL_EVENT_AUDIO_DEVICE_REMOVED -> {
                    consumer.onAudioDeviceRemoved(timestamp);
                }
                case SDL_EVENT_AUDIO_DEVICE_FORMAT_CHANGED -> {
                    consumer.onAudioDeviceFormatChanged(timestamp);
                }
                case SDL_EVENT_SENSOR_UPDATE -> {
                    consumer.onSensorUpdate(timestamp);
                }
                case SDL_EVENT_PEN_PROXIMITY_IN -> {
                    consumer.onPenProximityIn(timestamp);
                }
                case SDL_EVENT_PEN_PROXIMITY_OUT -> {
                    consumer.onPenProximityOut(timestamp);
                }
                case SDL_EVENT_PEN_DOWN -> {
                    consumer.onPenDown(timestamp);
                }
                case SDL_EVENT_PEN_UP -> {
                    consumer.onPenUp(timestamp);
                }
                case SDL_EVENT_PEN_BUTTON_DOWN -> {
                    consumer.onPenButtonDown(timestamp);
                }
                case SDL_EVENT_PEN_BUTTON_UP -> {
                    consumer.onPenButtonUp(timestamp);
                }
                case SDL_EVENT_PEN_MOTION -> {
                    consumer.onPenMotion(timestamp);
                }
                case SDL_EVENT_PEN_AXIS -> {
                    consumer.onPenAxis(timestamp);
                }
                case SDL_EVENT_CAMERA_DEVICE_ADDED -> {
                    consumer.onCameraDeviceAdded(timestamp);
                }
                case SDL_EVENT_CAMERA_DEVICE_REMOVED -> {
                    consumer.onCameraDeviceRemoved(timestamp);
                }
                case SDL_EVENT_CAMERA_DEVICE_APPROVED -> {
                    consumer.onCameraDeviceApproved(timestamp);
                }
                case SDL_EVENT_CAMERA_DEVICE_DENIED -> {
                    consumer.onCameraDeviceDenied(timestamp);
                }
                case SDL_EVENT_RENDER_TARGETS_RESET -> {
                    consumer.onRenderTargetsReset(timestamp);
                }
                case SDL_EVENT_RENDER_DEVICE_RESET -> {
                    consumer.onRenderDeviceReset(timestamp);
                }
                case SDL_EVENT_RENDER_DEVICE_LOST -> {
                    consumer.onRenderDeviceLost(timestamp);
                }
                case SDL_EVENT_PRIVATE0 -> {
                    consumer.onPrivate0(timestamp);
                }
                case SDL_EVENT_PRIVATE1 -> {
                    consumer.onPrivate1(timestamp);
                }
                case SDL_EVENT_PRIVATE2 -> {
                    consumer.onPrivate2(timestamp);
                }
                case SDL_EVENT_PRIVATE3 -> {
                    consumer.onPrivate3(timestamp);
                }
                case int userEventType when userEventType >= SDL_EVENT_USER && userEventType <= SDL_EVENT_LAST -> {
                    consumer.onUser(timestamp, userEventType);
                }
        }


    }

}
