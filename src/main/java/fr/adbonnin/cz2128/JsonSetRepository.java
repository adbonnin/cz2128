package fr.adbonnin.cz2128;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.adbonnin.cz2128.collect.IteratorUtils;
import fr.adbonnin.cz2128.json.iterator.JsonNodeArrayIterator;
import fr.adbonnin.cz2128.json.iterator.SkipChildrenArrayIterator;
import fr.adbonnin.cz2128.json.iterator.ValueArrayIterator;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

public class JsonSetRepository<T> implements JsonProvider {

    private final ObjectReader objectReader;

    private final ObjectMapper objectMapper;

    private final JsonProvider provider;

    private final JsonUpdateStrategy updateStrategy;

    public JsonSetRepository(Class<T> type, ObjectMapper objectMapper, JsonProvider provider, JsonUpdateStrategy updateStrategy) {
        this(objectMapper.readerFor(type), objectMapper, provider, updateStrategy);
    }

    public JsonSetRepository(TypeReference<T> type, ObjectMapper objectMapper, JsonProvider provider, JsonUpdateStrategy updateStrategy) {
        this(objectMapper.readerFor(type), objectMapper, provider, updateStrategy);
    }

    public JsonSetRepository(ObjectReader objectReader, ObjectMapper objectMapper, JsonProvider provider, JsonUpdateStrategy updateStrategy) {
        this.objectReader = requireNonNull(objectReader);
        this.objectMapper = requireNonNull(objectMapper);
        this.provider = requireNonNull(provider);
        this.updateStrategy = requireNonNull(updateStrategy);
    }

    public ObjectReader getObjectReader() {
        return objectReader;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public JsonProvider getProvider() {
        return provider;
    }

    public JsonUpdateStrategy getUpdateStrategy() {
        return updateStrategy;
    }

    public <U> JsonSetRepository<U> of(ObjectReader objectReader) {
        return new JsonSetRepository<>(objectReader, objectMapper, provider, updateStrategy);
    }

    public <U> JsonSetRepository<U> of(Class<U> type) {
        return new JsonSetRepository<>(type, objectMapper, provider, updateStrategy);
    }

    public <U> JsonSetRepository<U> of(TypeReference<U> type) {
        return new JsonSetRepository<>(type, objectMapper, provider, updateStrategy);
    }

    public boolean isEmpty() {
        return count() == 0;
    }

    public long count() {
        return withParser(parser -> IteratorUtils.count(new SkipChildrenArrayIterator(parser)));
    }

    public long count(Predicate<? super T> predicate) {
        return withIterator(iterator -> IteratorUtils.count(IteratorUtils.filter(iterator, predicate)));
    }

    public Optional<T> findFirst(Predicate<? super T> predicate) {
        return withIterator(iterator -> IteratorUtils.find(iterator, predicate));
    }

    public Set<T> findAll() {
        return findAll(x -> true);
    }

    public Set<T> findAll(Predicate<? super T> predicate) {
        return withIterator(iterator -> {
            final Iterator<T> itr = IteratorUtils.filter(iterator, predicate);

            final Set<T> result = new LinkedHashSet<>();
            itr.forEachRemaining(result::add);
            return result;
        });
    }

    public <R> R withStream(Function<Stream<? extends T>, ? extends R> function) {
        return withIterator((iterator) -> {
            final Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
            final Stream<T> stream = StreamSupport.stream(spliterator, false);
            return function.apply(stream);
        });
    }

    public <R> R withIterator(Function<Iterator<? extends T>, ? extends R> function) {
        return withParser(parser -> function.apply(new ValueArrayIterator<>(parser, objectReader)));
    }

    @Override
    public <R> R withParser(Function<JsonParser, ? extends R> function) {
        return provider.withParser(function);
    }

    @Override
    public <R> R withGenerator(BiFunction<JsonParser, JsonGenerator, ? extends R> function) {
        return provider.withGenerator(function);
    }

    public boolean save(T element) {
        return saveAll(Collections.singleton(element)) != 0;
    }

    public long saveAll(T... elements) {
        return saveAll(Arrays.asList(elements));
    }

    public long saveAll(Iterable<? extends T> elements) {
        return withGenerator((parser, generator) -> {
            final Map<T, T> newElements = StreamSupport.stream(elements.spliterator(), false)
                    .collect(LinkedHashMap::new, (map, item) -> map.put(item, item), Map::putAll);

            try {
                long updates = 0;
                generator.writeStartArray();

                // Update old elements
                final JsonNodeArrayIterator itr = new JsonNodeArrayIterator(parser, objectMapper);
                while (itr.hasNext()) {
                    final JsonNode oldNode = itr.next();
                    final T oldElement = objectReader.readValue(oldNode);

                    if (!newElements.containsKey(oldElement)) {
                        generator.writeTree(oldNode);
                        continue;
                    }

                    final T newElement = newElements.remove(oldElement);
                    final JsonNode newNode = objectMapper.valueToTree(newElement);

                    final boolean updated = updateStrategy.update(oldNode, newNode, generator);
                    if (updated) {
                        ++updates;
                    }
                }

                // Create new elements
                for (T newElement : newElements.values()) {
                    final JsonNode newNode = objectMapper.valueToTree(newElement);
                    generator.writeTree(newNode);
                    ++updates;
                }

                generator.writeEndArray();
                return updates;
            }
            catch (IOException e) {
                throw new JsonException(e);
            }
        });
    }

    public boolean delete(T element) {
        return deleteAll(element::equals) > 0;
    }

    public long deleteAll(Collection<? extends T> elements) {
        return deleteAll(elements::contains);
    }

    public long deleteAll() {
        return deleteAll(value -> true);
    }

    public long deleteAll(Predicate<? super T> predicate) {
        return withGenerator((parser, generator) -> {
            try {
                long deleted = 0;
                generator.writeStartArray();

                final JsonNodeArrayIterator itr = new JsonNodeArrayIterator(parser, objectMapper);
                while (itr.hasNext()) {
                    final JsonNode node = itr.next();
                    final T element = objectReader.readValue(node);

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
            catch (IOException e) {
                throw new JsonException(e);
            }
        });
    }
}
