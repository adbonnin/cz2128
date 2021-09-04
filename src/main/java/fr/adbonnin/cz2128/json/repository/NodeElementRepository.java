package fr.adbonnin.cz2128.json.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.adbonnin.cz2128.json.JsonProvider;
import fr.adbonnin.cz2128.json.JsonUtils;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class NodeElementRepository<T> extends ElementRepository<T> {

    private final JsonProvider provider;

    private final ObjectMapper mapper;

    public NodeElementRepository(Class<T> type, JsonProvider provider, ObjectMapper mapper) {
        this(mapper.readerFor(type), provider, mapper);
    }

    public NodeElementRepository(TypeReference<T> type, JsonProvider provider, ObjectMapper mapper) {
        this(mapper.readerFor(type), provider, mapper);
    }

    public NodeElementRepository(ObjectReader reader, JsonProvider provider, ObjectMapper mapper) {
        super(reader, provider);
        this.provider = requireNonNull(provider);
        this.mapper = requireNonNull(mapper);
    }

    @Override
    public <U> NodeElementRepository<U> of(Class<U> type) {
        return new NodeElementRepository<>(type, provider, mapper);
    }

    @Override
    public <U> NodeElementRepository<U> of(TypeReference<U> type) {
        return new NodeElementRepository<>(type, provider, mapper);
    }

    @Override
    public <U> NodeElementRepository<U> of(ObjectReader reader) {
        return new NodeElementRepository<>(reader, provider, mapper);
    }

    @Override
    protected boolean save(T element, JsonParser parser, JsonGenerator generator) throws IOException {
        final JsonToken token = parser.hasCurrentToken()
            ? parser.getCurrentToken()
            : parser.nextToken();

        final JsonNode oldNode = token == null ? null : mapper.readTree(parser);
        final JsonNode newNode = mapper.valueToTree(element);
        return JsonUtils.partialUpdate(oldNode, newNode, generator);
    }
}
