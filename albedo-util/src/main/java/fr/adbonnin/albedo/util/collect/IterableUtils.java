package fr.adbonnin.albedo.util.collect;

import java.util.Iterator;

import static java.util.Objects.requireNonNull;

public final class IterableUtils {

    private static <T> Iterator<Iterator<? extends T>> iterators(final Iterable<? extends Iterable<? extends T>> iterables) {
        requireNonNull(iterables);

        final Iterator<? extends Iterable<? extends T>> itr = iterables.iterator();
        return new AbstractIterator<Iterator<? extends T>>() {

            @Override
            protected Iterator<? extends T> computeNext() {
                return itr.hasNext() ? itr.next().iterator() : this.<Iterator<? extends T>>endOfData();
            }
        };
    }

    public static <T> Iterable<T> concat(final Iterable<? extends Iterable<? extends T>> iterables) {
        requireNonNull(iterables);
        return new Iterable<T>() {

            @Override
            public Iterator<T> iterator() {
                return IteratorUtils.concat(iterators(iterables));
            }
        };
    }

    public static boolean equal(Iterable<?> iterable1, Iterable<?> iterable2) {

        if (iterable1 == iterable2) {
            return true;
        }

        if (iterable1 == null || iterable2 == null) {
            return false;
        }

        return IteratorUtils.equal(iterable1.iterator(), iterable2.iterator());
    }

    private IterableUtils() { /* Cannot be instantiated */ }
}
