package fr.adbonnin.cz2128.json.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.adbonnin.cz2128.collect.IteratorUtils;
import fr.adbonnin.cz2128.json.JsonException;
import fr.adbonnin.cz2128.json.JsonProvider;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

import static com.fasterxml.jackson.core.JsonToken.VALUE_NULL;

public abstract class ElementRepository<T> extends BaseRepository<T> {

    private final ObjectReader reader;

    protected abstract boolean save(T element, JsonParser parser, JsonGenerator generator) throws IOException;

    public ElementRepository(ObjectReader reader, JsonProvider provider) {
        super(provider);
        this.reader = reader;
    }

    public ObjectReader getReader() {
        return reader;
    }

    @Override
    public boolean isEmpty() {
        return !value().isPresent();
    }

    public boolean isPresent() {
        return value().isPresent();
    }

    @Override
    public long count() {
        return value().isPresent() ? 1 : 0;
    }

    public Optional<T> value() {
        return withParser(parser -> {
            try {
                final JsonToken token = parser.hasCurrentToken()
                    ? parser.getCurrentToken()
                    : parser.nextToken();

                if (token == null || VALUE_NULL.equals(token)) {
                    return Optional.empty();
                }

                final T element = reader.readValue(parser);
                return Optional.ofNullable(element);
            }
            catch (IOException e) {
                throw new JsonException(e);
            }
        });
    }

    @Override
    public <R> R withIterator(Function<Iterator<? extends T>, ? extends R> function) {
        final Iterator<T> iterator = value()
            .map(IteratorUtils::singletonIterator)
            .orElse(Collections.emptyIterator());

        return function.apply(iterator);
    }

    public boolean save(T element) {
        return withGenerator((parser, generator) -> {
            try {
                return save(element, parser, generator);
            }
            catch (IOException e) {
                throw new JsonException(e);
            }
        });
    }

    public void delete() {
        save(null);
    }
}
