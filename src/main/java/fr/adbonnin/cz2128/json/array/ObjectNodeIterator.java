package fr.adbonnin.cz2128.json.array;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.adbonnin.cz2128.JsonException;
import fr.adbonnin.cz2128.collect.CloseableIterator;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class ObjectNodeIterator implements CloseableIterator<ObjectNode> {

    private final ArrayIterator iterator;

    private final ObjectMapper mapper;

    public ObjectNodeIterator(JsonParser parser, ObjectMapper mapper) {
        this.iterator = new ArrayIterator(parser);
        this.mapper = requireNonNull(mapper);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public ObjectNode next() {
        try {
            return mapper.readTree(iterator.next());
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
