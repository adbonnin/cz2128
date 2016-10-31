package fr.adbonnin.cz2128.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import fr.adbonnin.cz2128.base.Identifiable;
import fr.adbonnin.cz2128.base.Predicate;
import fr.adbonnin.cz2128.collect.IteratorUtils;
import fr.adbonnin.cz2128.io.CloseableIterator;

import java.io.IOException;
import java.util.List;

import static fr.adbonnin.cz2128.collect.CollectionUtils.newArrayList;
import static fr.adbonnin.cz2128.io.IOUtils.closeQuietly;

public abstract class IterableIdentifiableSerializer extends AbstractIdentifiableSerializer {

    protected abstract CloseableIterator<Void> asSkippedElementIterator(JsonParser parser) throws IOException;

    protected abstract <T> CloseableIterator<T> asElementIterator(JsonParser parser, JavaType typeOfT) throws IOException;

    @Override
    public long count(JsonParser parser) throws IOException {
        CloseableIterator<Void> itr = null;
        try {
            itr = asSkippedElementIterator(parser);
            return IteratorUtils.count(itr);
        }
        finally {
            closeQuietly(itr);
        }
    }

    @Override
    public <E extends Identifiable> long count(Predicate<E> predicate, JsonParser parser, JavaType typeOfT) throws IOException {
        CloseableIterator<E> itr = null;
        try {
            itr = asElementIterator(parser, typeOfT);
            return IteratorUtils.count(IteratorUtils.filter(itr, predicate));
        }
        finally {
            closeQuietly(itr);
        }
    }

    @Override
    public <E extends Identifiable> List<E> findAll(Predicate<E> predicate, JsonParser parser, JavaType typeOfT) throws IOException {
        CloseableIterator<E> itr = null;
        try {
            itr = asElementIterator(parser, typeOfT);
            return newArrayList(IteratorUtils.filter(itr, predicate));
        }
        finally {
            closeQuietly(itr);
        }
    }

    @Override
    public <E extends Identifiable> E findOne(Predicate<E> predicate, JsonParser parser, JavaType typeOfT) throws IOException {
        CloseableIterator<E> itr = null;
        try {
            itr = asElementIterator(parser, typeOfT);
            return IteratorUtils.find(itr, predicate, null);
        }
        finally {
            closeQuietly(itr);
        }
    }
}
