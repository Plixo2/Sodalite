package io.github.plixo2.sodalite.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.libsdl.sdl.*;

import java.lang.foreign.MemorySegment;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

@RequiredArgsConstructor
public final class Event {
    @Getter
    private final EventData data;
    @Getter
    private final int type;
    @Getter
    private final long timestamp;

    private static boolean isUserEvent(int code) {
        return code >= SDL_EVENT_USER() && code <= SDL_EVENT_LAST();
    }

    public static boolean isDisplayEvent(int code) {
        return code >= SDL_EVENT_DISPLAY_FIRST() && code <= SDL_EVENT_DISPLAY_LAST();
    }

    private static boolean isWindowEvent(int code) {
        return code >= SDL_EVENT_WINDOW_FIRST() && code <= SDL_EVENT_WINDOW_LAST();
    }

    public sealed interface EventData {

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
            data = new DisplayEvent( displayID, data1, data2);
        } else {
            // rest here
        }




        if (data == null) {
            data = new UnknownEvent();
        }
        return new Event(data, type, timestamp);
    }



    public record UnknownEvent(
    ) implements EventData {}

    public record UserEvent(
            int windowID,
            int code,
            MemorySegment data1,
            MemorySegment data2
    ) implements EventData {}

    public record WindowEvent(
            int windowID,
            int data1,
            int data2
    ) implements EventData {}

    public record DisplayEvent(
            int displayID,
            int data1,
            int data2
    ) implements EventData {}

}



/*


typedef union SDL_Event
{
    Uint32 type;
    SDL_DisplayEvent display;
    SDL_WindowEvent window;
    SDL_KeyboardDeviceEvent kdevice;
    SDL_KeyboardEvent key;
    SDL_TextEditingEvent edit;
    SDL_TextEditingCandidatesEvent edit_candidates;
    SDL_TextInputEvent text;
    SDL_MouseDeviceEvent mdevice;
    SDL_MouseMotionEvent motion;
    SDL_MouseButtonEvent button;
    SDL_MouseWheelEvent wheel;
    SDL_JoyDeviceEvent jdevice;
    SDL_JoyAxisEvent jaxis;
    SDL_JoyBallEvent jball;
    SDL_JoyHatEvent jhat;
    SDL_JoyButtonEvent jbutton;
    SDL_JoyBatteryEvent jbattery;
    SDL_GamepadDeviceEvent gdevice;
    SDL_GamepadAxisEvent gaxis;
    SDL_GamepadButtonEvent gbutton;
    SDL_GamepadTouchpadEvent gtouchpad;
    SDL_GamepadSensorEvent gsensor;
    SDL_AudioDeviceEvent adevice;
    SDL_CameraDeviceEvent cdevice;
    SDL_SensorEvent sensor;
    SDL_QuitEvent quit;
    SDL_UserEvent user;
    SDL_TouchFingerEvent tfinger;
    SDL_PinchFingerEvent pinch;
    SDL_PenProximityEvent pproximity;
    SDL_PenTouchEvent ptouch;
    SDL_PenMotionEvent pmotion;
    SDL_PenButtonEvent pbutton;
    SDL_PenAxisEvent paxis;
    SDL_RenderEvent render;
    SDL_DropEvent drop;
    SDL_ClipboardEvent clipboard;

}

 */
