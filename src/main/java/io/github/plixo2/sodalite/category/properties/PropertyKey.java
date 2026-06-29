package io.github.plixo2.sodalite.category.properties;

import io.github.plixo2.sodalite.SDLException;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Objects;

public sealed abstract class PropertyKey<T> implements PropertyKeys {

    @Getter
    private final PropertyType type;

    private @Nullable String name;

    @Getter
    private final MemorySegment nameSegment;

    public PropertyKey(
            PropertyType type,
            MemorySegment nameSegment
    ) {
        this.type = type;
        this.nameSegment = nameSegment;
    }

    public synchronized String asString() {
        if (this.name == null) {
            this.name = this.nameSegment.getString(0);
        }
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PropertyKey<?> that)) {
            return false;
        }
        return this.type == that.type && Objects.equals(asString(), that.asString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, asString());
    }

    abstract void set(PropertyGroup group, T value) throws SDLException;
    abstract T get(PropertyGroup group, T defaultValue);

    public static PropertyKey<MemorySegment> ofPointer(String name) {
        return new PointerProperty(Arena.ofAuto().allocateFrom(name));
    }
    public static PropertyKey<String> ofString(String name) {
        return new StringProperty(Arena.ofAuto().allocateFrom(name));
    }
    public static PropertyKey<Long> ofNumber(String name) {
        return new NumberProperty(Arena.ofAuto().allocateFrom(name));
    }
    public static PropertyKey<Float> ofFloat(String name) {
        return new FloatProperty(Arena.ofAuto().allocateFrom(name));
    }
    public static PropertyKey<Boolean> ofBoolean(String name) {
        return new BooleanProperty(Arena.ofAuto().allocateFrom(name));
    }

    public static PropertyKey<?> of(
            PropertyType type,
            String name
    ) {
        return of(type, Arena.ofAuto().allocateFrom(name));
    }

    public static PropertyKey<?> of(
            PropertyType type,
            MemorySegment nameSegment
    ) {
        return switch (type) {
            case POINTER -> new PointerProperty(nameSegment);
            case STRING -> new StringProperty(nameSegment);
            case NUMBER -> new NumberProperty(nameSegment);
            case FLOAT -> new FloatProperty(nameSegment);
            case BOOLEAN -> new BooleanProperty(nameSegment);
        };
    }

    public final static class PointerProperty extends PropertyKey<MemorySegment> {
        PointerProperty(MemorySegment nameSegment) {
            super(PropertyType.POINTER, nameSegment);
        }

        @Override
        void set(PropertyGroup group, MemorySegment value) throws SDLException {
            Properties.setPointerProperty(group, this, value);
        }

        @Override
        @Contract("_, !null -> !null")
        MemorySegment get(PropertyGroup group, @Nullable MemorySegment defaultValue) {
            return Properties.getPointerProperty(group, this, defaultValue);
        }
    }
    public final static class StringProperty extends PropertyKey<String> {
        StringProperty(MemorySegment nameSegment) {
            super(PropertyType.STRING, nameSegment);
        }

        @Override
        void set(PropertyGroup group, String value) throws SDLException {
            Properties.setStringProperty(group, this, value);
        }

        @Override
        @Contract("_, !null -> !null")
        String get(PropertyGroup group, @Nullable String defaultValue) {
            return Properties.getStringProperty(group, this, defaultValue);
        }
    }
    public final static class NumberProperty extends PropertyKey<Long> {
        NumberProperty(MemorySegment nameSegment) {
            super(PropertyType.NUMBER, nameSegment);
        }

        @Override
        void set(PropertyGroup group, Long value) throws SDLException {
            Properties.setNumberProperty(group, this, value);
        }

        @Override
        Long get(PropertyGroup group, Long defaultValue) {
            if (defaultValue == null) {
                defaultValue = 0L;
            }
            return Properties.getNumberProperty(group, this, defaultValue);
        }
    }
    public final static class FloatProperty extends PropertyKey<Float> {
        FloatProperty(MemorySegment nameSegment) {
            super(PropertyType.FLOAT, nameSegment);
        }

        @Override
        void set(PropertyGroup group, Float value) throws SDLException {
            Properties.setFloatProperty(group, this, value);
        }

        @Override
        Float get(PropertyGroup group, Float defaultValue) {
            if (defaultValue == null) {
                defaultValue = 0f;
            }
            return Properties.getFloatProperty(group, this, defaultValue);
        }
    }

    public final static class BooleanProperty extends PropertyKey<Boolean> {
        BooleanProperty(MemorySegment nameSegment) {
            super(PropertyType.BOOLEAN, nameSegment);
        }

        @Override
        void set(PropertyGroup group, Boolean value) throws SDLException {
            Properties.setBooleanProperty(group, this, value);
        }

        @Override
        Boolean get(PropertyGroup group, Boolean defaultValue) {
            if (defaultValue == null) {
                defaultValue = false;
            }
            return Properties.getBooleanProperty(group, this, defaultValue);
        }
    }


}
