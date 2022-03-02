package fr.adbonnin.cz2128.io;

import java.io.InputStream;

public class StreamUtils {

    public static InputStream nullInputStream() {
        return NULL_INPUT_STREAM;
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

    private StreamUtils() { /* Cannot be instantiated */ }
}
