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
    public <T> long count(JsonParser parser, ValueReader<T> reader, Predicate<? super T> predicate) throws IOException {
        return 0;
    }

    @Override
    public <T> boolean delete(JsonParser parser, ValueReader<T> reader, JsonGenerator generator, Predicate<? super T> predicate) throws IOException {
        return false;
    }

    @Override
    public <T> List<T> findAll(JsonParser parser, ValueReader<T> reader, Predicate<? super T> predicate) throws IOException {
        return null;
    }

    @Override
    public <T> T findOne(JsonParser parser, ValueReader<T> reader, Predicate<? super T> predicate, T defaultValue) throws IOException {
        return null;
    }

    @Override
    public <T> boolean save(Iterable<T> elements, JsonParser parser, ValueReader<T> reader, JsonGenerator generator) throws IOException {
        return false;
    }
}
