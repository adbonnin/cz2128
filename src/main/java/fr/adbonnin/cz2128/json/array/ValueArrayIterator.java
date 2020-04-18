package fr.adbonnin.cz2128.json.array;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.adbonnin.cz2128.JsonException;
import fr.adbonnin.cz2128.collect.CloseableIterator;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class ValueArrayIterator<E> implements CloseableIterator<E> {

    private final JsonNodeArrayIterator iterator;

    private final ObjectReader reader;

    public ValueArrayIterator(JsonParser parser, ObjectReader reader, ObjectMapper mapper) {
        this.reader = requireNonNull(reader);
        this.iterator = new JsonNodeArrayIterator(parser, mapper);
    }

    public ValueArrayIterator(JsonParser parser, Class<E> type, ObjectMapper mapper) {
        this(parser, mapper.readerFor(type), mapper);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public E next() {
        try {
            return reader.readValue(iterator.next());
        }
        catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public void remove() {
        iterator.remove();
    }

    @Override
    public void close() {
        iterator.close();
    }
}