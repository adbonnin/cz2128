package fr.adbonnin.cz2128.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface JsonRepository<T> extends JsonProvider {

    <U> JsonRepository<U> of(Class<U> type);

    <U> JsonRepository<U> of(TypeReference<U> type);

    <U> JsonRepository<U> of(ObjectReader reader);

    default boolean isEmpty() {
        return count() == 0;
    }

    long count();

    default <R> R withStream(Function<Stream<? extends T>, ? extends R> function) {
        return withIterator(iterator -> {
            final Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
            final Stream<T> stream = StreamSupport.stream(spliterator, false);
            return function.apply(stream);
        });
    }

    <R> R withIterator(Function<Iterator<? extends T>, ? extends R> function);
}
