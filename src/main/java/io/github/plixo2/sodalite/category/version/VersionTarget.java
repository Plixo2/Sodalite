package io.github.plixo2.sodalite.category.version;

/// Parameter for [Version#getVersion] & [Version#getRevision]
public enum VersionTarget {

    COMPILED,    // Get the version sdl was compiled with
    LINKED,       // Get the version sdl was linked with

    ;

}
