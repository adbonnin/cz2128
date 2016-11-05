package fr.adbonnin.cz2128.io.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StreamProcessor {

    <T> T read(ReadFunction<? extends T> function) throws IOException;

    long read(ReadLongFunction function) throws IOException;

    boolean write(WriteBooleanFunction function) throws IOException;

    interface ReadFunction<T> {

        T read(InputStream input) throws IOException;
    }

    interface ReadLongFunction {

        long read(InputStream input) throws IOException;
    }

    interface WriteBooleanFunction {

        boolean write(InputStream input, OutputStream output) throws IOException;
    }
}
