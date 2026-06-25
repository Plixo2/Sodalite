package io.github.plixo2.sodalite.io.image;

import lombok.Getter;

public class ImageIOException extends Exception {
    @Getter
    private final String reason;

    ImageIOException(String reason) {
        super("Failed to load image: " + reason);
        this.reason = reason;
    }

}