package fr.adbonnin.albedo.util.collect;

import java.util.Iterator;

public class TestIteratorUtils {

    @SuppressWarnings("unchecked")
    public static <T> Iterator<T> unsupportedOperation() {
        return (Iterator<T>) ObjectIterator.UNSUPPORTED_OPERATION;
    }

    enum ObjectIterator implements Iterator<Object> {

        UNSUPPORTED_OPERATION {

            @Override
            public boolean hasNext() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Object next() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }

    private TestIteratorUtils() { /* Cannot be instanciated */ }
}
