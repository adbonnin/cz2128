package fr.adbonnin.albedo.util.io.serializer;

import fr.adbonnin.albedo.util.Identifiable;
import fr.adbonnin.albedo.util.IdentifiableUtils;
import fr.adbonnin.albedo.util.Predicate;
import fr.adbonnin.albedo.util.PredicateUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.List;

import static fr.adbonnin.albedo.util.IdentifiableUtils.equalsIdPredicate;
import static fr.adbonnin.albedo.util.IdentifiableUtils.toIdIterator;
import static java.util.Collections.singletonList;

public abstract class AbstractArraySerializer implements ArraySerializer {

    @Override
    public <T extends Identifiable> long count(Predicate<T> predicate, Reader reader, Type typeOfT) throws IOException {
        return findAll(predicate, reader, typeOfT).size();
    }

    @Override
    public boolean delete(Object id, Reader reader, Writer writer, Type typeOfT) throws IOException {
        return delete(equalsIdPredicate(id), reader, writer, typeOfT);
    }

    @Override
    public <T extends Identifiable> boolean delete(Iterable<T> elements, Reader reader, Writer writer, Type typeOfT) throws IOException {
        return delete(equalsIdPredicate(toIdIterator(elements.iterator())), reader, writer, typeOfT);
    }

    @Override
    public <T extends Identifiable> boolean delete(T element, Reader reader, Writer writer, Type typeOfT) throws IOException {
        return delete(equalsIdPredicate(element), reader, writer, typeOfT);
    }

    @Override
    public boolean exists(Object id, Reader reader, Type typeOfT) throws IOException {
        return exists(equalsIdPredicate(id), reader, typeOfT);
    }

    @Override
    public <T extends Identifiable> boolean exists(Predicate<T> predicate, Reader reader, Type typeOfT) throws IOException {
        return findOne(predicate, reader, typeOfT) != null;
    }

    @Override
    public <T extends Identifiable> List<T> findAll(Reader reader, Type typeOfT) throws IOException {
        return findAll(PredicateUtils.<T>alwaysTrue(), reader, typeOfT);
    }

    @Override
    public <T extends Identifiable> List<T> findAll(Iterable<Object> ids, Reader reader, Type typeOfT) throws IOException {
        return findAll(IdentifiableUtils.<T>containsIdsPredicate(ids.iterator()), reader, typeOfT);
    }

    @Override
    public <T extends Identifiable> T findOne(Object id, Reader reader, Type typeOfT) throws IOException {
        return findOne(IdentifiableUtils.<T>equalsIdPredicate(id), reader, typeOfT);
    }

    @Override
    public <T extends Identifiable> boolean save(T element, Reader reader, Writer writer, Type typeOfT) throws IOException {
        return save(singletonList(element), reader, writer, typeOfT);
    }
}
