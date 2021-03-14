package fr.adbonnin.cz2128.json.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.adbonnin.cz2128.collect.IteratorUtils;
import fr.adbonnin.cz2128.json.JsonException;
import fr.adbonnin.cz2128.json.JsonProvider;
import fr.adbonnin.cz2128.json.JsonRepository;
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

public abstract class SetRepository<T> implements JsonRepository {

    private final ObjectReader reader;

    private final JsonProvider provider;

    protected abstract long saveAll(Iterable<? extends T> elements, JsonParser parser, JsonGenerator generator) throws IOException;

    protected abstract long deleteAll(Predicate<? super T> predicate, JsonParser parser, JsonGenerator generator) throws IOException;

    public SetRepository(ObjectReader reader, JsonProvider provider) {
        this.reader = requireNonNull(reader);
        this.provider = requireNonNull(provider);
    }

    public ObjectReader getReader() {
        return reader;
    }

    public JsonProvider getProvider() {
        return provider;
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
        return withParser(parser -> function.apply(new ValueArrayIterator<>(parser, reader)));
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
            try {
                return saveAll(elements, parser, generator);
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
                return deleteAll(predicate, parser, generator);
            }
            catch (IOException e) {
                throw new JsonException(e);
            }
        });
    }
}
