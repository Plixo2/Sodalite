package io.github.plixo2.sodalite.category.version;


/// Wrapper for the return value of `SDL_GetVersion`
public class VersionNumber {
    private final int version;

    VersionNumber(int version) {
        this.version = version;
    }

    public int major() {
        return (this.version) / 1000000;
    }

    public int minor() {
        return ((this.version) / 1000) % 1000;
    }

    public int micro() {
        return (this.version) % 1000;
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d", major(), minor(), micro());
    }

}
