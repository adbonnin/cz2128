package fr.adbonnin.cz2128.serializer;

import java.io.IOException;

class JacksonIOException extends RuntimeException {

    private final IOException cause;

    public JacksonIOException(IOException cause) {
        super(cause);
        this.cause = cause;
    }

    @Override
    public IOException getCause() {
        return cause;
    }
}
