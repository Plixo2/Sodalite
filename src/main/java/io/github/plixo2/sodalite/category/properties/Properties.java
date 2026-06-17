package io.github.plixo2.sodalite.category.properties;

import io.github.plixo2.sodalite.resource.ResourceSet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.libsdl.sdl.SDL_EnumeratePropertiesCallback;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.*;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;

/// @sdlCategory CategoryProperties
public class Properties {
    private Properties() {}

    /// @sdlAPI SDL_CreateProperties
    public static PropertyGroup createProperties(
            ResourceSet resources
    ) {
        var id = SDL_CreateProperties();
        check(id != 0);

        return new PropertyGroup(
                Objects.requireNonNull(resources, "resources"),
                id
        );
    }

    /// @sdlAPI SDL_GetGlobalProperties
    public static PropertyGroup getGlobalProperties() {
        var id = SDL_GetGlobalProperties();
        check(id != 0);
        return new PropertyGroup(null, id);
    }

    /// @sdlAPI SDL_DestroyProperties
    static void destroyProperties(int id) {
        SDL_DestroyProperties(id);
    }


    /// @sdlAPI SDL_ClearProperty
    static void clearProperty(
            PropertyGroup group,
            PropertyKey<?> property
    ) {
        check(SDL_ClearProperty(group.id(), property.nameSegment()));
    }

    /// @sdlAPI SDL_LockProperties
    static void lockProperties(
            PropertyGroup group
    ) {
        check(SDL_LockProperties(group.id()));
    }

    /// @sdlAPI SDL_UnlockProperties
    static void unlockProperties(
            PropertyGroup group
    ) {
        SDL_UnlockProperties(group.id());
    }

    /// @sdlAPI SDL_CopyProperties
    public static void copyProperties(
            PropertyGroup src,
            PropertyGroup dest
    ) {
        check(SDL_CopyProperties(dest.id(), src.id()));
    }

    /// @sdlAPI SDL_GetPropertyType
    static @Nullable PropertyType getPropertyType(
            PropertyGroup group,
            PropertyKey<?> property
    ) {
        var code = SDL_GetPropertyType(group.id(), property.nameSegment());
        return PropertyType.fromCode(code);
    }

    /// @sdlAPI SDL_HasProperty
    static boolean hasProperty(
            PropertyGroup group,
            PropertyKey<?> property
    ) {
        return SDL_HasProperty(group.id(), property.nameSegment());
    }

    /// @sdlAPI SDL_EnumerateProperties
    /// @sdlOther SDL_EnumeratePropertiesCallback
    static List<PropertyKey<?>> enumerateProperties(
            PropertyGroup group
    ) {
        var list = new ArrayList<PropertyKey<?>>();
        try (var arena = Arena.ofConfined()) {
            var callback = SDL_EnumeratePropertiesCallback.allocate(
                    (userData, prop, name) -> {
                        var type = PropertyType.fromCode(SDL_GetPropertyType(prop, name));
                        if (type != null) {
                            list.add(PropertyKey.of(type, name));
                        }
                    },
                    arena
            );
            SDL_EnumerateProperties(
                    group.id(),
                    callback,
                    MemorySegment.NULL
            );
        }
        return list;
    }


    /// @sdlAPI SDL_SetPointerProperty
    static void setPointerProperty(
            PropertyGroup group,
            PropertyKey<MemorySegment> property,
            MemorySegment value
    ) {
        check(SDL_SetPointerProperty(group.id(), property.nameSegment(), value));
    }
    /// @sdlAPI SDL_GetPointerProperty
    static MemorySegment getPointerProperty(
            PropertyGroup group,
            PropertyKey<MemorySegment> property,
            @Nullable MemorySegment defaultValue
    ) {
        var defaultVal = defaultValue == null ? MemorySegment.NULL : defaultValue;
        var segment = SDL_GetPointerProperty(group.id(), property.nameSegment(), defaultVal);
        if (segment.address() == 0) {
            return null;
        } else {
            return segment;
        }
    }

    /// @sdlAPI SDL_SetStringProperty
    static void setStringProperty(
            PropertyGroup group,
            PropertyKey<String> property,
            @Nullable String value
    ) {
        try (var arena = Arena.ofConfined()) {
            var valueSegment = allocNullString(arena, value);
            check(SDL_SetStringProperty(group.id(), property.nameSegment(), valueSegment));
        }
    }
    /// @sdlAPI SDL_GetStringProperty
    @Contract("_, _, !null -> !null")
    static @Nullable String getStringProperty(
            PropertyGroup group,
            PropertyKey<String> property,
            @Nullable String defaultValue
    ) {
        try (var arena = Arena.ofConfined()) {
            var valueSegment = allocNullString(arena, defaultValue);

            var resultSegment = SDL_GetStringProperty(group.id(), property.nameSegment(), valueSegment);
            return getNullString(resultSegment);
        }
    }

    /// @sdlAPI SDL_SetNumberProperty
    static void setNumberProperty(
            PropertyGroup group,
            PropertyKey<Long> property,
            long value
    ) {
        check(SDL_SetNumberProperty(group.id(), property.nameSegment(), value));
    }
    /// @sdlAPI SDL_GetNumberProperty
    static long getNumberProperty(
            PropertyGroup group,
            PropertyKey<Long> property,
            long defaultValue
    ) {
        return SDL_GetNumberProperty(group.id(), property.nameSegment(), defaultValue);
    }

    /// @sdlAPI SDL_SetFloatProperty
    static void setFloatProperty(
            PropertyGroup group,
            PropertyKey<Float> property,
            float value
    ) {
        check(SDL_SetFloatProperty(group.id(), property.nameSegment(), value));
    }
    /// @sdlAPI SDL_GetFloatProperty
    static float getFloatProperty(
            PropertyGroup group,
            PropertyKey<Float> property,
            float defaultValue
    ) {
        return SDL_GetFloatProperty(group.id(), property.nameSegment(), defaultValue);
    }

    /// @sdlAPI SDL_SetBooleanProperty
    static void setBooleanProperty(
            PropertyGroup group,
            PropertyKey<Boolean> property,
            boolean value
    ) {
        check(SDL_SetBooleanProperty(group.id(), property.nameSegment(), value));
    }
    /// @sdlAPI SDL_GetBooleanProperty
    static boolean getBooleanProperty(
            PropertyGroup group,
            PropertyKey<Boolean> property,
            boolean defaultValue
    ) {
        return SDL_GetBooleanProperty(group.id(), property.nameSegment(), defaultValue);
    }



}
