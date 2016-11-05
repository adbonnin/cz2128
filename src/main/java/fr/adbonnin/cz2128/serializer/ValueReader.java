package fr.adbonnin.cz2128.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class ValueReader<T> {

    private final ObjectReader reader;

    public ValueReader(ObjectReader reader) {
        this.reader = requireNonNull(reader);
    }

    public T read(ObjectNode node) throws IOException {
        return reader.readValue(node);
    }

    public static <T> ValueReader<T> readerFor(ObjectMapper mapper, Class<T> typeOfT) {
        return new ValueReader<>(mapper.readerFor(typeOfT));
    }
}
