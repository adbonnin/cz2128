package fr.adbonnin.cz2128.io;

import java.io.Closeable;
import java.io.IOException;

public final class IOUtils {

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (IOException e) {
                // Nothing to do
            }
        }
    }

    private IOUtils() { /* Cannot be instantiated */}
}
