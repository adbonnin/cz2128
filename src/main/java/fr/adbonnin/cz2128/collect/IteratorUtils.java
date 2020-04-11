package fr.adbonnin.cz2128.collect;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public class IteratorUtils {

    public static long count(Iterator<?> iterator) {

        long count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            ++count;
        }

        return count;
    }

    public static <E> Iterator<E> filter(Iterator<? extends E> iterator, Predicate<? super E> predicate) {
        requireNonNull(iterator);
        requireNonNull(predicate);
        return new AbstractIterator<E>() {

            @Override
            protected E computeNext() {
                while (iterator.hasNext()) {
                    final E element = iterator.next();
                    if (predicate.test(element)) {
                        return element;
                    }
                }
                return endOfData();
            }
        };
    }

    public static <E> Optional<E> find(Iterator<? extends E> iterator, Predicate<? super E> predicate) {
        final Iterator<? extends E> filtered = IteratorUtils.filter(iterator, predicate);
        return filtered.hasNext() ? Optional.of(filtered.next()) : Optional.empty();
    }

    private IteratorUtils() { /* Cannot be instantiated */ }
}
