package fr.adbonnin.albedo.util.collect;

import java.util.Iterator;
import java.util.Set;

public interface UnmodifiableIterableMap<K, V> {

    Set<K> keys();

    Iterator<V> values(K key);

    V first(K key);

    V first(K key, V defaultValue);

    boolean empty();
}
