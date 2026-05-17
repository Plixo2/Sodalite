# SDL 3.0 API by Category

## Basics

| **Functionality**           | *Header**        |
|-----------------------------|------------------|
| Application entry points    | SDL_main.h       |
| Initialization and Shutdown | SDL_init.h       |
| Configuration Variables     | SDL_hints.h      |
| Object Properties           | SDL_properties.h |
| Error Handling              | SDL_error.h      |
| Log Handling                | SDL_log.h        |
| Assertions                  | SDL_assert.h     |
| Querying SDL Version        | SDL_version.h    |

## Video

| **Functionality**                     | **Header**      |
|---------------------------------------|-----------------|
| Display and Window Management         | SDL_video.h     |
| 2D Accelerated Rendering              | SDL_render.h    |
| Pixel Formats and Conversion Routines | SDL_pixels.h    |
| Blend modes                           | SDL_blendmode.h |
| Rectangle Functions                   | SDL_rect.h      |
| Surface Creation and Simple Drawing   | SDL_surface.h   |
| Clipboard Handling                    | SDL_clipboard.h |
| Vulkan Support                        | SDL_vulkan.h    |
| Metal Support                         | SDL_metal.h     |
| OpenXR Support                        | SDL_openxr.h    |
| Camera Support                        | SDL_camera.h    |

## Input Events

| **Functionality**  | **Header**     |
|--------------------|----------------|
| Event Handling     | SDL_events.h   |
| Keyboard Support   | SDL_keyboard.h |
| Keyboard Keycodes  | SDL_keycode.h  |
| Keyboard Scancodes | SDL_scancode.h |
| Mouse Support      | SDL_mouse.h    |
| Joystick Support   | SDL_joystick.h |
| Gamepad Support    | SDL_gamepad.h  |
| Touch Support      | SDL_touch.h    |
| Pen Support        | SDL_pen.h      |
| Sensors            | SDL_sensor.h   |
| HIDAPI             | SDL_hidapi.h   |

## Force Feedback ("Haptic")

| **Functionality**      | **Header**   |
|------------------------|--------------|
| Force Feedback Support | SDL_haptic.h |

## Audio

| **Functionality**                     | **Header**  |
|---------------------------------------|-------------|
| Audio Playback, Recording, and Mixing | SDL_audio.h |

## GPU

| **Functionality**            | **Header** |
|------------------------------|------------|
| 3D Rendering and GPU Compute | SDL_gpu.h  |

## Threads

| **Functionality**                 | **Header**   |
|-----------------------------------|--------------|
| Thread Management                 | SDL_thread.h |
| Thread Synchronization Primitives | SDL_mutex.h  |
| Atomic Operations                 | SDL_atomic.h |

## Time

| **Functionality** | **Header**  |
|-------------------|-------------|
| Timer Support     | SDL_timer.h |
| Date and Time     | SDL_time.h  |

## File and I/O Abstractions

| **Functionality**   | **Header**       |
|---------------------|------------------|
| Filesystem Access   | SDL_filesystem.h |
| Storage Abstraction | SDL_storage.h    |
| I/O Streams         | SDL_iostream.h   |
| Async I/O           | SDL_asyncio.h    |

## Platform and CPU Information

| **Functionality**             | **Header**     |
|-------------------------------|----------------|
| Platform Detection            | SDL_platform.h |
| CPU Feature Detection         | SDL_cpuinfo.h  |
| Compiler Intrinsics Detection | SDL_intrin.h   |
| Byte Order and Byte Swapping  | SDL_endian.h   |
| Bit Manipulation              | SDL_bits.h     |

## Additional Functionality

| **Functionality**               | **Header**       |
|---------------------------------|------------------|
| Shared Object/DLL Management    | SDL_loadso.h     |
| Process Control                 | SDL_process.h    |
| Power Management Status         | SDL_power.h      |
| Message Boxes                   | SDL_messagebox.h |
| File Dialogs                    | SDL_dialog.h     |
| System Tray                     | SDL_tray.h       |
| Locale Info                     | SDL_locale.h     |
| Platform-specific Functionality | SDL_system.h     |
| Standard Library Functionality  | SDL_stdinc.h     |
| GUIDs                           | SDL_guid.h       |
| Miscellaneous                   | SDL_misc.h       |