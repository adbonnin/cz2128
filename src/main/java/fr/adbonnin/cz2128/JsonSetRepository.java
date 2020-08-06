package fr.adbonnin.cz2128;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.adbonnin.cz2128.collect.IteratorUtils;
import fr.adbonnin.cz2128.json.JsonUtils;
import fr.adbonnin.cz2128.json.iterator.JsonNodeArrayIterator;
import fr.adbonnin.cz2128.json.iterator.SkippedValueIterator;
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

    private final ObjectReader reader;

    private final ObjectMapper mapper;

    private final JsonProvider provider;

    public JsonSetRepository(ObjectReader reader, JsonProvider provider, ObjectMapper mapper) {
        this.reader = requireNonNull(reader);
        this.mapper = requireNonNull(mapper);
        this.provider = requireNonNull(provider);
    }

    public JsonSetRepository(Class<T> type, JsonProvider provider, ObjectMapper mapper) {
        this(mapper.readerFor(type), provider, mapper);
    }

    public JsonSetRepository(TypeReference<T> type, JsonProvider provider, ObjectMapper mapper) {
        this(mapper.readerFor(type), provider, mapper);
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

    public <U> JsonSetRepository<U> of(ObjectReader objectReader) {
        return new JsonSetRepository<>(objectReader, provider, mapper);
    }

    public <U> JsonSetRepository<U> of(Class<U> type) {
        return new JsonSetRepository<>(type, provider, mapper);
    }

    public <U> JsonSetRepository<U> of(TypeReference<U> type) {
        return new JsonSetRepository<>(type, provider, mapper);
    }

    public long count() {
        return provider.withParser(mapper, parser -> IteratorUtils.count(new SkippedValueIterator(parser)));
    }

    public boolean isEmpty() {
        return count() == 0;
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
            final Iterator<? extends T> itr = IteratorUtils.filter(iterator, predicate);

            final Set<T> result = new LinkedHashSet<>();
            itr.forEachRemaining(result::add);
            return result;
        });
    }

    @Override
    public <R> R withParser(ObjectMapper mapper, Function<JsonParser, ? extends R> function) {
        return provider.withParser(mapper, function);
    }

    @Override
    public <R> R withGenerator(ObjectMapper mapper, BiFunction<JsonParser, JsonGenerator, ? extends R> function) {
        return provider.withGenerator(mapper, function);
    }

    public <U> U withIterator(Function<Iterator<? extends T>, ? extends U> function) {
        return provider.withParser(mapper, parser -> function.apply(new ValueArrayIterator<>(parser, reader, mapper)));
    }

    public <R> R withStream(Function<Stream<? extends T>, ? extends R> function) {
        return withIterator((iterator) -> {
            final Spliterator<? extends T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
            final Stream<? extends T> stream = StreamSupport.stream(spliterator, false);
            return function.apply(stream);
        });
    }

    public boolean save(T element) {
        return saveAll(Collections.singleton(element)) != 0;
    }

    public long saveAll(T... elements) {
        return saveAll(Arrays.asList(elements));
    }

    public long saveAll(Iterable<? extends T> elements) {
        return withGenerator(mapper, (parser, generator) -> {
            final Map<T, T> newElements = StreamSupport.stream(elements.spliterator(), false)
                    .filter(Objects::nonNull)
                    .collect(LinkedHashMap::new, (map, item) -> map.put(item, item), Map::putAll);

            try {
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

                    final boolean updated;
                    if (newNode == null) {
                        updated = false;
                    }
                    else if (oldNode.isObject() && newNode.isObject()) {
                        updated = JsonUtils.updateObject((ObjectNode) oldNode, (ObjectNode)newNode, generator);
                    }
                    else if (oldNode.isArray() && newNode.isArray()) {
                        updated = JsonUtils.updateArray((ArrayNode) oldNode, (ArrayNode) newNode, generator);
                    }
                    else {
                        updated = !oldNode.equals(newNode);
                        generator.writeTree(newNode);
                    }

                    if (updated) {
                        ++updates;
                    }
                }

                // Create new elements
                for (T newElement : newElements.values()) {
                    final JsonNode newNode = mapper.valueToTree(newElement);
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

    public long deleteAll(Collection<T> elements) {
        return deleteAll(elements::contains);
    }

    public long deleteAll() {
        return deleteAll(value -> true);
    }

    public long deleteAll(Predicate<? super T> predicate) {
        return withGenerator(mapper, (parser, generator) -> {
            try {
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
            catch (IOException e) {
                throw new JsonException(e);
            }
        });
    }
}
