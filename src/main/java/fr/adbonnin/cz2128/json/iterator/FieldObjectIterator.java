package fr.adbonnin.cz2128.json.iterator;

import com.fasterxml.jackson.core.JsonParser;
import fr.adbonnin.cz2128.json.JsonException;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class FieldObjectIterator implements Iterator<String> {

    private final ObjectIterator iterator;

    public FieldObjectIterator(JsonParser parser) {
        this.iterator = new ObjectIterator(parser);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public String next() {
        final Map.Entry<String, JsonParser> next = iterator.next();

        try {
            next.getValue().skipChildren();
        }
        catch (IOException e) {
            throw new JsonException(e);
        }

        return next.getKey();
    }

    @Override
    public void remove() {
        iterator.remove();
    }
}
