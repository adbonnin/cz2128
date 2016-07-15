package fr.adbonnin.albedo.util.io.serializer;

import fr.adbonnin.albedo.util.Identifiable;
import fr.adbonnin.albedo.util.Predicate;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.List;

public interface EntitySerializer {

    long count(Reader reader) throws IOException;

    <T extends Identifiable> long count(Predicate<T> predicate, Reader reader, Type typeOfT) throws IOException;

    boolean delete(Object id, Reader reader, Writer writer, Type typeOfT) throws IOException;

    <T extends Identifiable> boolean delete(Iterable<T> entities, Reader reader, Writer writer, Type typeOfT) throws IOException;

    <T extends Identifiable> boolean delete(T entity, Reader reader, Writer writer, Type typeOfT) throws IOException;

    <T extends Identifiable> boolean delete(Predicate<T> predicate, Reader reader, Writer writer, Type typeOfT) throws IOException;

    boolean exists(Object id, Reader reader, Type typeOfT) throws IOException;

    <T extends Identifiable> boolean exists(Predicate<T> predicate, Reader reader, Type typeOfT) throws IOException;

    <T extends Identifiable> List<T> findAll(Reader reader, Type typeOfT) throws IOException;

    <T extends Identifiable> List<T> findAll(Iterable<Object> ids, Reader reader, Type typeOfT) throws IOException;

    <T extends Identifiable> List<T> findAll(Predicate<T> predicate, Reader reader, Type typeOfT) throws IOException;

    <T extends Identifiable> T findOne(Object id, Reader reader, Type typeOfT) throws IOException;

    <T extends Identifiable> T findOne(Predicate<T> predicate, Reader reader, Type typeOfT) throws IOException;

    <T extends Identifiable> boolean save(Iterable<T> entities, Reader reader, Writer writer, Type typeOfT) throws IOException;

    <T extends Identifiable> boolean save(T entity, Reader reader, Writer writer, Type typeOfT) throws IOException;
}
