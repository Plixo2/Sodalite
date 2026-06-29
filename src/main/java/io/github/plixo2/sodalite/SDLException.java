package io.github.plixo2.sodalite;

public class SDLException extends RuntimeException {

    public SDLException(String message) {
        super(message);
    }

    public SDLException(String message, Throwable cause) {
        super(message, cause);
    }
}
