package fr.adbonnin.cz2128.json.provider;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import fr.adbonnin.cz2128.json.JsonException;
import fr.adbonnin.cz2128.json.JsonProvider;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class ConcurrentJsonProvider implements JsonProvider {

    private final ReentrantReadWriteLock.ReadLock readLock;

    private final ReentrantReadWriteLock.WriteLock writeLock;

    private final JsonProvider provider;

    private final long lockTimeout;

    public ConcurrentJsonProvider(JsonProvider provider, long lockTimeout) {
        this.provider = requireNonNull(provider);
        this.lockTimeout = lockTimeout;

        final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }

    @Override
    public <R> R withParser(Function<JsonParser, ? extends R> function) {
        try {
            if (readLock.tryLock(lockTimeout, TimeUnit.MILLISECONDS)) {
                try {
                    return provider.withParser(function);
                }
                finally {
                    readLock.unlock();
                }
            }
            else {
                throw new JsonException("Reader lock can't be acquired.");
            }
        }
        catch (InterruptedException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public <R> R withGenerator(BiFunction<JsonParser, JsonGenerator, ? extends R> function) {
        try {
            if (writeLock.tryLock(lockTimeout, TimeUnit.MILLISECONDS)) {
                try {
                    return provider.withGenerator(function);
                }
                finally {
                    writeLock.unlock();
                }
            }
            else {
                throw new JsonException("Write lock can't be acquired.");
            }
        }
        catch (InterruptedException e) {
            throw new JsonException(e);
        }
    }
}
