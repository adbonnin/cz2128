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

    private final long lockTimeout;

    public SwapStreamProcessor(long lockTimeout) {
        final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        this.readLock = readWriteLock.readLock();
        this.writeLock = readWriteLock.writeLock();
        this.lockTimeout = lockTimeout;
    }

    protected abstract InputStream createInputStream() throws IOException;

    protected abstract OutputStream createToBeSwappedOutputStream() throws IOException;

    protected abstract void swap(OutputStream tempOutput) throws IOException;

    @Override
    public <T> T read(ReadFunction<? extends T> function) throws IOException {
        InputStream input = null;
        try {
            input = tryAcquireReadLockThenCreateInputStream();
            return function.read(input);
        }
        finally {
            closeQuietlyThenReleaseReadLock(input);
        }
    }

    @Override
    public <T> T write(WriteFunction<? extends T> function) throws IOException {
        boolean swap = false;
        InputStream input = null;
        OutputStream output = null;
        try {
            input = tryAcquireWriteLockThenCreateInputStream();
            output = createToBeSwappedOutputStream();
            final T result = function.write(input, output);
            swap = true;
            return result;
        }
        finally {
            closeQuietlyThenTrySwapFinallyReleaseWriteLock(input, output, swap);
        }
    }

    private InputStream tryAcquireReadLockThenCreateInputStream() throws IOException {
        try {
            readLock.tryLock(lockTimeout, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            throw new IOException("can't acquire read lock", e);
        }

        return createInputStream();
    }

    private InputStream tryAcquireWriteLockThenCreateInputStream() throws IOException {
        try {
            writeLock.tryLock(lockTimeout, TimeUnit.MILLISECONDS);
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

    private void closeQuietlyThenTrySwapFinallyReleaseWriteLock(InputStream input, OutputStream output, boolean swap) throws IOException {
        closeQuietly(input);
        closeQuietly(output);

        try {
            if (swap) {
                swap(output);
            }
        }
        finally {
            writeLock.unlock();
        }
    }
}
