package fr.adbonnin.cz2128.json.iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.adbonnin.cz2128.JsonException;
import fr.adbonnin.cz2128.base.Pair;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class ValueObjectIterator<E> implements Iterator<Map.Entry<String, E>> {

    private final ObjectIterator iterator;

    private final ObjectReader reader;

    public ValueObjectIterator(JsonParser parser, ObjectReader reader) {
        this.reader = requireNonNull(reader);
        this.iterator = new ObjectIterator(parser);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Map.Entry<String, E> next() {
        try {
            final Map.Entry<String, JsonParser> next = iterator.next();
            final E value = reader.readValue(next.getValue());
            return Pair.of(next.getKey(), value);
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
