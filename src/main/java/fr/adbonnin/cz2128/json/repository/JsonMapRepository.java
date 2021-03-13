package fr.adbonnin.cz2128.json.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.adbonnin.cz2128.base.Pair;
import fr.adbonnin.cz2128.collect.IteratorUtils;
import fr.adbonnin.cz2128.json.JsonException;
import fr.adbonnin.cz2128.json.JsonProvider;
import fr.adbonnin.cz2128.json.JsonUpdateStrategy;
import fr.adbonnin.cz2128.json.iterator.JsonNodeObjectIterator;
import fr.adbonnin.cz2128.json.iterator.SkipChildrenObjectIterator;
import fr.adbonnin.cz2128.json.iterator.ValueObjectIterator;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

public class JsonMapRepository<T> implements JsonProvider {

    private final ObjectReader reader;

    private final ObjectMapper mapper;

    private final JsonProvider provider;

    private final JsonUpdateStrategy updateStrategy;

    public JsonMapRepository(Class<T> type, ObjectMapper mapper, JsonProvider provider, JsonUpdateStrategy updateStrategy) {
        this(mapper.readerFor(type), mapper, provider, updateStrategy);
    }

    public JsonMapRepository(TypeReference<T> type, ObjectMapper mapper, JsonProvider provider, JsonUpdateStrategy updateStrategy) {
        this(mapper.readerFor(type), mapper, provider, updateStrategy);
    }

    public JsonMapRepository(ObjectReader reader, ObjectMapper mapper, JsonProvider provider, JsonUpdateStrategy updateStrategy) {
        this.reader = requireNonNull(reader);
        this.mapper = requireNonNull(mapper);
        this.provider = requireNonNull(provider);
        this.updateStrategy = requireNonNull(updateStrategy);
    }

    public ObjectReader getReader() {
        return reader;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public JsonProvider getProvider() {
        return provider;
    }

    public JsonUpdateStrategy getUpdateStrategy() {
        return updateStrategy;
    }

    public <U> JsonMapRepository<U> of(ObjectReader reader) {
        return new JsonMapRepository<>(reader, mapper, provider, updateStrategy);
    }

    public <U> JsonMapRepository<U> of(Class<U> type) {
        return new JsonMapRepository<>(type, mapper, provider, updateStrategy);
    }

    public <U> JsonMapRepository<U> of(TypeReference<U> type) {
        return new JsonMapRepository<>(type, mapper, provider, updateStrategy);
    }

    public boolean isEmpty() {
        return count() == 0;
    }

    public long count() {
        return withParser(parser -> IteratorUtils.count(new SkipChildrenObjectIterator(parser)));
    }

    public long count(Predicate<? super Map.Entry<String, T>> predicate) {
        return withIterator(iterator -> IteratorUtils.count(IteratorUtils.filter(iterator, predicate)));
    }

    public Optional<Map.Entry<String, T>> findFirst(Predicate<? super Map.Entry<String, T>> predicate) {
        return withIterator(iterator -> IteratorUtils.find(iterator, predicate));
    }

    public Map<String, T> findAll() {
        return findAll(x -> true);
    }

    public Map<String, T> findAll(Predicate<? super Map.Entry<String, T>> predicate) {
        return withIterator(iterator -> {
            final Iterator<Map.Entry<String, T>> itr = IteratorUtils.filter(iterator, predicate);

            final Map<String, T> result = new LinkedHashMap<>();
            itr.forEachRemaining(e -> result.put(e.getKey(), e.getValue()));
            return result;
        });
    }

    public <R> R withStream(Function<Stream<? extends Map.Entry<String, T>>, ? extends R> function) {
        return withIterator((iterator) -> {
            final Spliterator<Map.Entry<String, T>> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
            final Stream<Map.Entry<String, T>> stream = StreamSupport.stream(spliterator, false);
            return function.apply(stream);
        });
    }

    public <R> R withIterator(Function<Iterator<? extends Map.Entry<String, T>>, ? extends R> function) {
        return withParser(parser -> function.apply(new ValueObjectIterator<>(parser, reader)));
    }

    @Override
    public <R> R withParser(Function<JsonParser, ? extends R> function) {
        return provider.withParser(function);
    }

    @Override
    public <R> R withGenerator(BiFunction<JsonParser, JsonGenerator, ? extends R> function) {
        return provider.withGenerator(function);
    }

    public boolean save(String key, T element) {
        return saveAll(Collections.singletonMap(key, element)) != 0;
    }

    public long saveAll(Map<String, ? extends T> elements) {
        return withGenerator((parser, generator) -> {
            final Map<String, T> newElements = new LinkedHashMap<>(elements);

            try {
                long updates = 0;
                generator.writeStartObject();

                // Update old elements
                final JsonNodeObjectIterator itr = new JsonNodeObjectIterator(parser, mapper);
                while (itr.hasNext()) {
                    final Map.Entry<String, JsonNode> oldValue = itr.next();
                    final String key = oldValue.getKey();
                    final JsonNode oldNode = oldValue.getValue();

                    generator.writeFieldName(key);

                    if (!newElements.containsKey(key)) {
                        generator.writeTree(oldValue.getValue());
                        continue;
                    }

                    final T newElement = newElements.remove(key);
                    final JsonNode newNode = mapper.valueToTree(newElement);

                    final boolean updated = updateStrategy.update(oldNode, newNode, generator);
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
            catch (IOException e) {
                throw new JsonException(e);
            }
        });
    }

    public boolean delete(String key) {
        return deleteAll(e -> key.equals(e.getKey())) > 0;
    }

    public long deleteAll(Collection<String> elements) {
        return deleteAll(e -> elements.contains(e.getKey()));
    }

    public long deleteAll() {
        return deleteAll(value -> true);
    }

    public long deleteAll(Predicate<? super Map.Entry<String, ? extends T>> predicate) {
        return withGenerator((parser, generator) -> {
            try {
                long deleted = 0;
                generator.writeStartObject();

                final JsonNodeObjectIterator itr = new JsonNodeObjectIterator(parser, mapper);
                while (itr.hasNext()) {
                    final Map.Entry<String, JsonNode> value = itr.next();
                    final String key = value.getKey();
                    final JsonNode node = value.getValue();
                    final T element = reader.readValue(node);

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
            catch (IOException e) {
                throw new JsonException(e);
            }
        });
    }
}
