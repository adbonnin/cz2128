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

    private final ObjectMapper mapper;

    public ValueElementRepository(JsonProvider provider, ObjectMapper mapper, Class<T> type) {
        this(provider, mapper, mapper.readerFor(type));
    }

    public ValueElementRepository(JsonProvider provider, ObjectMapper mapper, TypeReference<T> type) {
        this(provider, mapper, mapper.readerFor(type));
    }

    public ValueElementRepository(JsonProvider provider, ObjectMapper mapper, ObjectReader reader) {
        super(provider, reader);
        this.mapper = requireNonNull(mapper);
    }

    @Override
    public <U> ValueElementRepository<U> of(Class<U> type) {
        return new ValueElementRepository<>(getProvider(), mapper, type);
    }

    @Override
    public <U> ValueElementRepository<U> of(TypeReference<U> type) {
        return new ValueElementRepository<>(getProvider(), mapper, type);
    }

    @Override
    public <U> ValueElementRepository<U> of(ObjectReader reader) {
        return new ValueElementRepository<>(getProvider(), mapper, reader);
    }

    @Override
    protected boolean save(JsonParser parser, JsonGenerator generator, T element) throws IOException {
        mapper.writeValue(generator, element);
        return true;
    }
}
