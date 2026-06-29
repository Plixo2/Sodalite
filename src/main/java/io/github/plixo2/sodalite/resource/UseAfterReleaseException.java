package io.github.plixo2.sodalite.resource;

import org.jetbrains.annotations.Nullable;

public class UseAfterReleaseException extends MemorySafetyException {

    UseAfterReleaseException(ResourceObject resource) {
        this(resource, null);
    }

    public UseAfterReleaseException(Object resource, @Nullable String message) {
        var msg = "Cannot use '" + readableClass(resource) + "' after it has been released";
        if (message != null && message.isEmpty()) {
            msg += ": " + message;
        }
        super(resource, msg);
    }
}