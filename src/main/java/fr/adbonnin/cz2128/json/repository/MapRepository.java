package fr.adbonnin.cz2128.json.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.adbonnin.cz2128.collect.IteratorUtils;
import fr.adbonnin.cz2128.json.JsonException;
import fr.adbonnin.cz2128.json.JsonProvider;
import fr.adbonnin.cz2128.json.iterator.SkipChildrenObjectIterator;
import fr.adbonnin.cz2128.json.iterator.ValueObjectIterator;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

public abstract class MapRepository<T> extends BaseRepository<T> {

    private final ObjectReader reader;

    protected abstract long saveAll(Map<String, ? extends T> elements, JsonParser parser, JsonGenerator generator) throws IOException;

    protected abstract long deleteAll(Predicate<? super Map.Entry<String, ? extends T>> predicate, JsonParser parser, JsonGenerator generator) throws IOException;

    public MapRepository(ObjectReader reader, JsonProvider provider) {
        super(provider);
        this.reader = requireNonNull(reader);
    }

    public ObjectReader getReader() {
        return reader;
    }

    @Override
    public boolean isEmpty() {
        return count() == 0;
    }

    public long count() {
        return withParser(parser -> IteratorUtils.count(new SkipChildrenObjectIterator(parser)));
    }

    public long count(Predicate<? super Map.Entry<String, T>> predicate) {
        return withEntryIterator(iterator -> IteratorUtils.count(IteratorUtils.filter(iterator, predicate)));
    }

    public Optional<Map.Entry<String, T>> findFirst(Predicate<? super Map.Entry<String, T>> predicate) {
        return withEntryIterator(iterator -> IteratorUtils.find(iterator, predicate));
    }

    public Map<String, T> findAll() {
        return findAll(x -> true);
    }

    public Map<String, T> findAll(Predicate<? super Map.Entry<String, T>> predicate) {
        return withEntryIterator(iterator -> {
            final Iterator<Map.Entry<String, T>> itr = IteratorUtils.filter(iterator, predicate);

            final Map<String, T> result = new LinkedHashMap<>();
            itr.forEachRemaining(e -> result.put(e.getKey(), e.getValue()));
            return result;
        });
    }

    @Override
    public <R> R withIterator(Function<Iterator<? extends T>, ? extends R> function) {
        return withEntryIterator(iterator -> {
            final Iterator<T> valueIterator = IteratorUtils.valueIterator(iterator);
            return function.apply(valueIterator);
        });
    }

    public <R> R withEntryStream(Function<Stream<? extends Map.Entry<String, T>>, ? extends R> function) {
        return withEntryIterator(iterator -> {
            final Spliterator<Map.Entry<String, T>> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
            final Stream<Map.Entry<String, T>> stream = StreamSupport.stream(spliterator, false);
            return function.apply(stream);
        });
    }

    public <R> R withEntryIterator(Function<Iterator<? extends Map.Entry<String, T>>, ? extends R> function) {
        return withParser(parser -> function.apply(new ValueObjectIterator<>(parser, reader)));
    }

    public boolean save(String key, T element) {
        return saveAll(Collections.singletonMap(key, element)) != 0;
    }

    public long saveAll(Map<String, ? extends T> elements) {
        return withGenerator((parser, generator) -> {
            try {
                return saveAll(elements, parser, generator);
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
                return deleteAll(predicate, parser, generator);
            }
            catch (IOException e) {
                throw new JsonException(e);
            }
        });
    }
}
