package io.github.plixo2.sodalite.resource;

import org.jetbrains.annotations.Nullable;


public class DoubleReleaseException extends MemorySafetyException {

    DoubleReleaseException(ResourceObject resource) {
        this(resource, null);
    }

    public DoubleReleaseException(Object resource, @Nullable String message) {
        var msg = "Attempted to release '" + readableClass(resource) + "' multiple times";
        if (message != null && message.isEmpty()) {
            msg += ": " + message;
        }
        super(resource, msg);
    }

}
