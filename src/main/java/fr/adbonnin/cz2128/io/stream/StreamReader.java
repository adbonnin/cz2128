package fr.adbonnin.cz2128.io.stream;

import java.io.IOException;
import java.io.InputStream;

public interface StreamReader {

    <T> T read(ReadFunction<? extends T> function) throws IOException;

    interface ReadFunction<T> {

        T read(InputStream input) throws IOException;
    }
}
