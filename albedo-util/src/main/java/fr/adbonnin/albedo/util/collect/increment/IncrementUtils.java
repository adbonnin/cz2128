package fr.adbonnin.albedo.util.collect.increment;

import java.util.*;

import static fr.adbonnin.albedo.util.collect.CollectionUtils.*;
import static fr.adbonnin.albedo.util.collect.IterableUtils.concat;

public final class IncrementUtils {

    public static <Rev, K, V> MapIncrement<Rev, K, V> newFullMapIncrement(Rev revision, Map<K, V> updated) {
        return newMapIncrement(revision, true, updated, null);
    }

    public static <Rev, K, V> MapIncrement<Rev, K, V> newPartialMapIncrement(Rev revision, Map<K, V> updated, Set<K> removed) {
        return newMapIncrement(revision, false, updated, removed);
    }

    public static <Rev, K, V> MapIncrement<Rev, K, V> newMapIncrement(Rev revision, boolean full, Map<K, V> updated, Set<K> removed) {
        return new MapIncrement<>(revision, full, asUnmodifiableMap(updated), asUnmodifiableSet(removed));
    }

    public static <Rev, K, V> MapIncrement<Rev, K, V> concatMapIncrements(Iterator<MapIncrement<Rev, K, V>> iterator) {

        if (iterator == null) {
            throw new IllegalArgumentException("iterator == null");
        }

        if (!iterator.hasNext()) {
            throw new IllegalStateException("iterator must have at least one iteration otherwise revision cannot be found");
        }

        Rev revision = null;
        boolean full = false;
        Map<K, V> updated = new HashMap<>();
        Set<K> removed = new HashSet<>();
        boolean first = true;

        while (iterator.hasNext()) {
            final MapIncrement<Rev, K, V> increment = iterator.next();
            revision = increment.revision();

            if (first) {
                first = false;
            }
            else {
                if (increment.full()) {
                    full = true;
                    updated = new HashMap<>(increment.updated());
                    removed = new HashSet<>();
                }
                else {
                    for (Map.Entry<K, V> up : increment.updated().entrySet()) {
                        final K key = up.getKey();
                        updated.put(key, up.getValue());
                        removed.remove(key);
                    }

                    for (K rm : increment.removed()) {
                        updated.remove(rm);
                        removed.add(rm);
                    }
                }
            }
        }

        return newMapIncrement(revision, full, updated, removed);
    }

    public static <Rev, T> IterableIncrement<Rev, T> newEmptyIterableIncrement(Rev revision) {
        return newIterableIncrement(revision, null);
    }

    public static <Rev, T> IterableIncrement<Rev, T> newIterableIncrement(Rev revision, Iterable<T> updated) {
        return new IterableIncrement<>(revision, asEmptyIterable(updated));
    }

    public static <Rev, T> IterableIncrement<Rev, T> concatIterableIncrements(Iterator<IterableIncrement<Rev, T>> iterator) {

        if (iterator == null) {
            throw new IllegalArgumentException("iterator == null");
        }

        if (!iterator.hasNext()) {
            throw new IllegalArgumentException("iterator must have at least one iteration otherwise revision cannot be found");
        }

        final List<Iterable<T>> iterables = new ArrayList<>();

        Rev revision = null;
        boolean first = true;

        while (iterator.hasNext()) {
            final IterableIncrement<Rev, T> increment = iterator.next();
            revision = increment.revision();

            if (first) {
                first = false;
            }
            else {
                iterables.add(increment.updated());
            }
        }

        return newIterableIncrement(revision, concat(iterables));
    }

    private IncrementUtils() { /* Cannot be instantiated */ }
}
