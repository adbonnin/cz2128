package fr.adbonnin.albedo.util.collect;

import java.util.*;

import static fr.adbonnin.albedo.util.collect.IteratorUtils.next;

public class IterableHashMap<K, V> implements IterableMap<K, V> {

    private final Map<K, List<V>> data = new HashMap<>();

    @Override
    public Set<K> keys() {
        return data.keySet();
    }

    @Override
    public Iterator<V> values(K key) {
        final Iterable<V> values = data.get(key);
        return values == null ? Collections.<V>emptyIterator() : values.iterator();
    }

    @Override
    public V first(K key) {
        return values(key).next();
    }

    @Override
    public V first(K key, V defaultValue) {
        return next(values(key), defaultValue);
    }

    @Override
    public boolean empty() {
        return data.isEmpty();
    }
}
