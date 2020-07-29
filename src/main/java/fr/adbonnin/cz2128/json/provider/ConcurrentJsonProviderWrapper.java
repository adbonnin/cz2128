package fr.adbonnin.cz2128.json.provider;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.adbonnin.cz2128.JsonException;
import fr.adbonnin.cz2128.JsonProvider;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class ConcurrentJsonProviderWrapper implements JsonProvider {

    private final ReentrantReadWriteLock.ReadLock readLock;

    private final ReentrantReadWriteLock.WriteLock writeLock;

    private final JsonProvider jsonProvider;

    private final long lockTimeout;

    public ConcurrentJsonProviderWrapper(JsonProvider jsonProvider, long lockTimeout) {
        this.jsonProvider = requireNonNull(jsonProvider);
        this.lockTimeout = lockTimeout;

        final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }

    @Override
    public <T> T withParser(ObjectMapper mapper, Function<JsonParser, ? extends T> function) {
        try {
            if (readLock.tryLock(lockTimeout, TimeUnit.MILLISECONDS)) {
                try {
                    return jsonProvider.withParser(mapper, function);
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
    public <T> T withGenerator(ObjectMapper mapper, BiFunction<JsonParser, JsonGenerator, ? extends T> function) {
        try {
            if (writeLock.tryLock(lockTimeout, TimeUnit.MILLISECONDS)) {
                try {
                    return jsonProvider.withGenerator(mapper, function);
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
