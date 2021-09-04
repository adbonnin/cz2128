package fr.adbonnin.cz2128.json.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.adbonnin.cz2128.base.Pair;
import fr.adbonnin.cz2128.json.JsonProvider;
import fr.adbonnin.cz2128.json.JsonUtils;
import fr.adbonnin.cz2128.json.iterator.JsonNodeObjectIterator;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public class NodeMapRepository<T> extends MapRepository<T> {

    private final ObjectMapper mapper;

    public NodeMapRepository(Class<T> type, JsonProvider provider, ObjectMapper mapper) {
        this(mapper.readerFor(type), provider, mapper);
    }

    public NodeMapRepository(TypeReference<T> type, JsonProvider provider, ObjectMapper mapper) {
        this(mapper.readerFor(type), provider, mapper);
    }

    public NodeMapRepository(ObjectReader reader, JsonProvider provider, ObjectMapper mapper) {
        super(reader, provider);
        this.mapper = requireNonNull(mapper);
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    @Override
    public <U> NodeMapRepository<U> of(Class<U> type) {
        return new NodeMapRepository<>(type, getProvider(), mapper);
    }

    @Override
    public <U> NodeMapRepository<U> of(TypeReference<U> type) {
        return new NodeMapRepository<>(type, getProvider(), mapper);
    }

    @Override
    public <U> NodeMapRepository<U> of(ObjectReader reader) {
        return new NodeMapRepository<>(reader, getProvider(), mapper);
    }

    @Override
    protected long saveAll(Map<String, ? extends T> elements, JsonParser parser, JsonGenerator generator) throws IOException {
        final Map<String, T> newElements = new LinkedHashMap<>(elements);

        long updates = 0;
        generator.writeStartObject();

        // Update old elements
        final JsonNodeObjectIterator itr = new JsonNodeObjectIterator(parser, mapper);
        while (itr.hasNext()) {
            final Map.Entry<String, JsonNode> old = itr.next();
            final String key = old.getKey();
            final JsonNode oldNode = old.getValue();

            generator.writeFieldName(key);

            if (!newElements.containsKey(key)) {
                generator.writeTree(oldNode);
                continue;
            }

            final T newElement = newElements.remove(key);
            final JsonNode newNode = mapper.valueToTree(newElement);

            final boolean updated = JsonUtils.partialUpdate(oldNode, newNode, generator);
            if (updated) {
                ++updates;
            }
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

        final JsonNodeObjectIterator itr = new JsonNodeObjectIterator(parser, mapper);
        while (itr.hasNext()) {
            final Map.Entry<String, JsonNode> etr = itr.next();
            final String key = etr.getKey();
            final JsonNode node = etr.getValue();
            final T element = getReader().readValue(node);

            if (predicate.test(Pair.of(key, element))) {
                ++deleted;
            }
            else {
                generator.writeFieldName(key);
                generator.writeTree(node);
            }
        }

        generator.writeEndObject();
        return deleted;
    }
}
