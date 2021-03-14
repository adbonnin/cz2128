package fr.adbonnin.cz2128.json.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.adbonnin.cz2128.json.JsonProvider;
import fr.adbonnin.cz2128.json.iterator.ValueObjectIterator;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public class ValueMapRepository<T> extends MapRepository<T> {

    private final ObjectReader reader;

    private final JsonProvider provider;

    private final ObjectMapper mapper;

    public ValueMapRepository(Class<T> type, JsonProvider provider, ObjectMapper mapper) {
        this(mapper.readerFor(type), provider, mapper);
    }

    public ValueMapRepository(TypeReference<T> type, JsonProvider provider, ObjectMapper mapper) {
        this(mapper.readerFor(type), provider, mapper);
    }

    public ValueMapRepository(ObjectReader reader, JsonProvider provider, ObjectMapper mapper) {
        super(reader, provider);
        this.reader = requireNonNull(reader);
        this.provider = requireNonNull(provider);
        this.mapper = requireNonNull(mapper);
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    @Override
    public <U> ValueMapRepository<U> of(Class<U> type) {
        return new ValueMapRepository<>(type, provider, mapper);
    }

    @Override
    public <U> ValueMapRepository<U> of(TypeReference<U> type) {
        return new ValueMapRepository<>(type, provider, mapper);
    }

    @Override
    public <U> ValueMapRepository<U> of(ObjectReader reader) {
        return new ValueMapRepository<>(reader, provider, mapper);
    }

    @Override
    protected long saveAll(Map<String, ? extends T> elements, JsonParser parser, JsonGenerator generator) throws IOException {
        final Map<String, T> newElements = new LinkedHashMap<>(elements);

        long updates = 0;
        generator.writeStartObject();

        // Update old elements
        final ValueObjectIterator<T> itr = new ValueObjectIterator<>(parser, reader);
        while (itr.hasNext()) {
            final Map.Entry<String, T> old = itr.next();
            final String key = old.getKey();
            final T oldElement = old.getValue();

            generator.writeFieldName(key);

            if (!newElements.containsKey(key)) {
                mapper.writeValue(generator, oldElement);
                continue;
            }

            final T newElement = newElements.remove(key);
            mapper.writeValue(generator, newElement);
            ++updates;
        }

        // Create new elements
        for (Map.Entry<String, T> newElement : newElements.entrySet()) {
            generator.writeFieldName(newElement.getKey());
            mapper.writeValue(generator, newElement.getValue());
            ++updates;
        }

        generator.writeEndObject();
        return updates;
    }

    @Override
    protected long deleteAll(Predicate<? super Map.Entry<String, ? extends T>> predicate, JsonParser parser, JsonGenerator generator) throws IOException {
        long deleted = 0;
        generator.writeStartObject();

        final ValueObjectIterator<T> itr = new ValueObjectIterator<>(parser, reader);
        while (itr.hasNext()) {
            final Map.Entry<String, T> etr = itr.next();
            final String key = etr.getKey();
            final T element = etr.getValue();

            if (predicate.test(etr)) {
                ++deleted;
            }
            else {
                generator.writeFieldName(key);
                mapper.writeValue(generator, element);
            }
        }

        generator.writeEndObject();
        return deleted;
    }
}
