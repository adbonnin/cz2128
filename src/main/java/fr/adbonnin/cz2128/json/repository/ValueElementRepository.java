package fr.adbonnin.cz2128.json.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.adbonnin.cz2128.json.JsonProvider;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class ValueElementRepository<T> extends ElementRepository<T> {

    private final JsonProvider provider;

    private final ObjectMapper mapper;

    public ValueElementRepository(Class<T> type, JsonProvider provider, ObjectMapper mapper) {
        this(mapper.readerFor(type), provider, mapper);
    }

    public ValueElementRepository(TypeReference<T> type, JsonProvider provider, ObjectMapper mapper) {
        this(mapper.readerFor(type), provider, mapper);
    }

    public ValueElementRepository(ObjectReader reader, JsonProvider provider, ObjectMapper mapper) {
        super(reader, provider);
        this.provider = requireNonNull(provider);
        this.mapper = requireNonNull(mapper);
    }

    @Override
    public <U> ValueElementRepository<U> of(Class<U> type) {
        return new ValueElementRepository<>(type, provider, mapper);
    }

    @Override
    public <U> ValueElementRepository<U> of(TypeReference<U> type) {
        return new ValueElementRepository<>(type, provider, mapper);
    }

    @Override
    public <U> ValueElementRepository<U> of(ObjectReader reader) {
        return new ValueElementRepository<>(reader, provider, mapper);
    }

    @Override
    protected boolean save(T element, JsonParser parser, JsonGenerator generator) throws IOException {
        mapper.writeValue(generator, element);
        return true;
    }
}
