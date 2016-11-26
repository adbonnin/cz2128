package fr.adbonnin.cz2128.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import fr.adbonnin.cz2128.base.Predicate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static fr.adbonnin.cz2128.serializer.JacksonUtils.readToField;
import static fr.adbonnin.cz2128.serializer.JacksonUtils.copyCurrent;
import static fr.adbonnin.cz2128.serializer.JacksonUtils.writeToField;
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
        return readToField(parser, field) ? serializer.count(parser) : 0;
    }

    @Override
    public <T> long count(JsonParser parser, ValueReader<T> reader, Predicate<? super T> predicate) throws IOException {
        return readToField(parser, field) ? serializer.count(parser, reader, predicate) : 0;
    }

    @Override
    public <T> boolean delete(JsonParser parser, ValueReader<T> reader, JsonGenerator generator, Predicate<? super T> predicate) throws IOException {
        writeStartToField(parser, generator);
        final boolean result = serializer.delete(parser, reader, generator, predicate);
        writeFromFieldToEnd(parser, generator);
        return result;
    }

    @Override
    public <T> List<T> findAll(JsonParser parser, ValueReader<T> reader, Predicate<? super T> predicate) throws IOException {
        return readToField(parser, field) ? serializer.findAll(parser, reader, predicate) : Collections.<T>emptyList();
    }

    @Override
    public <T> T findOne(JsonParser parser, ValueReader<T> reader, Predicate<? super T> predicate, T defaultValue) throws IOException {
        return readToField(parser, field) ? serializer.findOne(parser, reader, predicate, defaultValue) : defaultValue;
    }

    @Override
    public <T> boolean save(Iterable<T> elements, JsonParser parser, ValueReader<T> reader, JsonGenerator generator) throws IOException {
        writeStartToField(parser, generator);
        final boolean result = serializer.save(elements, parser, reader, generator);
        writeFromFieldToEnd(parser, generator);
        return result;
    }

    private void writeStartToField(JsonParser parser, JsonGenerator generator) throws IOException {
        generator.writeStartObject();
        writeToField(parser, generator, field);
    }

    private void writeFromFieldToEnd(JsonParser parser, JsonGenerator generator) throws IOException {
        copyCurrent(parser, generator);
        generator.writeEndObject();
    }
}
