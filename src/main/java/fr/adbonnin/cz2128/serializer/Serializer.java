package fr.adbonnin.cz2128.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import fr.adbonnin.cz2128.base.Predicate;

import java.io.IOException;
import java.util.List;

public interface Serializer {

    long count(JsonParser parser) throws IOException;

    <E> long count(JsonParser parser, ValueReader<E> reader, Predicate<? super E> predicate) throws IOException;

    <E> boolean delete(JsonParser parser, ValueReader<E> reader, JsonGenerator generator, Predicate<? super E> predicate) throws IOException;

    <E> List<E> findAll(JsonParser parser, ValueReader<E> reader, Predicate<? super E> predicate) throws IOException;

    <E> E findOne(JsonParser parser, ValueReader<E> reader, Predicate<? super E> predicate, E defaultValue) throws IOException;

    <E> boolean save(Iterable<E> elements, JsonParser parser, ValueReader<E> reader, JsonGenerator generator) throws IOException;
}
