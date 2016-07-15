package fr.adbonnin.albedo.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public abstract class InputStreamWrapper extends InputStream {

    private final InputStream input;

    public InputStreamWrapper(InputStream input) {
        this.input = Objects.requireNonNull(input);
    }

    @Override
    public int available() throws IOException {
        return input.available();
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

    @Override
    public void mark(int readlimit) {
        input.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return input.markSupported();
    }

    @Override
    public int read() throws IOException {
        return input.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return input.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return input.read(b, off, len);
    }

    @Override
    public void reset() throws IOException {
        input.reset();
    }

    @Override
    public long skip(long n) throws IOException {
        return input.skip(n);
    }

    @Override
    public int hashCode() {
        return input.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return input.equals(obj);
    }

    @Override
    public String toString() {
        return input.toString();
    }
}
