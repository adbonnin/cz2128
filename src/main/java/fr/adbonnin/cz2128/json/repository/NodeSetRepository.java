package fr.adbonnin.cz2128.json.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.adbonnin.cz2128.json.JsonProvider;
import fr.adbonnin.cz2128.json.JsonUtils;
import fr.adbonnin.cz2128.json.iterator.JsonNodeArrayIterator;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

public class NodeSetRepository<T> extends SetRepository<T> {

    private final ObjectReader reader;

    private final JsonProvider provider;

    private final ObjectMapper mapper;

    public NodeSetRepository(Class<T> type, JsonProvider provider, ObjectMapper mapper) {
        this(mapper.readerFor(type), provider, mapper);
    }

    public NodeSetRepository(TypeReference<T> type, JsonProvider provider, ObjectMapper mapper) {
        this(mapper.readerFor(type), provider, mapper);
    }

    public NodeSetRepository(ObjectReader reader, JsonProvider provider, ObjectMapper mapper) {
        super(reader, provider);
        this.reader = requireNonNull(reader);
        this.provider = requireNonNull(provider);
        this.mapper = requireNonNull(mapper);
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    @Override
    public <U> NodeSetRepository<U> of(Class<U> type) {
        return new NodeSetRepository<>(type, provider, mapper);
    }

    @Override
    public <U> NodeSetRepository<U> of(TypeReference<U> type) {
        return new NodeSetRepository<>(type, provider, mapper);
    }

    @Override
    public <U> NodeSetRepository<U> of(ObjectReader reader) {
        return new NodeSetRepository<>(reader, provider, mapper);
    }

    @Override
    protected long saveAll(Iterable<? extends T> elements, JsonParser parser, JsonGenerator generator) throws IOException {
        final Map<T, T> newElements = StreamSupport.stream(elements.spliterator(), false)
            .collect(LinkedHashMap::new, (map, item) -> map.put(item, item), Map::putAll);

        long updates = 0;
        generator.writeStartArray();

        // Update old elements
        final JsonNodeArrayIterator itr = new JsonNodeArrayIterator(parser, mapper);
        while (itr.hasNext()) {
            final JsonNode oldNode = itr.next();
            final T oldElement = reader.readValue(oldNode);

            if (!newElements.containsKey(oldElement)) {
                generator.writeTree(oldNode);
                continue;
            }

            final T newElement = newElements.remove(oldElement);
            final JsonNode newNode = mapper.valueToTree(newElement);

            final boolean updated = JsonUtils.partialUpdate(oldNode, newNode, generator);
            if (updated) {
                ++updates;
            }
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

        final JsonNodeArrayIterator itr = new JsonNodeArrayIterator(parser, mapper);
        while (itr.hasNext()) {
            final JsonNode node = itr.next();
            final T element = reader.readValue(node);

            if (predicate.test(element)) {
                ++deleted;
            }
            else {
                generator.writeTree(node);
            }
        }

        generator.writeEndArray();
        return deleted;
    }
}
