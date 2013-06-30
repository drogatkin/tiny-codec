package net.didion.loopy;

import java.io.IOException;

public class LoopyException extends Exception {
    public LoopyException() {
    }

    public LoopyException(String message) {
        super(message);
    }

    public LoopyException(Throwable cause) {
        super(cause);
    }

    public LoopyException(String message, Throwable cause) {
        super(message, cause);
    }
}