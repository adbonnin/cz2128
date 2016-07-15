package fr.adbonnin.albedo.util.collect;

import java.util.*;

public class LinkedHashLine<K, V> {

    private final Map<K, LineEntry<K, V>> values = new HashMap<>();

    private final int maxSize;

    private volatile LineEntry<K, V> head;
    private volatile LineEntry<K, V> tail;

    public LinkedHashLine(int maxSize) {

        if (maxSize < 0) {
            throw new IllegalArgumentException("max size must be positive");
        }

        this.maxSize = maxSize;
    }

    public int size() {
        return values.size();
    }

    public int maxSize() {
        return maxSize;
    }

    public boolean add(K key, V value) {

        if (values.containsKey(key)) {
            return false;
        }

        final LineEntry<K, V> oldHead = head;
        final LineEntry<K, V> newHead = new LineEntry<>(key, value);
        values.put(key, newHead);

        if (oldHead != null) {
            oldHead.next(newHead);
        }

        if (tail == null) {
            tail = newHead;
        }

        if (values.size() > maxSize) {
            values.remove(tail.getKey());
            tail = tail.next();
        }

        head = newHead;
        return true;
    }

    public Iterator<Map.Entry<K, V>> entries(K from) {
        final LineEntry<K, V> first = values.get(from);
        return new Iterator<Map.Entry<K, V>>() {

            private LineEntry<K, V> next = first;

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public Map.Entry<K, V> next() {

                if (next == null) {
                    throw new NoSuchElementException();
                }

                final LineEntry<K, V> current = next;
                next = current.next();
                return current;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public Iterator<Map.Entry<K, V>> entries() {
        return tail == null ? IteratorUtils.<Map.Entry<K, V>>emptyIterator() : entries(tail.getKey());
    }

    public Iterator<V> values(K from) {
        return asValuesIterator(entries(from));
    }

    public Iterator<V> values() {
        return asValuesIterator(entries());
    }

    public Iterator<V> asValuesIterator(final Iterator<Map.Entry<K, V>> iterator) {
        return new Iterator<V>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public V next() {
                final Map.Entry<K, V> next = iterator.next();
                return next == null ? null : next.getValue();
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

    private static class LineEntry<K, V> implements Map.Entry<K, V> {

        private final K key;

        private final V value;

        private LineEntry<K, V> next;

        public LineEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        public LineEntry<K, V> next() {
            return next;
        }

        public void next(LineEntry<K, V> next) {
            this.next = next;
        }

        @Override
        public boolean equals(Object obj) {

            if (!(obj instanceof Map.Entry)) {
                return false;
            }

            final Map.Entry<?,?> other = (Map.Entry<?,?>) obj;
            return Objects.equals(key, other.getKey())
                && Objects.equals(value, other.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }
}
