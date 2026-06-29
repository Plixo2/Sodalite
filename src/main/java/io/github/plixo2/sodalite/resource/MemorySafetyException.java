package io.github.plixo2.sodalite.resource;

import lombok.Getter;

public class MemorySafetyException extends RuntimeException {

    @Getter
    private final Object resource;

    public MemorySafetyException(Object resource, String message) {
        super(message);
        this.resource = resource;
    }

    protected static String readableClass(Object resourceClass) {
        if (resourceClass == null) {
            return "null"; // should not happen
        }
        return resourceClass.getClass().getTypeName();
    }
}
