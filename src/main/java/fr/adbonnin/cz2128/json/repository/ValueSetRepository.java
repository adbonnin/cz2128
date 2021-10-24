package fr.adbonnin.cz2128.json.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.adbonnin.cz2128.json.JsonProvider;
import fr.adbonnin.cz2128.json.iterator.ValueArrayIterator;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

public class ValueSetRepository<T> extends SetRepository<T> {

    private final ObjectMapper mapper;

    public ValueSetRepository(Class<T> type, JsonProvider provider, ObjectMapper mapper) {
        this(mapper.readerFor(type), provider, mapper);
    }

    public ValueSetRepository(TypeReference<T> type, JsonProvider provider, ObjectMapper mapper) {
        this(mapper.readerFor(type), provider, mapper);
    }

    public ValueSetRepository(ObjectReader reader, JsonProvider provider, ObjectMapper mapper) {
        super(reader, provider);
        this.mapper = requireNonNull(mapper);
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    @Override
    public <U> ValueSetRepository<U> of(Class<U> type) {
        return new ValueSetRepository<>(type, getProvider(), mapper);
    }

    @Override
    public <U> ValueSetRepository<U> of(TypeReference<U> type) {
        return new ValueSetRepository<>(type, getProvider(), mapper);
    }

    @Override
    public <U> ValueSetRepository<U> of(ObjectReader reader) {
        return new ValueSetRepository<>(reader, getProvider(), mapper);
    }

    @Override
    protected long saveAll(Iterator<? extends T> elements, JsonParser parser, JsonGenerator generator) throws IOException {
        long updates = 0;
        generator.writeStartArray();

        final Map<T, T> newElements = StreamSupport.stream(Spliterators.spliteratorUnknownSize(elements, Spliterator.ORDERED), false)
            .collect(LinkedHashMap::new, (map, item) -> map.put(item, item), Map::putAll);

        // Update old elements
        final ValueArrayIterator<T> itr = new ValueArrayIterator<>(parser, getReader());
        while (itr.hasNext()) {
            final T oldElement = itr.next();

            if (!newElements.containsKey(oldElement)) {
                mapper.writeValue(generator, oldElement);
                continue;
            }

            final T newElement = newElements.remove(oldElement);
            mapper.writeValue(generator, newElement);
            ++updates;
        }

        // Create new elements
        for (T newElement : newElements.values()) {
            mapper.writeValue(generator, newElement);
            ++updates;
        }

        generator.writeEndArray();
        return updates;
    }

    @Override
    protected long deleteAll(Predicate<? super T> predicate, JsonParser parser, JsonGenerator generator) throws IOException {
        long deleted = 0;
        generator.writeStartArray();

        final ValueArrayIterator<T> itr = new ValueArrayIterator<>(parser, getReader());
        while (itr.hasNext()) {
            final T element = itr.next();

            if (predicate.test(element)) {
                ++deleted;
            }
            else {
                mapper.writeValue(generator, element);
            }
        }

        generator.writeEndArray();
        return deleted;
    }
}
