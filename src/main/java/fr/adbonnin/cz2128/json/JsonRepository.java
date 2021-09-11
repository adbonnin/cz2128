package fr.adbonnin.cz2128.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

public interface JsonRepository<T> extends JsonProvider {

    <U> JsonRepository<U> of(Class<U> type);

    <U> JsonRepository<U> of(TypeReference<U> type);

    <U> JsonRepository<U> of(ObjectReader reader);

    default boolean isEmpty() {
        return count() == 0;
    }

    long count();

    <R> R withStream(Function<Stream<? extends T>, ? extends R> function);

    <R> R withIterator(Function<Iterator<? extends T>, ? extends R> function);
}
