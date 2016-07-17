package fr.adbonnin.albedo.util.collect;

import fr.adbonnin.albedo.util.Predicate;

import java.util.*;

import static java.util.Collections.emptyIterator;
import static java.util.Objects.requireNonNull;

public final class IteratorUtils {

    public static boolean equal(Iterator<?> iterator1, Iterator<?> iterator2) {

        if (iterator1 == iterator2) {
            return true;
        }

        if (iterator1 == null || iterator2 == null) {
            return false;
        }

        while (iterator1.hasNext()) {

            if (!iterator2.hasNext()) {
                return false;
            }

            final Object o1 = iterator1.next();
            final Object o2 = iterator2.next();
            if (!Objects.equals(o1, o2)) {
                return false;
            }
        }

        return !iterator2.hasNext();
    }

    public static int count(Iterator<?> iterator) {
        requireNonNull(iterator);

        int count = 0;
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

    public static <T> Iterator<T> concat(final Iterator<? extends Iterator<? extends T>> iterators) {
        requireNonNull(iterators);

        if (!iterators.hasNext()) {
            return emptyIterator();
        }

        return new AbstractIterator<T>() {

            private Iterator<? extends T> current = iterators.next();

            @Override
            protected T computeNext() {
                for (;;) {
                    if (current.hasNext()) {
                        return current.next();
                    }
                    else if (iterators.hasNext()) {
                        current = iterators.next();
                    }
                    else {
                        return endOfData();
                    }
                }
            }
        };
    }

    public static <T> Iterator<T> asIterator(final T item) {
        return new Iterator<T>() {

            private boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public T next() {
                if (hasNext) {
                    hasNext = false;
                    return item;
                }
                else {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static <T> Iterator<T> asIterator(final T... items) {
        final int len = items == null ? 0 : items.length;

        if (len <= 0) {
            return emptyIterator();
        }

        return new Iterator<T>() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < len;
            }

            @Override
            public T next() {

                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                return items[index++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static <K, V> Iterator<V> asValuesIterator(final Iterator<? extends Map.Entry<? extends K, ? extends V>> iterator) {

        if (iterator == null) {
            return emptyIterator();
        }

        return new Iterator<V>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public V next() {
                final Map.Entry<? extends K, ? extends V> next = iterator.next();
                return next == null ? null : next.getValue();
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

    private IteratorUtils() { /* Cannot be instantiated */ }
}
