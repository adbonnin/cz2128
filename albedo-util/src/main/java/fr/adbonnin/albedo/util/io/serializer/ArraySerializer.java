package fr.adbonnin.albedo.util.io.serializer;

import fr.adbonnin.albedo.util.Identifiable;
import fr.adbonnin.albedo.util.Predicate;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.List;

public interface ArraySerializer {

    long count(Reader reader) throws IOException;

    <E extends Identifiable> long count(Predicate<E> predicate, Reader reader, Type typeOfT) throws IOException;

    boolean delete(Object id, Reader reader, Writer writer, Type typeOfT) throws IOException;

    <E extends Identifiable> boolean delete(Iterable<E> elements, Reader reader, Writer writer, Type typeOfT) throws IOException;

    <E extends Identifiable> boolean delete(E element, Reader reader, Writer writer, Type typeOfT) throws IOException;

    <E extends Identifiable> boolean delete(Predicate<E> predicate, Reader reader, Writer writer, Type typeOfT) throws IOException;

    boolean exists(Object id, Reader reader, Type typeOfT) throws IOException;

    <E extends Identifiable> boolean exists(Predicate<E> predicate, Reader reader, Type typeOfT) throws IOException;

    <E extends Identifiable> List<E> findAll(Reader reader, Type typeOfT) throws IOException;

    <E extends Identifiable> List<E> findAll(Iterable<Object> ids, Reader reader, Type typeOfT) throws IOException;

    <E extends Identifiable> List<E> findAll(Predicate<E> predicate, Reader reader, Type typeOfT) throws IOException;

    <E extends Identifiable> E findOne(Object id, Reader reader, Type typeOfT) throws IOException;

    <E extends Identifiable> E findOne(Predicate<E> predicate, Reader reader, Type typeOfT) throws IOException;

    <E extends Identifiable> boolean save(Iterable<E> elements, Reader reader, Writer writer, Type typeOfT) throws IOException;

    <E extends Identifiable> boolean save(E element, Reader reader, Writer writer, Type typeOfT) throws IOException;
}
