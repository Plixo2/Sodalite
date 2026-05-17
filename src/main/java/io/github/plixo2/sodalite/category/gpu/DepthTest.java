package io.github.plixo2.sodalite.category.gpu;


public enum DepthTest {
    ENABLED,
    DISABLED,

    ;

    public boolean value() {
        return this == ENABLED;
    }
}
