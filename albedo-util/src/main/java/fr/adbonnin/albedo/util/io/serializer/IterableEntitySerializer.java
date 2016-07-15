package fr.adbonnin.albedo.util.io.serializer;

import fr.adbonnin.albedo.util.Identifiable;
import fr.adbonnin.albedo.util.Predicate;
import fr.adbonnin.albedo.util.collect.IteratorUtils;
import fr.adbonnin.albedo.util.io.CloseableIterator;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

import static fr.adbonnin.albedo.util.collect.CollectionUtils.newArrayList;
import static fr.adbonnin.albedo.util.collect.IteratorUtils.filter;
import static fr.adbonnin.albedo.util.collect.IteratorUtils.find;
import static fr.adbonnin.albedo.util.io.IOUtils.closeQuietly;

public abstract class IterableEntitySerializer extends AbstractEntitySerializer {

    protected abstract CloseableIterator<Void> asSkippedValueIterator(Reader reader) throws IOException;

    protected abstract <T> CloseableIterator<T> asEntityIterator(Reader reader, final Type typeOfT) throws IOException;

    @Override
    public long count(Reader reader) throws IOException {
        CloseableIterator<Void> itr = null;
        try {
            itr = asSkippedValueIterator(reader);
            return IteratorUtils.count(itr);
        }
        finally {
            closeQuietly(itr);
        }
    }

    @Override
    public <T extends Identifiable> long count(Predicate<T> predicate, Reader reader, Type typeOfT) throws IOException {
        CloseableIterator<T> itr = null;
        try {
            itr = asEntityIterator(reader, typeOfT);
            return IteratorUtils.count(filter(itr, predicate));
        }
        finally {
            closeQuietly(itr);
        }
    }

    @Override
    public <T extends Identifiable> List<T> findAll(Predicate<T> predicate, Reader reader, Type typeOfT) throws IOException {
        CloseableIterator<T> itr = null;
        try {
            itr = asEntityIterator(reader, typeOfT);
            return newArrayList(filter(itr, predicate));
        }
        finally {
            closeQuietly(itr);
        }
    }

    @Override
    public <T extends Identifiable> T findOne(Predicate<T> predicate, Reader reader, Type typeOfT) throws IOException {
        CloseableIterator<T> itr = null;
        try {
            itr = asEntityIterator(reader, typeOfT);
            return find(itr, predicate, null);
        }
        finally {
            closeQuietly(itr);
        }
    }
}
