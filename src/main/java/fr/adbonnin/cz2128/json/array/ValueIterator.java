package fr.adbonnin.cz2128.json.array;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.adbonnin.cz2128.JsonException;
import fr.adbonnin.cz2128.collect.CloseableIterator;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class ValueIterator<E> implements CloseableIterator<E> {

    private final ObjectNodeIterator iterator;

    private final ObjectReader reader;

    public ValueIterator(JsonParser parser, ObjectReader reader, ObjectMapper mapper) {
        this.reader = requireNonNull(reader);
        this.iterator = new ObjectNodeIterator(parser, mapper);
    }

    public ValueIterator(JsonParser parser, Class<E> type, ObjectMapper mapper) {
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
