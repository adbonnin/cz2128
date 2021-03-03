package fr.adbonnin.cz2128.json.iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.adbonnin.cz2128.JsonException;
import fr.adbonnin.cz2128.base.Pair;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class JsonNodeObjectIterator implements Iterator<Map.Entry<String, JsonNode>> {

    private final ObjectIterator iterator;

    private final ObjectMapper mapper;

    public JsonNodeObjectIterator(JsonParser parser, ObjectMapper mapper) {
        this.iterator = new ObjectIterator(parser);
        this.mapper = requireNonNull(mapper);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Map.Entry<String, JsonNode> next() {
        try {
            final Map.Entry<String, JsonParser> next = iterator.next();
            final JsonNode node = mapper.readTree(next.getValue());
            return Pair.of(next.getKey(), node);
        }
        catch (IOException e) {
            throw new JsonException(e);
        }
    }
}
