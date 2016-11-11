package fr.adbonnin.cz2128.io.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static fr.adbonnin.cz2128.io.IOUtils.closeQuietly;

public abstract class SwapStreamProcessor implements StreamProcessor {

    private final Lock readLock;

    private final Lock writeLock;

    private final long timeout;

    public SwapStreamProcessor(long timeout) {
        final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        this.readLock = readWriteLock.readLock();
        this.writeLock = readWriteLock.writeLock();
        this.timeout = timeout;
    }

    protected abstract InputStream createInputStream() throws IOException;

    protected abstract OutputStream createOutputStream() throws IOException;

    protected abstract void swap(OutputStream output) throws IOException;

    @Override
    public <T> T read(ReadFunction<? extends T> function) throws IOException {
        InputStream input = null;
        try {
            input = acquireReadLockThenCreateInputStream();
            return function.read(input);
        }
        finally {
            closeQuietlyThenReleaseReadLock(input);
        }
    }

    @Override
    public <T> T write(WriteFunction<? extends T> function) throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = acquireWriteLockThenCreateInputStream();
            output = createOutputStream();
            return function.write(input, output);
        }
        finally {
            closeQuietlyThenSwapThenReleaseWriteLock(input, output);
        }
    }

    private InputStream acquireReadLockThenCreateInputStream() throws IOException {
        try {
            readLock.tryLock(timeout, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            throw new IOException("can't acquire read lock", e);
        }

        return createInputStream();
    }

    private InputStream acquireWriteLockThenCreateInputStream() throws IOException {
        try {
            writeLock.tryLock(timeout, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            throw new IOException("can't acquire write lock", e);
        }

        return createInputStream();
    }

    private void closeQuietlyThenReleaseReadLock(InputStream input) {
        closeQuietly(input);
        readLock.unlock();
    }

    private void closeQuietlyThenSwapThenReleaseWriteLock(InputStream input, OutputStream output) throws IOException {
        closeQuietly(input);
        closeQuietly(output);

        try {
            swap(output);
        }
        finally {
            writeLock.unlock();
        }
    }
}
