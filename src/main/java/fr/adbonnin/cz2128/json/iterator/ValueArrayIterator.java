package fr.adbonnin.cz2128.json.iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.adbonnin.cz2128.json.JsonException;

import java.io.IOException;
import java.util.Iterator;

import static java.util.Objects.requireNonNull;

public class ValueArrayIterator<E> implements Iterator<E> {

    private final ArrayIterator iterator;

    private final ObjectReader reader;

    public ValueArrayIterator(JsonParser parser, ObjectReader reader) {
        this.reader = requireNonNull(reader);
        this.iterator = new ArrayIterator(parser);
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
}
