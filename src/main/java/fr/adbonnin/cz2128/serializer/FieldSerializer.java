package fr.adbonnin.cz2128.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import fr.adbonnin.cz2128.base.Predicate;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class FieldSerializer implements Serializer {

    private final Serializer serializer;

    private final String field;

    public FieldSerializer(Serializer serializer, String field) {
        this.serializer = requireNonNull(serializer);
        this.field = requireNonNull(field);
    }

    @Override
    public long count(JsonParser parser) throws IOException {
        return 0;
    }

    @Override
    public <E> long count(JsonParser parser, ValueReader<E> reader, Predicate<? super E> predicate) throws IOException {
        return 0;
    }

    @Override
    public <E> boolean delete(JsonParser parser, ValueReader<E> reader, JsonGenerator generator, Predicate<? super E> predicate) throws IOException {
        return false;
    }

    @Override
    public <E> List<E> findAll(JsonParser parser, ValueReader<E> reader, Predicate<? super E> predicate) throws IOException {
        return null;
    }

    @Override
    public <E> E findOne(JsonParser parser, ValueReader<E> reader, Predicate<? super E> predicate, E defaultValue) throws IOException {
        return null;
    }

    @Override
    public <E> boolean save(Iterable<E> elements, JsonParser parser, ValueReader<E> reader, JsonGenerator generator) throws IOException {
        return false;
    }
}
