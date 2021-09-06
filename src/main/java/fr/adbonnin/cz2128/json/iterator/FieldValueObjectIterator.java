package fr.adbonnin.cz2128.json.iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.adbonnin.cz2128.base.Pair;
import fr.adbonnin.cz2128.json.JsonException;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class FieldValueObjectIterator<E> implements Iterator<Map.Entry<String, E>> {

    private final ObjectIterator iterator;

    private final ObjectReader reader;

    public FieldValueObjectIterator(JsonParser parser, ObjectReader reader) {
        this.reader = requireNonNull(reader);
        this.iterator = new ObjectIterator(parser);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Map.Entry<String, E> next() {
        final Map.Entry<String, JsonParser> next = iterator.next();

        final E value;
        try {
            value = reader.readValue(next.getValue());
        }
        catch (IOException e) {
            throw new JsonException(e);
        }

        return Pair.of(next.getKey(), value);
    }

    @Override
    public void remove() {
        iterator.remove();
    }
}
