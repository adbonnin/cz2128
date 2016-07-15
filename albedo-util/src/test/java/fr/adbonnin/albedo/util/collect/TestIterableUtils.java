package fr.adbonnin.albedo.util.collect;

import java.util.Iterator;

public final class TestIterableUtils {

    @SuppressWarnings("unchked")
    public static <T> Iterable<T> unsupportedOperator() {
        return (Iterable<T>) ObjectIterable.UNSUPPORTED_OPERATION;
    }

    enum ObjectIterable implements Iterable<Object> {

        /** @see TestIterableUtils#unsupportedOperator() */
        UNSUPPORTED_OPERATION {

            @Override
            public Iterator<Object> iterator() {
                return TestIteratorUtils.ObjectIterator.UNSUPPORTED_OPERATION;
            }
        }
    }

    private TestIterableUtils() { /* Cannot be instantiated */ }
}
