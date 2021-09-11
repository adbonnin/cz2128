package fr.adbonnin.cz2128.collect;

import java.util.*;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public class IteratorUtils {

    public static <E> Optional<E> first(Iterator<? extends E> iterator) {
        return iterator.hasNext() ? Optional.ofNullable(iterator.next()) : Optional.empty();
    }

    public static <E> ArrayList<E> newArrayList(Iterator<? extends E> iterator) {
        final ArrayList<E> list = new ArrayList<>();
        IteratorUtils.addAll(list, iterator);
        return list;
    }

    public static <E> LinkedHashSet<E> newLinkedHashSet(Iterator<? extends E> iterator) {
        final LinkedHashSet<E> list = new LinkedHashSet<>();
        IteratorUtils.addAll(list, iterator);
        return list;
    }

    public static <E> boolean addAll(Collection<E> addTo, Iterator<? extends E> iterator) {
        requireNonNull(addTo);
        requireNonNull(iterator);

        boolean wasModified = false;
        while (iterator.hasNext()) {
            wasModified = addTo.add(iterator.next()) || wasModified;
        }

        return wasModified;
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Iterator<? extends Map.Entry<? extends K, ? extends V>> iterator) {
        final LinkedHashMap<K, V> map = new LinkedHashMap<>();
        IteratorUtils.putAll(map, iterator);
        return map;
    }

    public static <K, V> void putAll(Map<K, V> putTo, Iterator<? extends Map.Entry<? extends K, ? extends V>> iterator) {
        requireNonNull(putTo);
        requireNonNull(iterator);

        while (iterator.hasNext()) {
            final Map.Entry<? extends K, ? extends V> next = iterator.next();
            putTo.put(next.getKey(), next.getValue());
        }
    }

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

    public static <K, V> Iterator<V> valueIterator(Iterator<? extends Map.Entry<? extends K, ? extends V>> itr) {
        requireNonNull(itr);

        return new Iterator<V>() {
            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public V next() {
                return itr.next().getValue();
            }
        };
    }

    public static <T> Iterator<T> singletonIterator(T value) {
        return new Iterator<T>() {
            private boolean consumed;

            @Override
            public boolean hasNext() {
                return !consumed;
            }

            @Override
            public T next() {
                if (consumed) {
                    throw new NoSuchElementException();
                }
                consumed = true;
                return value;
            }
        };
    }

    private IteratorUtils() { /* Cannot be instantiated */ }
}
