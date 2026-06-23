package io.github.plixo2.sodalite.category.version;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/// Wrapper for the return value of `SDL_GetVersion`
@EqualsAndHashCode
public class VersionNumber implements Comparable<VersionNumber> {
    @Getter
    private final int value;

    VersionNumber(int value) {
        this.value = value;
    }

    /// @sdlAPI SDL_VERSIONNUM
    public VersionNumber(int major, int minor, int micro) {
        this.value = ((major) * 1000000 + (minor) * 1000 + (micro));
    }

    public static VersionNumber of(int major, int minor, int micro) {
        return new VersionNumber(major, minor, micro);
    }
    public static VersionNumber of(int value) {
        return new VersionNumber(value);
    }

    /// @sdlAPI SDL_VERSIONNUM_MAJOR
    public int major() {
        return (this.value) / 1000000;
    }

    /// @sdlAPI SDL_VERSIONNUM_MINOR
    public int minor() {
        return ((this.value) / 1000) % 1000;
    }

    /// @sdlAPI SDL_VERSIONNUM_MICRO
    public int micro() {
        return (this.value) % 1000;
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d", major(), minor(), micro());
    }

    /// @sdlAPI SDL_VERSION_ATLEAST
    @Override
    public int compareTo(@NotNull VersionNumber o) {
        return Integer.compare(this.value, o.value);
    }
}
