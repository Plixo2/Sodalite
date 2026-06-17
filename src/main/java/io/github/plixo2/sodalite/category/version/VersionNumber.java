package io.github.plixo2.sodalite.category.version;


import org.jetbrains.annotations.NotNull;

/// Wrapper for the return value of `SDL_GetVersion`
public class VersionNumber implements Comparable<VersionNumber> {
    private final int num;

    VersionNumber(int num) {
        this.num = num;
    }

    /// @sdlAPI SDL_VERSIONNUM
    public VersionNumber(int major, int minor, int micro) {
        this.num = ((major) * 1000000 + (minor) * 1000 + (micro));
    }

    public static VersionNumber of(int major, int minor, int micro) {
        return new VersionNumber(major, minor, micro);
    }

    public int number() {
        return this.num;
    }

    /// @sdlAPI SDL_VERSIONNUM_MAJOR
    public int major() {
        return (this.num) / 1000000;
    }

    /// @sdlAPI SDL_VERSIONNUM_MINOR
    public int minor() {
        return ((this.num) / 1000) % 1000;
    }

    /// @sdlAPI SDL_VERSIONNUM_MICRO
    public int micro() {
        return (this.num) % 1000;
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d", major(), minor(), micro());
    }

    /// @sdlAPI SDL_VERSION_ATLEAST
    @Override
    public int compareTo(@NotNull VersionNumber o) {
        return Integer.compare(this.num, o.num);
    }
}
