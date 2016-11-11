package fr.adbonnin.cz2128.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import fr.adbonnin.cz2128.base.Predicate;

import java.io.IOException;
import java.util.List;

public interface Serializer {

    long count(JsonParser parser) throws IOException;

    <T> long count(JsonParser parser, ValueReader<T> reader, Predicate<? super T> predicate) throws IOException;

    <T> boolean delete(JsonParser parser, ValueReader<T> reader, JsonGenerator generator, Predicate<? super T> predicate) throws IOException;

    <T> List<T> findAll(JsonParser parser, ValueReader<T> reader, Predicate<? super T> predicate) throws IOException;

    <T> T findOne(JsonParser parser, ValueReader<T> reader, Predicate<? super T> predicate, T defaultValue) throws IOException;

    <T> boolean save(Iterable<T> elements, JsonParser parser, ValueReader<T> reader, JsonGenerator generator) throws IOException;
}
