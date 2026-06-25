package io.github.plixo2.sodalite.category.properties;

import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.util.List;


/// @sdlAPI SDL_PropertiesID
@ToString
public class PropertyGroup extends ResourceObject {

    private final int id;

    PropertyGroup(
            @Nullable ResourceSet resources,
            int id
    ) {
        if (resources != null) {
            resources.register(this, () -> Properties.destroyProperties(id));
        }
        this.id = id;
    }

    public int id() {
        ensureNotReleased();
        return this.id;
    }

    public List<PropertyKey<?>> properties() {
        return Properties.enumerateProperties(this);
    }

    public <T> void set(PropertyKey<T> property, T value) {
        property.set(this, value);
    }
    public <T> T get(PropertyKey<T> property, T defaultValue) {
        return property.get(this, defaultValue);
    }

    public void clear(PropertyKey<?> property) {
        Properties.clearProperty(this, property);
    }

    public void lock() {
        Properties.lockProperties(this);
    }
    public void unlock() {
        Properties.unlockProperties(this);
    }
    public boolean has(PropertyKey<?> property) {
        return Properties.hasProperty(this, property);
    }

    public @Nullable PropertyType typeOf(PropertyKey<?> property) {
        return Properties.getPropertyType(this, property);
    }


    public PropertyGroup copy(ResourceSet resource) {
        var copy = Properties.createProperties(resource);
        Properties.copyProperties(this, copy);
        return copy;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PropertyGroup other && this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.id);
    }
}
