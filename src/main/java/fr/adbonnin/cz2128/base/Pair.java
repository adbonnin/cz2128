package fr.adbonnin.cz2128.base;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class Pair<K, V> implements Map.Entry<K, V> {

    private final K key;

    private final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    public <R> Pair<R, V> mapKey(Function<? super K, ? extends R> function) {
        return Pair.of(function.apply(key), value);
    }

    public <R> Pair<K, R> mapValue(Function<? super V, ? extends R> function) {
        return Pair.of(key, function.apply(value));
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Map.Entry)) {
            return false;
        }

        final Map.Entry that = (Map.Entry) obj;
        return Objects.equals(key, that.getKey()) &&
            Objects.equals(value, that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key) * 31 + Objects.hashCode(value);
    }

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }
}
