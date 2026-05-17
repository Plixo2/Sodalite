package io.github.plixo2.sodalite.category.gpu;


public enum DepthWrite {
    ENABLED,
    DISABLED,

    ;

    public boolean value() {
        return this == ENABLED;
    }
}
