package fr.adbonnin.cz2128.io.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StreamWriter {

    <T> T write(WriteFunction<? extends T> function) throws IOException;

    interface WriteFunction<T> {

        T write(InputStream input, OutputStream output) throws IOException;
    }
}
