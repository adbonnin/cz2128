package fr.adbonnin.cz2128.collect;

import fr.adbonnin.cz2128.base.Predicate;

import java.util.Iterator;

import static java.util.Objects.requireNonNull;

public final class IteratorUtils {

    public static long count(Iterator<?> iterator) {

        long count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            ++count;
        }

        return count;
    }

    public static <E> Iterator<E> filter(final Iterator<E> iterator, final Predicate<? super E> predicate) {
        requireNonNull(iterator);
        requireNonNull(predicate);
        return new AbstractIterator<E>() {

            @Override
            protected E computeNext() {
                while (iterator.hasNext()) {
                    final E element = iterator.next();
                    if (predicate.evaluate(element)) {
                        return element;
                    }
                }
                return endOfData();
            }
        };
    }

    public static <E> E find(Iterator<? extends E> iterator, Predicate<? super E> predicate, E defaultValue) {
        return next(filter(iterator, predicate), defaultValue);
    }

    public static <E> E next(Iterator<? extends E> iterator, E defaultValue) {
        return iterator.hasNext() ? iterator.next() : defaultValue;
    }

    private IteratorUtils() { /* Cannot be instantiated */ }
}
