package io.github.plixo2.sodalite.events;


import org.libsdl.sdl.*;

import java.lang.foreign.MemorySegment;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

public record Event(EventData data, int type, long timestamp) {

    public sealed interface EventData {

    }

    private static boolean isUserEvent(int code) {
        return code >= SDL_EVENT_USER() && code <= SDL_EVENT_LAST();
    }

    public static boolean isDisplayEvent(int code) {
        return code >= SDL_EVENT_DISPLAY_FIRST() && code <= SDL_EVENT_DISPLAY_LAST();
    }

    private static boolean isWindowEvent(int code) {
        return code >= SDL_EVENT_WINDOW_FIRST() && code <= SDL_EVENT_WINDOW_LAST();
    }

    private static boolean isKeyboardDeviceEvent(int code) {
        return code == SDL_EVENT_KEYBOARD_ADDED() || code == SDL_EVENT_KEYBOARD_REMOVED();
    }

    private static boolean isKeyboardEvent(int code) {
        return code == SDL_EVENT_KEY_DOWN() || code == SDL_EVENT_KEY_UP();
    }

    private static boolean isTextEditingEvent(int code) {
        return code == SDL_EVENT_TEXT_EDITING();
    }

    private static boolean isTextEditingCandidatesEvent(int code) {
        return code == SDL_EVENT_TEXT_EDITING_CANDIDATES();
    }

    private static boolean isTextInputEvent(int code) {
        return code == SDL_EVENT_TEXT_INPUT();
    }

    private static boolean isMouseDeviceEvent(int code) {
        return code == SDL_EVENT_MOUSE_ADDED() || code == SDL_EVENT_MOUSE_REMOVED();
    }

    private static boolean isMouseMotionEvent(int code) {
        return code == SDL_EVENT_MOUSE_MOTION();
    }

    private static boolean isMouseButtonEvent(int code) {
        return code == SDL_EVENT_MOUSE_BUTTON_DOWN() || code == SDL_EVENT_MOUSE_BUTTON_UP();
    }

    private static boolean isMouseWheelEvent(int code) {
        return code == SDL_EVENT_MOUSE_WHEEL();
    }

    private static boolean isJoystickDeviceEvent(int code) {
        return code == SDL_EVENT_JOYSTICK_ADDED()
                || code == SDL_EVENT_JOYSTICK_REMOVED()
                || code == SDL_EVENT_JOYSTICK_UPDATE_COMPLETE();
    }

    private static boolean isJoystickAxisEvent(int code) {
        return code == SDL_EVENT_JOYSTICK_AXIS_MOTION();
    }

    private static boolean isJoystickBallEvent(int code) {
        return code == SDL_EVENT_JOYSTICK_BALL_MOTION();
    }

    private static boolean isJoystickHatEvent(int code) {
        return code == SDL_EVENT_JOYSTICK_HAT_MOTION();
    }

    private static boolean isJoystickButtonEvent(int code) {
        return code == SDL_EVENT_JOYSTICK_BUTTON_DOWN() || code == SDL_EVENT_JOYSTICK_BUTTON_UP();
    }

    private static boolean isJoystickBatteryEvent(int code) {
        return code == SDL_EVENT_JOYSTICK_BATTERY_UPDATED();
    }

    private static boolean isGamepadDeviceEvent(int code) {
        return code == SDL_EVENT_GAMEPAD_ADDED()
                || code == SDL_EVENT_GAMEPAD_REMOVED()
                || code == SDL_EVENT_GAMEPAD_REMAPPED()
                || code == SDL_EVENT_GAMEPAD_UPDATE_COMPLETE()
                || code == SDL_EVENT_GAMEPAD_STEAM_HANDLE_UPDATED();
    }

    private static boolean isGamepadAxisEvent(int code) {
        return code == SDL_EVENT_GAMEPAD_AXIS_MOTION();
    }

    private static boolean isGamepadButtonEvent(int code) {
        return code == SDL_EVENT_GAMEPAD_BUTTON_DOWN() || code == SDL_EVENT_GAMEPAD_BUTTON_UP();
    }

    private static boolean isGamepadTouchpadEvent(int code) {
        return code == SDL_EVENT_GAMEPAD_TOUCHPAD_DOWN()
                || code == SDL_EVENT_GAMEPAD_TOUCHPAD_MOTION()
                || code == SDL_EVENT_GAMEPAD_TOUCHPAD_UP();
    }

    private static boolean isGamepadSensorEvent(int code) {
        return code == SDL_EVENT_GAMEPAD_SENSOR_UPDATE();
    }

    private static boolean isTouchFingerEvent(int code) {
        return code >= SDL_EVENT_FINGER_DOWN() && code <= SDL_EVENT_FINGER_CANCELED();
    }

    private static boolean isPinchEvent(int code) {
        return code >= SDL_EVENT_PINCH_BEGIN() && code <= SDL_EVENT_PINCH_END();
    }

    private static boolean isDropEvent(int code) {
        return code >= SDL_EVENT_DROP_FILE() && code <= SDL_EVENT_DROP_POSITION();
    }

    private static boolean isClipboardEvent(int code) {
        return code == SDL_EVENT_CLIPBOARD_UPDATE();
    }

    private static boolean isAudioDeviceEvent(int code) {
        return code >= SDL_EVENT_AUDIO_DEVICE_ADDED() && code <= SDL_EVENT_AUDIO_DEVICE_FORMAT_CHANGED();
    }

    private static boolean isSensorEvent(int code) {
        return code == SDL_EVENT_SENSOR_UPDATE();
    }

    private static boolean isPenProximityEvent(int code) {
        return code == SDL_EVENT_PEN_PROXIMITY_IN() || code == SDL_EVENT_PEN_PROXIMITY_OUT();
    }

    private static boolean isPenTouchEvent(int code) {
        return code == SDL_EVENT_PEN_DOWN() || code == SDL_EVENT_PEN_UP();
    }

    private static boolean isPenMotionEvent(int code) {
        return code == SDL_EVENT_PEN_MOTION();
    }

    private static boolean isPenButtonEvent(int code) {
        return code == SDL_EVENT_PEN_BUTTON_DOWN() || code == SDL_EVENT_PEN_BUTTON_UP();
    }

    private static boolean isPenAxisEvent(int code) {
        return code == SDL_EVENT_PEN_AXIS();
    }

    private static boolean isCameraDeviceEvent(int code) {
        return code >= SDL_EVENT_CAMERA_DEVICE_ADDED() && code <= SDL_EVENT_CAMERA_DEVICE_DENIED();
    }

    private static boolean isRenderEvent(int code) {
        return code >= SDL_EVENT_RENDER_TARGETS_RESET() && code <= SDL_EVENT_RENDER_DEVICE_LOST();
    }

    private static boolean isQuitEvent(int code) {
        return code == SDL_EVENT_QUIT();
    }

    private static boolean isCommonOnlyEvent(int code) {
        return code == SDL_EVENT_TERMINATING()
                || code == SDL_EVENT_LOW_MEMORY()
                || code == SDL_EVENT_WILL_ENTER_BACKGROUND()
                || code == SDL_EVENT_DID_ENTER_BACKGROUND()
                || code == SDL_EVENT_WILL_ENTER_FOREGROUND()
                || code == SDL_EVENT_DID_ENTER_FOREGROUND()
                || code == SDL_EVENT_LOCALE_CHANGED()
                || code == SDL_EVENT_SYSTEM_THEME_CHANGED()
                || code == SDL_EVENT_KEYMAP_CHANGED()
                || code == SDL_EVENT_SCREEN_KEYBOARD_SHOWN()
                || code == SDL_EVENT_SCREEN_KEYBOARD_HIDDEN()
                || code == SDL_EVENT_POLL_SENTINEL()
                || code == SDL_EVENT_PRIVATE0()
                || code == SDL_EVENT_PRIVATE1()
                || code == SDL_EVENT_PRIVATE2()
                || code == SDL_EVENT_PRIVATE3();
    }

    static Event from(MemorySegment segment) {
        var type = SDL_Event.type(segment);
        var timestamp = SDL_CommonEvent.timestamp(SDL_Event.common(segment));

        EventData data = null;
        if (isUserEvent(type)) {
            var asUser = SDL_Event.user(segment);
            var windowID = SDL_UserEvent.windowID(asUser);
            var code = SDL_UserEvent.code(asUser);
            var data1 = SDL_UserEvent.data1(asUser);
            var data2 = SDL_UserEvent.data2(asUser);
            data = new UserEvent(windowID, code, data1, data2);
        } else if (isWindowEvent(type)) {
            var asWindow = SDL_Event.window(segment);
            var windowID = SDL_WindowEvent.windowID(asWindow);
            var data1 = SDL_WindowEvent.data1(asWindow);
            var data2 = SDL_WindowEvent.data2(asWindow);
            data = new WindowEvent(windowID, data1, data2);
        } else if (isDisplayEvent(type)) {
            var asDisplay = SDL_Event.display(segment);
            var displayID = SDL_DisplayEvent.displayID(asDisplay);
            var data1 = SDL_DisplayEvent.data1(asDisplay);
            var data2 = SDL_DisplayEvent.data2(asDisplay);
            data = new DisplayEvent(displayID, data1, data2);
        } else if (isKeyboardDeviceEvent(type)) {
            var asKeyboardDevice = SDL_Event.kdevice(segment);
            var which = SDL_KeyboardDeviceEvent.which(asKeyboardDevice);
            var reserved = SDL_KeyboardDeviceEvent.reserved(asKeyboardDevice);
            data = new KeyboardDeviceEvent(which, reserved);
        } else if (isKeyboardEvent(type)) {
            var asKey = SDL_Event.key(segment);
            var windowID = SDL_KeyboardEvent.windowID(asKey);
            var which = SDL_KeyboardEvent.which(asKey);
            var scancode = SDL_KeyboardEvent.scancode(asKey);
            var key = SDL_KeyboardEvent.key(asKey);
            var mod = SDL_KeyboardEvent.mod(asKey);
            var raw = SDL_KeyboardEvent.raw(asKey);
            var down = SDL_KeyboardEvent.down(asKey);
            var repeat = SDL_KeyboardEvent.repeat(asKey);
            data = new KeyboardEvent(windowID, which, scancode, key, mod, raw, down, repeat);
        } else if (isTextEditingEvent(type)) {
            var asEdit = SDL_Event.edit(segment);
            var windowID = SDL_TextEditingEvent.windowID(asEdit);
            var text = SDL_TextEditingEvent.text(asEdit);
            var start = SDL_TextEditingEvent.start(asEdit);
            var length = SDL_TextEditingEvent.length(asEdit);
            data = new TextEditingEvent(windowID, text, start, length);
        } else if (isTextEditingCandidatesEvent(type)) {
            var asCandidates = SDL_Event.edit_candidates(segment);
            var windowID = SDL_TextEditingCandidatesEvent.windowID(asCandidates);
            var candidates = SDL_TextEditingCandidatesEvent.candidates(asCandidates);
            var numCandidates = SDL_TextEditingCandidatesEvent.num_candidates(asCandidates);
            var selectedCandidate = SDL_TextEditingCandidatesEvent.selected_candidate(asCandidates);
            var horizontal = SDL_TextEditingCandidatesEvent.horizontal(asCandidates);
            data = new TextEditingCandidatesEvent(windowID, candidates, numCandidates, selectedCandidate, horizontal);
        } else if (isTextInputEvent(type)) {
            var asText = SDL_Event.text(segment);
            var windowID = SDL_TextInputEvent.windowID(asText);
            var text = SDL_TextInputEvent.text(asText);
            data = new TextInputEvent(windowID, text);
        } else if (isMouseDeviceEvent(type)) {
            var asMouseDevice = SDL_Event.mdevice(segment);
            var which = SDL_MouseDeviceEvent.which(asMouseDevice);
            data = new MouseDeviceEvent(which);
        } else if (isMouseMotionEvent(type)) {
            var asMotion = SDL_Event.motion(segment);
            var windowID = SDL_MouseMotionEvent.windowID(asMotion);
            var which = SDL_MouseMotionEvent.which(asMotion);
            var state = SDL_MouseMotionEvent.state(asMotion);
            var x = SDL_MouseMotionEvent.x(asMotion);
            var y = SDL_MouseMotionEvent.y(asMotion);
            var xrel = SDL_MouseMotionEvent.xrel(asMotion);
            var yrel = SDL_MouseMotionEvent.yrel(asMotion);
            data = new MouseMotionEvent(windowID, which, state, x, y, xrel, yrel);
        } else if (isMouseButtonEvent(type)) {
            var asButton = SDL_Event.button(segment);
            var windowID = SDL_MouseButtonEvent.windowID(asButton);
            var which = SDL_MouseButtonEvent.which(asButton);
            var button = SDL_MouseButtonEvent.button(asButton);
            var down = SDL_MouseButtonEvent.down(asButton);
            var clicks = SDL_MouseButtonEvent.clicks(asButton);
            var x = SDL_MouseButtonEvent.x(asButton);
            var y = SDL_MouseButtonEvent.y(asButton);
            data = new MouseButtonEvent(windowID, which, button, down, clicks, x, y);
        } else if (isMouseWheelEvent(type)) {
            var asWheel = SDL_Event.wheel(segment);
            var windowID = SDL_MouseWheelEvent.windowID(asWheel);
            var which = SDL_MouseWheelEvent.which(asWheel);
            var x = SDL_MouseWheelEvent.x(asWheel);
            var y = SDL_MouseWheelEvent.y(asWheel);
            var direction = SDL_MouseWheelEvent.direction(asWheel);
            var mouseX = SDL_MouseWheelEvent.mouse_x(asWheel);
            var mouseY = SDL_MouseWheelEvent.mouse_y(asWheel);
            var integerX = SDL_MouseWheelEvent.integer_x(asWheel);
            var integerY = SDL_MouseWheelEvent.integer_y(asWheel);
            data = new MouseWheelEvent(windowID, which, x, y, direction, mouseX, mouseY, integerX, integerY);
        } else if (isJoystickDeviceEvent(type)) {
            var asJDevice = SDL_Event.jdevice(segment);
            var which = SDL_JoyDeviceEvent.which(asJDevice);
            data = new JoystickDeviceEvent(which);
        } else if (isJoystickAxisEvent(type)) {
            var asAxis = SDL_Event.jaxis(segment);
            var which = SDL_JoyAxisEvent.which(asAxis);
            var axis = SDL_JoyAxisEvent.axis(asAxis);
            var value = SDL_JoyAxisEvent.value(asAxis);
            data = new JoystickAxisEvent(which, axis, value);
        } else if (isJoystickBallEvent(type)) {
            var asBall = SDL_Event.jball(segment);
            var which = SDL_JoyBallEvent.which(asBall);
            var ball = SDL_JoyBallEvent.ball(asBall);
            var xrel = SDL_JoyBallEvent.xrel(asBall);
            var yrel = SDL_JoyBallEvent.yrel(asBall);
            data = new JoystickBallEvent(which, ball, xrel, yrel);
        } else if (isJoystickHatEvent(type)) {
            var asHat = SDL_Event.jhat(segment);
            var which = SDL_JoyHatEvent.which(asHat);
            var hat = SDL_JoyHatEvent.hat(asHat);
            var value = SDL_JoyHatEvent.value(asHat);
            data = new JoystickHatEvent(which, hat, value);
        } else if (isJoystickButtonEvent(type)) {
            var asButton = SDL_Event.jbutton(segment);
            var which = SDL_JoyButtonEvent.which(asButton);
            var button = SDL_JoyButtonEvent.button(asButton);
            var down = SDL_JoyButtonEvent.down(asButton);
            data = new JoystickButtonEvent(which, button, down);
        } else if (isJoystickBatteryEvent(type)) {
            var asBattery = SDL_Event.jbattery(segment);
            var which = SDL_JoyBatteryEvent.which(asBattery);
            var state = SDL_JoyBatteryEvent.state(asBattery);
            var percent = SDL_JoyBatteryEvent.percent(asBattery);
            data = new JoystickBatteryEvent(which, state, percent);
        } else if (isGamepadDeviceEvent(type)) {
            var asGDevice = SDL_Event.gdevice(segment);
            var which = SDL_GamepadDeviceEvent.which(asGDevice);
            data = new GamepadDeviceEvent(which);
        } else if (isGamepadAxisEvent(type)) {
            var asAxis = SDL_Event.gaxis(segment);
            var which = SDL_GamepadAxisEvent.which(asAxis);
            var axis = SDL_GamepadAxisEvent.axis(asAxis);
            var value = SDL_GamepadAxisEvent.value(asAxis);
            data = new GamepadAxisEvent(which, axis, value);
        } else if (isGamepadButtonEvent(type)) {
            var asButton = SDL_Event.gbutton(segment);
            var which = SDL_GamepadButtonEvent.which(asButton);
            var button = SDL_GamepadButtonEvent.button(asButton);
            var down = SDL_GamepadButtonEvent.down(asButton);
            data = new GamepadButtonEvent(which, button, down);
        } else if (isGamepadTouchpadEvent(type)) {
            var asTouchpad = SDL_Event.gtouchpad(segment);
            var which = SDL_GamepadTouchpadEvent.which(asTouchpad);
            var touchpad = SDL_GamepadTouchpadEvent.touchpad(asTouchpad);
            var finger = SDL_GamepadTouchpadEvent.finger(asTouchpad);
            var x = SDL_GamepadTouchpadEvent.x(asTouchpad);
            var y = SDL_GamepadTouchpadEvent.y(asTouchpad);
            var pressure = SDL_GamepadTouchpadEvent.pressure(asTouchpad);
            data = new GamepadTouchpadEvent(which, touchpad, finger, x, y, pressure);
        } else if (isGamepadSensorEvent(type)) {
            var asSensor = SDL_Event.gsensor(segment);
            var which = SDL_GamepadSensorEvent.which(asSensor);
            var sensor = SDL_GamepadSensorEvent.sensor(asSensor);
            var dataSegment = SDL_GamepadSensorEvent.data(asSensor);
            var sensorTimestamp = SDL_GamepadSensorEvent.sensor_timestamp(asSensor);
            data = new GamepadSensorEvent(which, sensor, dataSegment, sensorTimestamp);
        } else if (isTouchFingerEvent(type)) {
            var asTouch = SDL_Event.tfinger(segment);
            var touchID = SDL_TouchFingerEvent.touchID(asTouch);
            var fingerID = SDL_TouchFingerEvent.fingerID(asTouch);
            var x = SDL_TouchFingerEvent.x(asTouch);
            var y = SDL_TouchFingerEvent.y(asTouch);
            var dx = SDL_TouchFingerEvent.dx(asTouch);
            var dy = SDL_TouchFingerEvent.dy(asTouch);
            var pressure = SDL_TouchFingerEvent.pressure(asTouch);
            var windowID = SDL_TouchFingerEvent.windowID(asTouch);
            data = new TouchFingerEvent(touchID, fingerID, x, y, dx, dy, pressure, windowID);
        } else if (isPinchEvent(type)) {
            var asPinch = SDL_Event.pinch(segment);
            var scale = SDL_PinchFingerEvent.scale(asPinch);
            var windowID = SDL_PinchFingerEvent.windowID(asPinch);
            data = new PinchFingerEvent(scale, windowID);
        } else if (isPenProximityEvent(type)) {
            var asProximity = SDL_Event.pproximity(segment);
            var windowID = SDL_PenProximityEvent.windowID(asProximity);
            var which = SDL_PenProximityEvent.which(asProximity);
            data = new PenProximityEvent(windowID, which);
        } else if (isPenTouchEvent(type)) {
            var asTouch = SDL_Event.ptouch(segment);
            var windowID = SDL_PenTouchEvent.windowID(asTouch);
            var which = SDL_PenTouchEvent.which(asTouch);
            var penState = SDL_PenTouchEvent.pen_state(asTouch);
            var x = SDL_PenTouchEvent.x(asTouch);
            var y = SDL_PenTouchEvent.y(asTouch);
            var eraser = SDL_PenTouchEvent.eraser(asTouch);
            var down = SDL_PenTouchEvent.down(asTouch);
            data = new PenTouchEvent(windowID, which, penState, x, y, eraser, down);
        } else if (isPenMotionEvent(type)) {
            var asMotion = SDL_Event.pmotion(segment);
            var windowID = SDL_PenMotionEvent.windowID(asMotion);
            var which = SDL_PenMotionEvent.which(asMotion);
            var penState = SDL_PenMotionEvent.pen_state(asMotion);
            var x = SDL_PenMotionEvent.x(asMotion);
            var y = SDL_PenMotionEvent.y(asMotion);
            data = new PenMotionEvent(windowID, which, penState, x, y);
        } else if (isPenButtonEvent(type)) {
            var asButton = SDL_Event.pbutton(segment);
            var windowID = SDL_PenButtonEvent.windowID(asButton);
            var which = SDL_PenButtonEvent.which(asButton);
            var penState = SDL_PenButtonEvent.pen_state(asButton);
            var x = SDL_PenButtonEvent.x(asButton);
            var y = SDL_PenButtonEvent.y(asButton);
            var button = SDL_PenButtonEvent.button(asButton);
            var down = SDL_PenButtonEvent.down(asButton);
            data = new PenButtonEvent(windowID, which, penState, x, y, button, down);
        } else if (isPenAxisEvent(type)) {
            var asAxis = SDL_Event.paxis(segment);
            var windowID = SDL_PenAxisEvent.windowID(asAxis);
            var which = SDL_PenAxisEvent.which(asAxis);
            var penState = SDL_PenAxisEvent.pen_state(asAxis);
            var x = SDL_PenAxisEvent.x(asAxis);
            var y = SDL_PenAxisEvent.y(asAxis);
            var axis = SDL_PenAxisEvent.axis(asAxis);
            var value = SDL_PenAxisEvent.value(asAxis);
            data = new PenAxisEvent(windowID, which, penState, x, y, axis, value);
        } else if (isDropEvent(type)) {
            var asDrop = SDL_Event.drop(segment);
            var windowID = SDL_DropEvent.windowID(asDrop);
            var x = SDL_DropEvent.x(asDrop);
            var y = SDL_DropEvent.y(asDrop);
            var source = SDL_DropEvent.source(asDrop);
            var dropData = SDL_DropEvent.data(asDrop);
            data = new DropEvent(windowID, x, y, source, dropData);
        } else if (isClipboardEvent(type)) {
            var asClipboard = SDL_Event.clipboard(segment);
            var owner = SDL_ClipboardEvent.owner(asClipboard);
            var numMimeTypes = SDL_ClipboardEvent.num_mime_types(asClipboard);
            var mimeTypes = SDL_ClipboardEvent.mime_types(asClipboard);
            data = new ClipboardEvent(owner, numMimeTypes, mimeTypes);
        } else if (isAudioDeviceEvent(type)) {
            var asAudio = SDL_Event.adevice(segment);
            var which = SDL_AudioDeviceEvent.which(asAudio);
            var recording = SDL_AudioDeviceEvent.recording(asAudio);
            data = new AudioDeviceEvent(which, recording);
        } else if (isCameraDeviceEvent(type)) {
            var asCamera = SDL_Event.cdevice(segment);
            var which = SDL_CameraDeviceEvent.which(asCamera);
            data = new CameraDeviceEvent(which);
        } else if (isSensorEvent(type)) {
            var asSensor = SDL_Event.sensor(segment);
            var which = SDL_SensorEvent.which(asSensor);
            var dataSegment = SDL_SensorEvent.data(asSensor);
            var sensorTimestamp = SDL_SensorEvent.sensor_timestamp(asSensor);
            data = new SensorEvent(which, dataSegment, sensorTimestamp);
        } else if (isRenderEvent(type)) {
            var asRender = SDL_Event.render(segment);
            var windowID = SDL_RenderEvent.windowID(asRender);
            data = new RenderEvent(windowID);
        } else if (isQuitEvent(type)) {
            var asQuit = SDL_Event.quit(segment);
            var reserved = SDL_QuitEvent.reserved(asQuit);
            data = new QuitEvent(reserved);
        } else if (isCommonOnlyEvent(type)) {
            var asCommon = SDL_Event.common(segment);
            var reserved = SDL_CommonEvent.reserved(asCommon);
            data = new CommonEvent(reserved);
        }

        if (data == null) {
            data = new UnknownEvent();
        }
        return new Event(data, type, timestamp);
    }



    public record UnknownEvent(
    ) implements EventData {}

    public record CommonEvent(
            int reserved
    ) implements EventData {}

    public record DisplayEvent(
            int displayID,
            int data1,
            int data2
    ) implements EventData {}

    public record WindowEvent(
            int windowID,
            int data1,
            int data2
    ) implements EventData {}

    public record KeyboardDeviceEvent(
            int which,
            int reserved
    ) implements EventData {}

    public record KeyboardEvent(
            int windowID,
            int which,
            int scancode,
            int key,
            int mod,
            int raw,
            boolean down,
            boolean repeat
    ) implements EventData {}

    public record TextEditingEvent(
            int windowID,
            MemorySegment text,
            int start,
            int length
    ) implements EventData {}

    public record TextEditingCandidatesEvent(
            int windowID,
            MemorySegment candidates,
            int numCandidates,
            int selectedCandidate,
            boolean horizontal
    ) implements EventData {}

    public record TextInputEvent(
            int windowID,
            MemorySegment text
    ) implements EventData {}

    public record MouseDeviceEvent(
            int which
    ) implements EventData {}

    public record MouseMotionEvent(
            int windowID,
            int which,
            int state,
            float x,
            float y,
            float xrel,
            float yrel
    ) implements EventData {}

    public record MouseButtonEvent(
            int windowID,
            int which,
            int button,
            boolean down,
            int clicks,
            float x,
            float y
    ) implements EventData {}

    public record MouseWheelEvent(
            int windowID,
            int which,
            float x,
            float y,
            int direction,
            float mouseX,
            float mouseY,
            int integerX,
            int integerY
    ) implements EventData {}

    public record JoystickDeviceEvent(
            int which
    ) implements EventData {}

    public record JoystickAxisEvent(
            int which,
            int axis,
            int value
    ) implements EventData {}

    public record JoystickBallEvent(
            int which,
            int ball,
            int xrel,
            int yrel
    ) implements EventData {}

    public record JoystickHatEvent(
            int which,
            int hat,
            int value
    ) implements EventData {}

    public record JoystickButtonEvent(
            int which,
            int button,
            boolean down
    ) implements EventData {}

    public record JoystickBatteryEvent(
            int which,
            int state,
            int percent
    ) implements EventData {}

    public record GamepadDeviceEvent(
            int which
    ) implements EventData {}

    public record GamepadAxisEvent(
            int which,
            int axis,
            int value
    ) implements EventData {}

    public record GamepadButtonEvent(
            int which,
            int button,
            boolean down
    ) implements EventData {}

    public record GamepadTouchpadEvent(
            int which,
            int touchpad,
            int finger,
            float x,
            float y,
            float pressure
    ) implements EventData {}

    public record GamepadSensorEvent(
            int which,
            int sensor,
            MemorySegment data,
            long sensorTimestamp
    ) implements EventData {}

    public record AudioDeviceEvent(
            int which,
            boolean recording
    ) implements EventData {}

    public record CameraDeviceEvent(
            int which
    ) implements EventData {}

    public record SensorEvent(
            int which,
            MemorySegment data,
            long sensorTimestamp
    ) implements EventData {}

    public record QuitEvent(
            int reserved
    ) implements EventData {}

    public record UserEvent(
            int windowID,
            int code,
            MemorySegment data1,
            MemorySegment data2
    ) implements EventData {}

    public record TouchFingerEvent(
            long touchID,
            long fingerID,
            float x,
            float y,
            float dx,
            float dy,
            float pressure,
            int windowID
    ) implements EventData {}

    public record PinchFingerEvent(
            float scale,
            int windowID
    ) implements EventData {}

    public record PenProximityEvent(
            int windowID,
            int which
    ) implements EventData {}

    public record PenTouchEvent(
            int windowID,
            int which,
            int penState,
            float x,
            float y,
            boolean eraser,
            boolean down
    ) implements EventData {}

    public record PenMotionEvent(
            int windowID,
            int which,
            int penState,
            float x,
            float y
    ) implements EventData {}

    public record PenButtonEvent(
            int windowID,
            int which,
            int penState,
            float x,
            float y,
            int button,
            boolean down
    ) implements EventData {}

    public record PenAxisEvent(
            int windowID,
            int which,
            int penState,
            float x,
            float y,
            int axis,
            float value
    ) implements EventData {}

    public record RenderEvent(
            int windowID
    ) implements EventData {}

    public record DropEvent(
            int windowID,
            float x,
            float y,
            MemorySegment source,
            MemorySegment data
    ) implements EventData {}

    public record ClipboardEvent(
            boolean owner,
            int numMimeTypes,
            MemorySegment mimeTypes
    ) implements EventData {}
}

