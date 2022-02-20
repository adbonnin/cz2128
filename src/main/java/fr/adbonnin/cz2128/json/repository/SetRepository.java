package fr.adbonnin.cz2128.json.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.adbonnin.cz2128.collect.IteratorUtils;
import fr.adbonnin.cz2128.json.JsonException;
import fr.adbonnin.cz2128.json.JsonProvider;
import fr.adbonnin.cz2128.json.iterator.SkipChildrenArrayIterator;
import fr.adbonnin.cz2128.json.iterator.ValueArrayIterator;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class SetRepository<T> extends BaseRepository<T> {

    private final ObjectReader reader;

    protected abstract long saveAll(JsonParser parser, JsonGenerator generator, Iterator<? extends T> elements) throws IOException;

    protected abstract long deleteAll(JsonParser parser, JsonGenerator generator, Predicate<? super T> predicate) throws IOException;

    public SetRepository(JsonProvider provider, ObjectReader reader) {
        super(provider);
        this.reader = reader;
    }

    public ObjectReader getReader() {
        return reader;
    }

    @Override
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
            return IteratorUtils.newLinkedHashSet(itr);
        });
    }

    @Override
    public <R> R withIterator(Function<Iterator<? extends T>, ? extends R> function) {
        return withParser(parser -> function.apply(new ValueArrayIterator<>(parser, reader)));
    }

    public boolean save(T element) {
        return saveAll(Collections.singleton(element)) != 0;
    }

    public long saveAll(T... elements) {
        return saveAll(Arrays.asList(elements));
    }

    public long saveAll(Iterable<? extends T> elements) {
        return saveAll(elements.iterator());
    }

    public long saveAll(Iterator<? extends T> elements) {
        return withGenerator((parser, generator) -> {
            try {
                return saveAll(parser, generator, elements);
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
                return deleteAll(parser, generator, predicate);
            }
            catch (IOException e) {
                throw new JsonException(e);
            }
        });
    }
}
