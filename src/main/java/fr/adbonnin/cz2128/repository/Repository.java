package fr.adbonnin.cz2128.repository;

import com.fasterxml.jackson.core.JsonFactory;
import fr.adbonnin.cz2128.base.Predicate;
import fr.adbonnin.cz2128.base.PredicateUtils;
import fr.adbonnin.cz2128.io.stream.StreamProcessor;
import fr.adbonnin.cz2128.serializer.Serializer;
import fr.adbonnin.cz2128.serializer.ValueReader;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class Repository<E> extends BaseRepository {

    private final ValueReader<E> reader;

    public Repository(StreamProcessor processor, JsonFactory jsonFactory, Serializer serializer, ValueReader<E> reader) {
        super(processor, jsonFactory, serializer);
        this.reader = requireNonNull(reader);
    }

    public long count(Predicate<? super E> predicate) throws IOException {
        return count(reader, predicate);
    }

    public boolean delete(Predicate<? super E> predicate) throws IOException {
        return delete(reader, predicate);
    }

    public List<E> findAll(Predicate<? super E> predicate) throws IOException {
        return findAll(reader, predicate);
    }

    public E findOne(Predicate<? super E> predicate, E defaultValue) throws IOException {
        return findOne(reader, predicate, defaultValue);
    }

    public E first(E defaultValue) throws IOException {
        return findOne(reader, PredicateUtils.<E>alwaysTrue(), defaultValue);
    }

    public boolean save(E element) throws IOException {
        return save(element, reader);
    }

    public boolean save(Iterable<E> elements) throws IOException {
        return save(elements, reader);
    }
}
