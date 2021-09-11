package fr.adbonnin.cz2128.json.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.adbonnin.cz2128.collect.IteratorUtils;
import fr.adbonnin.cz2128.json.JsonException;
import fr.adbonnin.cz2128.json.JsonProvider;
import fr.adbonnin.cz2128.json.iterator.FieldObjectIterator;
import fr.adbonnin.cz2128.json.iterator.FieldValueObjectIterator;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

public abstract class BaseMapRepository<T> extends BaseRepository<T> implements MapRepository<T> {

    private final ObjectReader reader;

    protected abstract long saveAll(Map<String, ? extends T> elements, JsonParser parser, JsonGenerator generator) throws IOException;

    protected abstract long deleteAll(Predicate<? super Map.Entry<String, T>> predicate, JsonParser parser, JsonGenerator generator) throws IOException;

    public BaseMapRepository(ObjectReader reader, JsonProvider provider) {
        super(provider);
        this.reader = requireNonNull(reader);
    }

    public ObjectReader getReader() {
        return reader;
    }

    @Override
    public long count() {
        return withFieldIterator(IteratorUtils::count);
    }

    @Override
    public long count(Predicate<? super T> predicate) {
        return withEntryIterator(field -> true, predicate, IteratorUtils::count);
    }

    @Override
    public long countFields(Predicate<String> predicate) {
        return withEntryIterator(predicate, value -> true, IteratorUtils::count);
    }

    @Override
    public long countEntries(Predicate<? super Map.Entry<String, T>> predicate) {
        return withEntryIterator(iterator -> {
            final Iterator<Map.Entry<String, T>> filtered = IteratorUtils.filter(iterator, predicate);
            return IteratorUtils.count(filtered);
        });
    }

    @Override
    public Optional<Map.Entry<String, T>> findFirst(Predicate<? super T> predicate) {
        return withEntryIterator(field -> true, predicate, IteratorUtils::first);
    }

    @Override
    public Optional<Map.Entry<String, T>> findFirstField(Predicate<String> predicate) {
        return withEntryIterator(predicate, value -> true, IteratorUtils::first);
    }

    @Override
    public Optional<Map.Entry<String, T>> findFirstEntry(Predicate<? super Map.Entry<String, T>> predicate) {
        return withEntryIterator(iterator -> IteratorUtils.find(iterator, predicate));
    }

    @Override
    public Map<String, T> findAll() {
        return findAll(entry -> true);
    }

    @Override
    public Map<String, T> findAll(Predicate<? super Map.Entry<String, T>> predicate) {
        return withEntryIterator(iterator -> {
            final Iterator<? extends Map.Entry<String, ? extends T>> filtered = IteratorUtils.filter(iterator, predicate);
            return IteratorUtils.newLinkedHashMap(filtered);
        });
    }

    @Override
    public <R> R withIterator(Function<Iterator<? extends T>, ? extends R> function) {
        return withEntryIterator(iterator -> {
            final Iterator<T> valueIterator = IteratorUtils.valueIterator(iterator);
            return function.apply(valueIterator);
        });
    }

    @Override
    public <R> R withFieldIterator(Function<Iterator<? extends String>, R> function) {
        return withParser(parser -> {
            final FieldObjectIterator fieldIterator = new FieldObjectIterator(parser);
            return function.apply(fieldIterator);
        });
    }

    @Override
    public <R> R withEntryIterator(Function<Iterator<? extends Map.Entry<String, T>>, R> function) {
        return withEntryIterator(field -> true, value -> true, function);
    }

    private <R> R withEntryIterator(Predicate<String> fieldPredicate, Predicate<? super T> valuePredicate, Function<Iterator<? extends Map.Entry<String, T>>, R> function) {
        return withParser(parser -> {
            final FieldValueObjectIterator<T> fieldValueIterator = new FieldValueObjectIterator<>(parser, reader, fieldPredicate, valuePredicate);
            return function.apply(fieldValueIterator);
        });
    }

    @Override
    public <R> R withEntryStream(Function<Stream<? extends Map.Entry<String, T>>, R> function) {
        return withEntryIterator(iterator -> {
            final Spliterator<Map.Entry<String, T>> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
            final Stream<Map.Entry<String, T>> stream = StreamSupport.stream(spliterator, false);
            return function.apply(stream);
        });
    }

    @Override
    public boolean save(String key, T element) {
        return saveAll(Collections.singletonMap(key, element)) != 0;
    }

    @Override
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

    @Override
    public boolean delete(String key) {
        return deleteAll(e -> key.equals(e.getKey())) > 0;
    }

    @Override
    public long deleteAll() {
        return deleteAll(value -> true);
    }

    @Override
    public long deleteAll(Collection<String> elements) {
        return deleteAll(e -> elements.contains(e.getKey()));
    }

    @Override
    public long deleteAll(Predicate<? super Map.Entry<String, T>> predicate) {
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
