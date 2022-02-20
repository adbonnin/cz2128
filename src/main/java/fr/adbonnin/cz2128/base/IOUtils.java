package fr.adbonnin.cz2128.base;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class IOUtils {

    public static InputStream nullInputStream() {
        return NULL_INPUT_STREAM;
    }

    public static void deleteIfExistsQuietly(Path path) {
        if (path != null) {
            try {
                Files.deleteIfExists(path);
            }
            catch (IOException ignored) {
                // nothing to do
            }
        }
    }

    private static final InputStream NULL_INPUT_STREAM = new InputStream() {

        @Override
        public int available() {
            return 0;
        }

        @Override
        public int read() {
            return -1;
        }

        @Override
        public int read(byte[] b) {
            return -1;
        }

        @Override
        public int read(byte[] b, int off, int len) {
            return len == 0 ? 0 : -1;
        }

        @Override
        public long skip(long n) {
            return 0;
        }
    };

    private IOUtils() { /* Cannot be instantiated */ }
}
