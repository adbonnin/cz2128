package fr.adbonnin.cz2128.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import fr.adbonnin.cz2128.base.Identifiable;
import fr.adbonnin.cz2128.base.IdentifiableUtils;
import fr.adbonnin.cz2128.base.Predicate;
import fr.adbonnin.cz2128.base.PredicateUtils;

import java.io.IOException;
import java.util.List;

import static java.util.Collections.singletonList;

public abstract class AbstractIdentifiableSerializer implements IdentifiableSerializer {

    @Override
    public <E extends Identifiable> long count(Predicate<E> predicate, JsonParser parser, JavaType typeOfT) throws IOException {
        return findAll(predicate, parser, typeOfT).size();
    }

    @Override
    public boolean delete(Object id, JsonParser parser, JsonGenerator generator, JavaType typeOfT) throws IOException {
        return delete(IdentifiableUtils.equalsIdPredicate(id), parser, generator, typeOfT);
    }

    @Override
    public <E extends Identifiable> boolean delete(Iterable<E> elements, JsonParser parser, JsonGenerator generator, JavaType typeOfT) throws IOException {
        return delete(IdentifiableUtils.equalsIdPredicate(IdentifiableUtils.toIdIterator(elements.iterator())), parser, generator, typeOfT);
    }

    @Override
    public <E extends Identifiable> boolean delete(E element, JsonParser parser, JsonGenerator generator, JavaType typeOfT) throws IOException {
        return delete(IdentifiableUtils.equalsIdPredicate(element), parser, generator, typeOfT);
    }

    @Override
    public boolean exists(Object id, JsonParser parser, JavaType typeOfT) throws IOException {
        return exists(IdentifiableUtils.equalsIdPredicate(id), parser, typeOfT);
    }

    @Override
    public <E extends Identifiable> boolean exists(Predicate<E> predicate, JsonParser parser, JavaType typeOfT) throws IOException {
        return findOne(predicate, parser, typeOfT) != null;
    }

    @Override
    public <E extends Identifiable> List<E> findAll(JsonParser parser, JavaType typeOfT) throws IOException {
        return findAll(PredicateUtils.<E>alwaysTrue(), parser, typeOfT);
    }

    @Override
    public <E extends Identifiable> List<E> findAll(Iterable<Object> ids, JsonParser parser, JavaType typeOfT) throws IOException {
        return findAll(IdentifiableUtils.<E>containsIdsPredicate(ids.iterator()), parser, typeOfT);
    }

    @Override
    public <E extends Identifiable> E findOne(Object id, JsonParser parser, JavaType typeOfT) throws IOException {
        return findOne(IdentifiableUtils.<E>equalsIdPredicate(id), parser, typeOfT);
    }

    @Override
    public <E extends Identifiable> boolean save(E element, JsonParser parser, JsonGenerator generator, JavaType typeOfT) throws IOException {
        return save(singletonList(element), parser, generator, typeOfT);
    }
}
