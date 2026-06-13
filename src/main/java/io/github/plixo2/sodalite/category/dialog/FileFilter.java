package io.github.plixo2.sodalite.category.dialog;

import org.libsdl.sdl.SDL_DialogFileFilter;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.List;

/// @sdlAPI SDL_DialogFileFilter
public record FileFilter(
        String name,
        String... patterns
) {
    public FileFilter(String name, String... patterns) {
        this.name = name;
        this.patterns = patterns.clone();
    }
    public FileFilter(String name, List<String> patterns) {
        this(name, patterns.toArray(String[]::new));
    }
    
    public static FileFilter of(String name, String... patterns) {
        return new FileFilter(name, patterns);
    }
    public static FileFilter of(String name, List<String> patterns) {
        return new FileFilter(name, patterns);
    }
    public static FileFilter ofAny(String name) {
        return new FileFilter(name);
    }

    void put(MemorySegment segment, Arena arena) {
        var nameSegment = arena.allocateFrom(this.name);
        var patternsSegment = arena.allocateFrom(
                this.patterns.length == 0
                        ? "*"
                        : String.join(";", this.patterns)
        );

        SDL_DialogFileFilter.initialize(segment, nameSegment, patternsSegment);
    }

}
