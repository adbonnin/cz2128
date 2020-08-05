package fr.adbonnin.cz2128.json.iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.adbonnin.cz2128.JsonException;

import java.io.IOException;
import java.util.Iterator;

import static java.util.Objects.requireNonNull;

public class JsonNodeArrayIterator implements Iterator<JsonNode> {

    private final ArrayIterator iterator;

    private final ObjectMapper mapper;

    public JsonNodeArrayIterator(JsonParser parser, ObjectMapper mapper) {
        this.iterator = new ArrayIterator(parser);
        this.mapper = requireNonNull(mapper);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public JsonNode next() {
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
}
