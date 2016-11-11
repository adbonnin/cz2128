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

    public static <T> Iterator<T> filter(final Iterator<T> iterator, final Predicate<? super T> predicate) {
        requireNonNull(iterator);
        requireNonNull(predicate);
        return new AbstractIterator<T>() {

            @Override
            protected T computeNext() {
                while (iterator.hasNext()) {
                    final T element = iterator.next();
                    if (predicate.evaluate(element)) {
                        return element;
                    }
                }
                return endOfData();
            }
        };
    }

    public static <T> T find(Iterator<? extends T> iterator, Predicate<? super T> predicate, T defaultValue) {
        return next(filter(iterator, predicate), defaultValue);
    }

    public static <T> T next(Iterator<? extends T> iterator, T defaultValue) {
        return iterator.hasNext() ? iterator.next() : defaultValue;
    }

    private IteratorUtils() { /* Cannot be instantiated */ }
}
