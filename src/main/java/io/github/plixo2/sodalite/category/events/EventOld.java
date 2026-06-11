package io.github.plixo2.sodalite.category.events;


/// @apiNote SDL_Event
public record EventOld() {


    /*

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



    static Event from(MemorySegment segment) {
        var type = SDL_Event.type(segment);
        var timestamp = SDL_CommonEvent.timestamp(SDL_Event.common(segment));
        var enumType = EventType.fromCode(type);


        return null;
//        EventData data = null;
//        if (isUserEvent(type)) {
//            var asUser = SDL_Event.user(segment);
//            var windowID = SDL_UserEvent.windowID(asUser);
//            var code = SDL_UserEvent.code(asUser);
//            var data1 = SDL_UserEvent.data1(asUser);
//            var data2 = SDL_UserEvent.data2(asUser);
//            data = new UserEvent(type, windowID, code, data1, data2);
//        } else if (isWindowEvent(type)) {
//            var asWindow = SDL_Event.window(segment);
//            var windowID = SDL_WindowEvent.windowID(asWindow);
//            var data1 = SDL_WindowEvent.data1(asWindow);
//            var data2 = SDL_WindowEvent.data2(asWindow);
//            data = new WindowEvent(windowID, data1, data2);
//        } else if (isDisplayEvent(type)) {
//            var asDisplay = SDL_Event.display(segment);
//            var displayID = SDL_DisplayEvent.displayID(asDisplay);
//            var data1 = SDL_DisplayEvent.data1(asDisplay);
//            var data2 = SDL_DisplayEvent.data2(asDisplay);
//            data = new DisplayEvent(displayID, data1, data2);
//        } else if (isKeyboardDeviceEvent(type)) {
//            var asKeyboardDevice = SDL_Event.kdevice(segment);
//            var which = SDL_KeyboardDeviceEvent.which(asKeyboardDevice);
//            data = new KeyboardDeviceEvent(which);
//        } else if (isKeyboardEvent(type)) {
//            var asKey = SDL_Event.key(segment);
//            var windowID = SDL_KeyboardEvent.windowID(asKey);
//            var which = SDL_KeyboardEvent.which(asKey);
//            var scancode = SDL_KeyboardEvent.scancode(asKey);
//            var key = SDL_KeyboardEvent.key(asKey);
//            var mod = SDL_KeyboardEvent.mod(asKey);
//            var raw = SDL_KeyboardEvent.raw(asKey);
//            var down = SDL_KeyboardEvent.down(asKey);
//            var repeat = SDL_KeyboardEvent.repeat(asKey);
//            data = new KeyboardEvent(windowID, which, scancode, key, mod, raw, down, repeat);
//        } else if (isTextEditingEvent(type)) {
//            var asEdit = SDL_Event.edit(segment);
//            var windowID = SDL_TextEditingEvent.windowID(asEdit);
//            var text = toStringOrNull(SDL_TextEditingEvent.text(asEdit));
//            var start = SDL_TextEditingEvent.start(asEdit);
//            var length = SDL_TextEditingEvent.length(asEdit);
//            data = new TextEditingEvent(windowID, text, start, length);
//        } else if (isTextEditingCandidatesEvent(type)) {
//            var asCandidates = SDL_Event.edit_candidates(segment);
//            var windowID = SDL_TextEditingCandidatesEvent.windowID(asCandidates);
//            var candidatesSegment = SDL_TextEditingCandidatesEvent.candidates(asCandidates);
//            var numCandidates = SDL_TextEditingCandidatesEvent.num_candidates(asCandidates);
//            var selectedCandidate = SDL_TextEditingCandidatesEvent.selected_candidate(asCandidates);
//            var horizontal = SDL_TextEditingCandidatesEvent.horizontal(asCandidates);
//            var candidates = toStringArray(candidatesSegment, numCandidates);
//            data = new TextEditingCandidatesEvent(windowID, candidates, selectedCandidate, horizontal);
//        } else if (isTextInputEvent(type)) {
//            var asText = SDL_Event.text(segment);
//            var windowID = SDL_TextInputEvent.windowID(asText);
//            var text = toStringOrNull(SDL_TextInputEvent.text(asText));
//            data = new TextInputEvent(windowID, text);
//        } else if (isMouseDeviceEvent(type)) {
//            var asMouseDevice = SDL_Event.mdevice(segment);
//            var which = SDL_MouseDeviceEvent.which(asMouseDevice);
//            data = new MouseDeviceEvent(which);
//        } else if (isMouseMotionEvent(type)) {
//            var asMotion = SDL_Event.motion(segment);
//            var windowID = SDL_MouseMotionEvent.windowID(asMotion);
//            var which = SDL_MouseMotionEvent.which(asMotion);
//            var state = SDL_MouseMotionEvent.state(asMotion);
//            var x = SDL_MouseMotionEvent.x(asMotion);
//            var y = SDL_MouseMotionEvent.y(asMotion);
//            var xrel = SDL_MouseMotionEvent.xrel(asMotion);
//            var yrel = SDL_MouseMotionEvent.yrel(asMotion);
//            data = new MouseMotionEvent(windowID, which, state, x, y, xrel, yrel);
//        } else if (isMouseButtonEvent(type)) {
//            var asButton = SDL_Event.button(segment);
//            var windowID = SDL_MouseButtonEvent.windowID(asButton);
//            var which = SDL_MouseButtonEvent.which(asButton);
//            var button = SDL_MouseButtonEvent.button(asButton);
//            var down = SDL_MouseButtonEvent.down(asButton);
//            var clicks = SDL_MouseButtonEvent.clicks(asButton);
//            var x = SDL_MouseButtonEvent.x(asButton);
//            var y = SDL_MouseButtonEvent.y(asButton);
//            data = new MouseButtonEvent(windowID, which, button, down, clicks, x, y);
//        } else if (isMouseWheelEvent(type)) {
//            var asWheel = SDL_Event.wheel(segment);
//            var windowID = SDL_MouseWheelEvent.windowID(asWheel);
//            var which = SDL_MouseWheelEvent.which(asWheel);
//            var x = SDL_MouseWheelEvent.x(asWheel);
//            var y = SDL_MouseWheelEvent.y(asWheel);
//            var direction = SDL_MouseWheelEvent.direction(asWheel);
//            var mouseX = SDL_MouseWheelEvent.mouse_x(asWheel);
//            var mouseY = SDL_MouseWheelEvent.mouse_y(asWheel);
//            var integerX = SDL_MouseWheelEvent.integer_x(asWheel);
//            var integerY = SDL_MouseWheelEvent.integer_y(asWheel);
//            data = new MouseWheelEvent(windowID, which, x, y, direction, mouseX, mouseY, integerX, integerY);
//        } else if (isJoystickDeviceEvent(type)) {
//            var asJDevice = SDL_Event.jdevice(segment);
//            var which = SDL_JoyDeviceEvent.which(asJDevice);
//            data = new JoystickDeviceEvent(which);
//        } else if (isJoystickAxisEvent(type)) {
//            var asAxis = SDL_Event.jaxis(segment);
//            var which = SDL_JoyAxisEvent.which(asAxis);
//            var axis = SDL_JoyAxisEvent.axis(asAxis);
//            var value = SDL_JoyAxisEvent.value(asAxis);
//            data = new JoystickAxisEvent(which, axis, value);
//        } else if (isJoystickBallEvent(type)) {
//            var asBall = SDL_Event.jball(segment);
//            var which = SDL_JoyBallEvent.which(asBall);
//            var ball = SDL_JoyBallEvent.ball(asBall);
//            var xrel = SDL_JoyBallEvent.xrel(asBall);
//            var yrel = SDL_JoyBallEvent.yrel(asBall);
//            data = new JoystickBallEvent(which, ball, xrel, yrel);
//        } else if (isJoystickHatEvent(type)) {
//            var asHat = SDL_Event.jhat(segment);
//            var which = SDL_JoyHatEvent.which(asHat);
//            var hat = SDL_JoyHatEvent.hat(asHat);
//            var value = SDL_JoyHatEvent.value(asHat);
//            data = new JoystickHatEvent(which, hat, value);
//        } else if (isJoystickButtonEvent(type)) {
//            var asButton = SDL_Event.jbutton(segment);
//            var which = SDL_JoyButtonEvent.which(asButton);
//            var button = SDL_JoyButtonEvent.button(asButton);
//            var down = SDL_JoyButtonEvent.down(asButton);
//            data = new JoystickButtonEvent(which, button, down);
//        } else if (isJoystickBatteryEvent(type)) {
//            var asBattery = SDL_Event.jbattery(segment);
//            var which = SDL_JoyBatteryEvent.which(asBattery);
//            var state = SDL_JoyBatteryEvent.state(asBattery);
//            var percent = SDL_JoyBatteryEvent.percent(asBattery);
//            data = new JoystickBatteryEvent(which, state, percent);
//        } else if (isGamepadDeviceEvent(type)) {
//            var asGDevice = SDL_Event.gdevice(segment);
//            var which = SDL_GamepadDeviceEvent.which(asGDevice);
//            data = new GamepadDeviceEvent(which);
//        } else if (isGamepadAxisEvent(type)) {
//            var asAxis = SDL_Event.gaxis(segment);
//            var which = SDL_GamepadAxisEvent.which(asAxis);
//            var axis = SDL_GamepadAxisEvent.axis(asAxis);
//            var value = SDL_GamepadAxisEvent.value(asAxis);
//            data = new GamepadAxisEvent(which, axis, value);
//        } else if (isGamepadButtonEvent(type)) {
//            var asButton = SDL_Event.gbutton(segment);
//            var which = SDL_GamepadButtonEvent.which(asButton);
//            var button = SDL_GamepadButtonEvent.button(asButton);
//            var down = SDL_GamepadButtonEvent.down(asButton);
//            data = new GamepadButtonEvent(which, button, down);
//        } else if (isGamepadTouchpadEvent(type)) {
//            var asTouchpad = SDL_Event.gtouchpad(segment);
//            var which = SDL_GamepadTouchpadEvent.which(asTouchpad);
//            var touchpad = SDL_GamepadTouchpadEvent.touchpad(asTouchpad);
//            var finger = SDL_GamepadTouchpadEvent.finger(asTouchpad);
//            var x = SDL_GamepadTouchpadEvent.x(asTouchpad);
//            var y = SDL_GamepadTouchpadEvent.y(asTouchpad);
//            var pressure = SDL_GamepadTouchpadEvent.pressure(asTouchpad);
//            data = new GamepadTouchpadEvent(which, touchpad, finger, x, y, pressure);
//        } else if (isGamepadSensorEvent(type)) {
//            var asSensor = SDL_Event.gsensor(segment);
//            var which = SDL_GamepadSensorEvent.which(asSensor);
//            var sensor = SDL_GamepadSensorEvent.sensor(asSensor);
//            var dataSegment = SDL_GamepadSensorEvent.data(asSensor);
//            var sensorTimestamp = SDL_GamepadSensorEvent.sensor_timestamp(asSensor);
//            data = new GamepadSensorEvent(which, sensor, dataSegment, sensorTimestamp);
//        } else if (isTouchFingerEvent(type)) {
//            var asTouch = SDL_Event.tfinger(segment);
//            var touchID = SDL_TouchFingerEvent.touchID(asTouch);
//            var fingerID = SDL_TouchFingerEvent.fingerID(asTouch);
//            var x = SDL_TouchFingerEvent.x(asTouch);
//            var y = SDL_TouchFingerEvent.y(asTouch);
//            var dx = SDL_TouchFingerEvent.dx(asTouch);
//            var dy = SDL_TouchFingerEvent.dy(asTouch);
//            var pressure = SDL_TouchFingerEvent.pressure(asTouch);
//            var windowID = SDL_TouchFingerEvent.windowID(asTouch);
//            data = new TouchFingerEvent(touchID, fingerID, x, y, dx, dy, pressure, windowID);
//        } else if (isPinchEvent(type)) {
//            var asPinch = SDL_Event.pinch(segment);
//            var scale = SDL_PinchFingerEvent.scale(asPinch);
//            var windowID = SDL_PinchFingerEvent.windowID(asPinch);
//            data = new PinchFingerEvent(scale, windowID);
//        } else if (isPenProximityEvent(type)) {
//            var asProximity = SDL_Event.pproximity(segment);
//            var windowID = SDL_PenProximityEvent.windowID(asProximity);
//            var which = SDL_PenProximityEvent.which(asProximity);
//            data = new PenProximityEvent(windowID, which);
//        } else if (isPenTouchEvent(type)) {
//            var asTouch = SDL_Event.ptouch(segment);
//            var windowID = SDL_PenTouchEvent.windowID(asTouch);
//            var which = SDL_PenTouchEvent.which(asTouch);
//            var penState = SDL_PenTouchEvent.pen_state(asTouch);
//            var x = SDL_PenTouchEvent.x(asTouch);
//            var y = SDL_PenTouchEvent.y(asTouch);
//            var eraser = SDL_PenTouchEvent.eraser(asTouch);
//            var down = SDL_PenTouchEvent.down(asTouch);
//            data = new PenTouchEvent(windowID, which, penState, x, y, eraser, down);
//        } else if (isPenMotionEvent(type)) {
//            var asMotion = SDL_Event.pmotion(segment);
//            var windowID = SDL_PenMotionEvent.windowID(asMotion);
//            var which = SDL_PenMotionEvent.which(asMotion);
//            var penState = SDL_PenMotionEvent.pen_state(asMotion);
//            var x = SDL_PenMotionEvent.x(asMotion);
//            var y = SDL_PenMotionEvent.y(asMotion);
//            data = new PenMotionEvent(windowID, which, penState, x, y);
//        } else if (isPenButtonEvent(type)) {
//            var asButton = SDL_Event.pbutton(segment);
//            var windowID = SDL_PenButtonEvent.windowID(asButton);
//            var which = SDL_PenButtonEvent.which(asButton);
//            var penState = SDL_PenButtonEvent.pen_state(asButton);
//            var x = SDL_PenButtonEvent.x(asButton);
//            var y = SDL_PenButtonEvent.y(asButton);
//            var button = SDL_PenButtonEvent.button(asButton);
//            var down = SDL_PenButtonEvent.down(asButton);
//            data = new PenButtonEvent(windowID, which, penState, x, y, button, down);
//        } else if (isPenAxisEvent(type)) {
//            var asAxis = SDL_Event.paxis(segment);
//            var windowID = SDL_PenAxisEvent.windowID(asAxis);
//            var which = SDL_PenAxisEvent.which(asAxis);
//            var penState = SDL_PenAxisEvent.pen_state(asAxis);
//            var x = SDL_PenAxisEvent.x(asAxis);
//            var y = SDL_PenAxisEvent.y(asAxis);
//            var axis = SDL_PenAxisEvent.axis(asAxis);
//            var value = SDL_PenAxisEvent.value(asAxis);
//            data = new PenAxisEvent(windowID, which, penState, x, y, axis, value);
//        } else if (isDropEvent(type)) {
//            var asDrop = SDL_Event.drop(segment);
//            var windowID = SDL_DropEvent.windowID(asDrop);
//            var x = SDL_DropEvent.x(asDrop);
//            var y = SDL_DropEvent.y(asDrop);
//            var source = toStringOrNull(SDL_DropEvent.source(asDrop));
//            var dropData = toStringOrNull(SDL_DropEvent.data(asDrop));
//            data = new DropEvent(windowID, x, y, source, dropData);
//        } else if (isClipboardEvent(type)) {
//            var asClipboard = SDL_Event.clipboard(segment);
//            var owner = SDL_ClipboardEvent.owner(asClipboard);
//            var numMimeTypes = SDL_ClipboardEvent.num_mime_types(asClipboard);
//            var mimeTypesSegment = SDL_ClipboardEvent.mime_types(asClipboard);
//            var mimeTypes = toStringArray(mimeTypesSegment, numMimeTypes);
//            data = new ClipboardEvent(owner, mimeTypes);
//        } else if (isAudioDeviceEvent(type)) {
//            var asAudio = SDL_Event.adevice(segment);
//            var which = SDL_AudioDeviceEvent.which(asAudio);
//            var recording = SDL_AudioDeviceEvent.recording(asAudio);
//            data = new AudioDeviceEvent(which, recording);
//        } else if (isCameraDeviceEvent(type)) {
//            var asCamera = SDL_Event.cdevice(segment);
//            var which = SDL_CameraDeviceEvent.which(asCamera);
//            data = new CameraDeviceEvent(which);
//        } else if (isSensorEvent(type)) {
//            var asSensor = SDL_Event.sensor(segment);
//            var which = SDL_SensorEvent.which(asSensor);
//            var dataSegment = SDL_SensorEvent.data(asSensor);
//            var sensorTimestamp = SDL_SensorEvent.sensor_timestamp(asSensor);
//            data = new SensorEvent(which, dataSegment, sensorTimestamp);
//        } else if (isRenderEvent(type)) {
//            var asRender = SDL_Event.render(segment);
//            var windowID = SDL_RenderEvent.windowID(asRender);
//            data = new RenderEvent(windowID);
//        } else if (isQuitEvent(type)) {
//            var asQuit = SDL_Event.quit(segment);
//            data = new QuitEvent();
//        }
//        else if (type == SDL_EVENT_TERMINATING()) { data = new TerminatingEvent(); }
//        else if (type == SDL_EVENT_LOW_MEMORY()) { data = new LowMemoryEvent(); }
//        else if (type == SDL_EVENT_WILL_ENTER_BACKGROUND()) { data = new WillEnterBackgroundEvent(); }
//        else if (type == SDL_EVENT_DID_ENTER_BACKGROUND()) { data = new DidEnterBackgroundEvent(); }
//        else if (type == SDL_EVENT_WILL_ENTER_FOREGROUND()) { data = new WillEnterForegroundEvent(); }
//        else if (type == SDL_EVENT_DID_ENTER_FOREGROUND()) { data = new DidEnterForegroundEvent(); }
//        else if (type == SDL_EVENT_LOCALE_CHANGED()) { data = new LocaleChangedEvent(); }
//        else if (type == SDL_EVENT_SYSTEM_THEME_CHANGED()) { data = new SystemThemeChangedEvent(); }
//        else if (type == SDL_EVENT_KEYMAP_CHANGED()) { data = new KeymapChangedEvent(); }
//        else if (type == SDL_EVENT_SCREEN_KEYBOARD_SHOWN()) { data = new ScreenKeyboardShownEvent(); }
//        else if (type == SDL_EVENT_SCREEN_KEYBOARD_HIDDEN()) { data = new ScreenKeyboardHiddenEvent(); }
//        else if (type == SDL_EVENT_POLL_SENTINEL()) { data = new PollSentinelEvent(); }
//        else if (type == SDL_EVENT_PRIVATE0()) { data = new Private0Event(); }
//        else if (type == SDL_EVENT_PRIVATE1()) { data = new Private1Event(); }
//        else if (type == SDL_EVENT_PRIVATE2()) { data = new Private2Event(); }
//        else if (type == SDL_EVENT_PRIVATE3()) { data = new Private3Event(); }
//
//        if (data == null) {
//            data = new UnknownEvent();
//        }
//        return new Event(data, enumType, timestamp);

    }

    private static @Nullable String toStringOrNull(MemorySegment segment) {
        if (segment.address() == 0) {
            return null;
        }
        return segment.reinterpret(Integer.MAX_VALUE).getString(0);
    }

    private static String @Nullable[] toStringArray(MemorySegment arraySegment, int count) {
        if (arraySegment.address() == 0 || count <= 0) {
            return new String[0];
        }
        long arrayByteSize = count * ValueLayout.ADDRESS.byteSize();
        var boundedArraySegment = arraySegment.reinterpret(arrayByteSize);
        var result = new String[count];
        for (int i = 0; i < count; i++) {
            var pointer = boundedArraySegment.getAtIndex(ValueLayout.ADDRESS, i);
            result[i] = toStringOrNull(pointer);
        }
        return result;
    }

    public record UnknownEvent(
    ) implements EventData {}

    public record TerminatingEvent(
    ) implements EventData {}

    public record LowMemoryEvent(
    ) implements EventData {}

    public record WillEnterBackgroundEvent(
    ) implements EventData {}

    public record DidEnterBackgroundEvent(
    ) implements EventData {}

    public record WillEnterForegroundEvent(
    ) implements EventData {}

    public record DidEnterForegroundEvent(
    ) implements EventData {}

    public record LocaleChangedEvent(
    ) implements EventData {}

    public record SystemThemeChangedEvent(
    ) implements EventData {}

    public record KeymapChangedEvent(
    ) implements EventData {}

    public record ScreenKeyboardShownEvent(
    ) implements EventData {}

    public record ScreenKeyboardHiddenEvent(
    ) implements EventData {}

    public record PollSentinelEvent(
    ) implements EventData {}

    public record Private0Event(
    ) implements EventData {}

    public record Private1Event(
    ) implements EventData {}

    public record Private2Event(
    ) implements EventData {}

    public record Private3Event(
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
            int which
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
            @Nullable String text,
            int start,
            int length
    ) implements EventData {}

    public record TextEditingCandidatesEvent(
            int windowID,
            String @Nullable[] candidates,
            int selectedCandidate,
            boolean horizontal
    ) implements EventData {}

    public record TextInputEvent(
            int windowID,
            @Nullable String text
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
    ) implements EventData {}

    public record UserEvent(
            int type,
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
            @Nullable String source,
            @Nullable String data
    ) implements EventData {}

    public record ClipboardEvent(
            boolean owner,
            String @Nullable[] mimeTypes
    ) implements EventData {}


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


    package io.github.plixo2.sodalite.category.events;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.libsdl.sdl.SDL3_h.*;

/// @apiNote SDL_EventType
@RequiredArgsConstructor
enum EventType {

    QUIT(SDL_EVENT_QUIT()),
    TERMINATING(SDL_EVENT_TERMINATING()),
    LOW_MEMORY(SDL_EVENT_LOW_MEMORY()),
    WILL_ENTER_BACKGROUND(SDL_EVENT_WILL_ENTER_BACKGROUND()),
    DID_ENTER_BACKGROUND(SDL_EVENT_DID_ENTER_BACKGROUND()),
    WILL_ENTER_FOREGROUND(SDL_EVENT_WILL_ENTER_FOREGROUND()),
    DID_ENTER_FOREGROUND(SDL_EVENT_DID_ENTER_FOREGROUND()),
    LOCALE_CHANGED(SDL_EVENT_LOCALE_CHANGED()),
    SYSTEM_THEME_CHANGED(SDL_EVENT_SYSTEM_THEME_CHANGED()),

    DISPLAY_ORIENTATION(SDL_EVENT_DISPLAY_ORIENTATION()),
    DISPLAY_ADDED(SDL_EVENT_DISPLAY_ADDED()),
    DISPLAY_REMOVED(SDL_EVENT_DISPLAY_REMOVED()),
    DISPLAY_MOVED(SDL_EVENT_DISPLAY_MOVED()),
    DISPLAY_DESKTOP_MODE_CHANGED(SDL_EVENT_DISPLAY_DESKTOP_MODE_CHANGED()),
    DISPLAY_CURRENT_MODE_CHANGED(SDL_EVENT_DISPLAY_CURRENT_MODE_CHANGED()),
    DISPLAY_CONTENT_SCALE_CHANGED(SDL_EVENT_DISPLAY_CONTENT_SCALE_CHANGED()),
    DISPLAY_USABLE_BOUNDS_CHANGED(SDL_EVENT_DISPLAY_USABLE_BOUNDS_CHANGED()),

    WINDOW_SHOWN(SDL_EVENT_WINDOW_SHOWN()),
    WINDOW_HIDDEN(SDL_EVENT_WINDOW_HIDDEN()),
    WINDOW_EXPOSED(SDL_EVENT_WINDOW_EXPOSED()),
    WINDOW_MOVED(SDL_EVENT_WINDOW_MOVED()),
    WINDOW_RESIZED(SDL_EVENT_WINDOW_RESIZED()),
    WINDOW_PIXEL_SIZE_CHANGED(SDL_EVENT_WINDOW_PIXEL_SIZE_CHANGED()),
    WINDOW_METAL_VIEW_RESIZED(SDL_EVENT_WINDOW_METAL_VIEW_RESIZED()),
    WINDOW_MINIMIZED(SDL_EVENT_WINDOW_MINIMIZED()),
    WINDOW_MAXIMIZED(SDL_EVENT_WINDOW_MAXIMIZED()),
    WINDOW_RESTORED(SDL_EVENT_WINDOW_RESTORED()),
    WINDOW_MOUSE_ENTER(SDL_EVENT_WINDOW_MOUSE_ENTER()),
    WINDOW_MOUSE_LEAVE(SDL_EVENT_WINDOW_MOUSE_LEAVE()),
    WINDOW_FOCUS_GAINED(SDL_EVENT_WINDOW_FOCUS_GAINED()),
    WINDOW_FOCUS_LOST(SDL_EVENT_WINDOW_FOCUS_LOST()),
    WINDOW_CLOSE_REQUESTED(SDL_EVENT_WINDOW_CLOSE_REQUESTED()),
    WINDOW_HIT_TEST(SDL_EVENT_WINDOW_HIT_TEST()),
    WINDOW_ICCPROF_CHANGED(SDL_EVENT_WINDOW_ICCPROF_CHANGED()),
    WINDOW_DISPLAY_CHANGED(SDL_EVENT_WINDOW_DISPLAY_CHANGED()),
    WINDOW_DISPLAY_SCALE_CHANGED(SDL_EVENT_WINDOW_DISPLAY_SCALE_CHANGED()),
    WINDOW_SAFE_AREA_CHANGED(SDL_EVENT_WINDOW_SAFE_AREA_CHANGED()),
    WINDOW_OCCLUDED(SDL_EVENT_WINDOW_OCCLUDED()),
    WINDOW_ENTER_FULLSCREEN(SDL_EVENT_WINDOW_ENTER_FULLSCREEN()),
    WINDOW_LEAVE_FULLSCREEN(SDL_EVENT_WINDOW_LEAVE_FULLSCREEN()),
    WINDOW_DESTROYED(SDL_EVENT_WINDOW_DESTROYED()),
    WINDOW_HDR_STATE_CHANGED(SDL_EVENT_WINDOW_HDR_STATE_CHANGED()),

    KEY_DOWN(SDL_EVENT_KEY_DOWN()),
    KEY_UP(SDL_EVENT_KEY_UP()),
    TEXT_EDITING(SDL_EVENT_TEXT_EDITING()),
    TEXT_INPUT(SDL_EVENT_TEXT_INPUT()),
    KEYMAP_CHANGED(SDL_EVENT_KEYMAP_CHANGED()),
    KEYBOARD_ADDED(SDL_EVENT_KEYBOARD_ADDED()),
    KEYBOARD_REMOVED(SDL_EVENT_KEYBOARD_REMOVED()),
    TEXT_EDITING_CANDIDATES(SDL_EVENT_TEXT_EDITING_CANDIDATES()),
    SCREEN_KEYBOARD_SHOWN(SDL_EVENT_SCREEN_KEYBOARD_SHOWN()),
    SCREEN_KEYBOARD_HIDDEN(SDL_EVENT_SCREEN_KEYBOARD_HIDDEN()),
    MOUSE_MOTION(SDL_EVENT_MOUSE_MOTION()),
    MOUSE_BUTTON_DOWN(SDL_EVENT_MOUSE_BUTTON_DOWN()),
    MOUSE_BUTTON_UP(SDL_EVENT_MOUSE_BUTTON_UP()),
    MOUSE_WHEEL(SDL_EVENT_MOUSE_WHEEL()),
    MOUSE_ADDED(SDL_EVENT_MOUSE_ADDED()),
    MOUSE_REMOVED(SDL_EVENT_MOUSE_REMOVED()),
    JOYSTICK_AXIS_MOTION(SDL_EVENT_JOYSTICK_AXIS_MOTION()),
    JOYSTICK_BALL_MOTION(SDL_EVENT_JOYSTICK_BALL_MOTION()),
    JOYSTICK_HAT_MOTION(SDL_EVENT_JOYSTICK_HAT_MOTION()),
    JOYSTICK_BUTTON_DOWN(SDL_EVENT_JOYSTICK_BUTTON_DOWN()),
    JOYSTICK_BUTTON_UP(SDL_EVENT_JOYSTICK_BUTTON_UP()),
    JOYSTICK_ADDED(SDL_EVENT_JOYSTICK_ADDED()),
    JOYSTICK_REMOVED(SDL_EVENT_JOYSTICK_REMOVED()),
    JOYSTICK_BATTERY_UPDATED(SDL_EVENT_JOYSTICK_BATTERY_UPDATED()),
    JOYSTICK_UPDATE_COMPLETE(SDL_EVENT_JOYSTICK_UPDATE_COMPLETE()),
    GAMEPAD_AXIS_MOTION(SDL_EVENT_GAMEPAD_AXIS_MOTION()),
    GAMEPAD_BUTTON_DOWN(SDL_EVENT_GAMEPAD_BUTTON_DOWN()),
    GAMEPAD_BUTTON_UP(SDL_EVENT_GAMEPAD_BUTTON_UP()),
    GAMEPAD_ADDED(SDL_EVENT_GAMEPAD_ADDED()),
    GAMEPAD_REMOVED(SDL_EVENT_GAMEPAD_REMOVED()),
    GAMEPAD_REMAPPED(SDL_EVENT_GAMEPAD_REMAPPED()),
    GAMEPAD_TOUCHPAD_DOWN(SDL_EVENT_GAMEPAD_TOUCHPAD_DOWN()),
    GAMEPAD_TOUCHPAD_MOTION(SDL_EVENT_GAMEPAD_TOUCHPAD_MOTION()),
    GAMEPAD_TOUCHPAD_UP(SDL_EVENT_GAMEPAD_TOUCHPAD_UP()),
    GAMEPAD_SENSOR_UPDATE(SDL_EVENT_GAMEPAD_SENSOR_UPDATE()),
    GAMEPAD_UPDATE_COMPLETE(SDL_EVENT_GAMEPAD_UPDATE_COMPLETE()),
    GAMEPAD_STEAM_HANDLE_UPDATED(SDL_EVENT_GAMEPAD_STEAM_HANDLE_UPDATED()),
    FINGER_DOWN(SDL_EVENT_FINGER_DOWN()),
    FINGER_UP(SDL_EVENT_FINGER_UP()),
    FINGER_MOTION(SDL_EVENT_FINGER_MOTION()),
    FINGER_CANCELED(SDL_EVENT_FINGER_CANCELED()),
    PINCH_BEGIN(SDL_EVENT_PINCH_BEGIN()),
    PINCH_UPDATE(SDL_EVENT_PINCH_UPDATE()),
    PINCH_END(SDL_EVENT_PINCH_END()),
    CLIPBOARD_UPDATE(SDL_EVENT_CLIPBOARD_UPDATE()),
    DROP_FILE(SDL_EVENT_DROP_FILE()),
    DROP_TEXT(SDL_EVENT_DROP_TEXT()),
    DROP_BEGIN(SDL_EVENT_DROP_BEGIN()),
    DROP_COMPLETE(SDL_EVENT_DROP_COMPLETE()),
    DROP_POSITION(SDL_EVENT_DROP_POSITION()),
    AUDIO_DEVICE_ADDED(SDL_EVENT_AUDIO_DEVICE_ADDED()),
    AUDIO_DEVICE_REMOVED(SDL_EVENT_AUDIO_DEVICE_REMOVED()),
    AUDIO_DEVICE_FORMAT_CHANGED(SDL_EVENT_AUDIO_DEVICE_FORMAT_CHANGED()),
    SENSOR_UPDATE(SDL_EVENT_SENSOR_UPDATE()),
    PEN_PROXIMITY_IN(SDL_EVENT_PEN_PROXIMITY_IN()),
    PEN_PROXIMITY_OUT(SDL_EVENT_PEN_PROXIMITY_OUT()),
    PEN_DOWN(SDL_EVENT_PEN_DOWN()),
    PEN_UP(SDL_EVENT_PEN_UP()),
    PEN_BUTTON_DOWN(SDL_EVENT_PEN_BUTTON_DOWN()),
    PEN_BUTTON_UP(SDL_EVENT_PEN_BUTTON_UP()),
    PEN_MOTION(SDL_EVENT_PEN_MOTION()),
    PEN_AXIS(SDL_EVENT_PEN_AXIS()),
    CAMERA_DEVICE_ADDED(SDL_EVENT_CAMERA_DEVICE_ADDED()),
    CAMERA_DEVICE_REMOVED(SDL_EVENT_CAMERA_DEVICE_REMOVED()),
    CAMERA_DEVICE_APPROVED(SDL_EVENT_CAMERA_DEVICE_APPROVED()),
    CAMERA_DEVICE_DENIED(SDL_EVENT_CAMERA_DEVICE_DENIED()),
    RENDER_TARGETS_RESET(SDL_EVENT_RENDER_TARGETS_RESET()),
    RENDER_DEVICE_RESET(SDL_EVENT_RENDER_DEVICE_RESET()),
    RENDER_DEVICE_LOST(SDL_EVENT_RENDER_DEVICE_LOST()),
    PRIVATE0(SDL_EVENT_PRIVATE0()),
    PRIVATE1(SDL_EVENT_PRIVATE1()),
    PRIVATE2(SDL_EVENT_PRIVATE2()),
    PRIVATE3(SDL_EVENT_PRIVATE3()),
    POLL_SENTINEL(SDL_EVENT_POLL_SENTINEL()),

    /// Events SDL_EVENT_USER through SDL_EVENT_LAST are for your use
    /// (0x8000 through 0xFFFF)
    USER(SDL_EVENT_USER()),


    UNKNOWN(-1)

    ;

    private static final Map<Integer, EventType> codeMap = Arrays.stream(values()).collect(
        Collectors.toMap(EventType::code, Function.identity())
    );

    private static EventType fromCode(int code) {
        if (isUserEvent(code)) {
            return USER;
        }
        return codeMap.getOrDefault(code, UNKNOWN);
    }

    private static boolean isUserEvent(int code) {
        return code >= SDL_EVENT_USER() && code <= SDL_EVENT_LAST();
    }

    private final int code;
    private int code() {
        return this.code;
    }
}



 */


}
