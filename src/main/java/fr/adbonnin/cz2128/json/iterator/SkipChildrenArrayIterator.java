package fr.adbonnin.cz2128.json.iterator;

import com.fasterxml.jackson.core.JsonParser;
import fr.adbonnin.cz2128.json.JsonException;

import java.io.IOException;
import java.util.Iterator;

public class SkipChildrenArrayIterator implements Iterator<Void> {

    private final ArrayIterator iterator;

    public SkipChildrenArrayIterator(JsonParser parser) {
        this.iterator = new ArrayIterator(parser);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Void next() {
        try {
            iterator.next().skipChildren();
            return null;
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
