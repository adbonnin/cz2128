package fr.adbonnin.cz2128.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import fr.adbonnin.cz2128.base.Identifiable;
import fr.adbonnin.cz2128.base.Predicate;

import java.io.IOException;
import java.util.List;

public interface IdentifiableSerializer {

    long count(JsonParser parser) throws IOException;

    <E extends Identifiable> long count(Predicate<E> predicate, JsonParser parser, JavaType typeOfT) throws IOException;

    boolean delete(Object id, JsonParser parser, JsonGenerator generator, JavaType typeOfT) throws IOException;

    <E extends Identifiable> boolean delete(Iterable<E> elements, JsonParser parser, JsonGenerator generator, JavaType typeOfT) throws IOException;

    <E extends Identifiable> boolean delete(E element, JsonParser parser, JsonGenerator generator, JavaType typeOfT) throws IOException;

    <E extends Identifiable> boolean delete(Predicate<E> predicate, JsonParser parser, JsonGenerator generator, JavaType typeOfT) throws IOException;

    boolean exists(Object id, JsonParser parser, JavaType typeOfT) throws IOException;

    <E extends Identifiable> boolean exists(Predicate<E> predicate, JsonParser parser, JavaType typeOfT) throws IOException;

    <E extends Identifiable> List<E> findAll(JsonParser parser, JavaType typeOfT) throws IOException;

    <E extends Identifiable> List<E> findAll(Iterable<Object> ids, JsonParser parser, JavaType typeOfT) throws IOException;

    <E extends Identifiable> List<E> findAll(Predicate<E> predicate, JsonParser parser, JavaType typeOfT) throws IOException;

    <E extends Identifiable> E findOne(Object id, JsonParser parser, JavaType typeOfT) throws IOException;

    <E extends Identifiable> E findOne(Predicate<E> predicate, JsonParser parser, JavaType typeOfT) throws IOException;

    <E extends Identifiable> boolean save(Iterable<E> elements, JsonParser parser, JsonGenerator generator, JavaType typeOfT) throws IOException;

    <E extends Identifiable> boolean save(E element, JsonParser parser, JsonGenerator generator, JavaType typeOfT) throws IOException;
}
