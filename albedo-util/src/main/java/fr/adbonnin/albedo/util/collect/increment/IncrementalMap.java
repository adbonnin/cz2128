package fr.adbonnin.albedo.util.collect.increment;

import fr.adbonnin.albedo.util.Equivalence;
import fr.adbonnin.albedo.util.collect.LinkedHashLine;

import java.util.*;

import static fr.adbonnin.albedo.util.collect.increment.IncrementUtils.*;
import static java.util.Objects.requireNonNull;

public class IncrementalMap<Rev, K, V> {

    private final LinkedHashLine<Rev, MapIncrement<Rev, K, V>> increments;

    private final Equivalence<V> equivalence;

    private volatile Rev currentRevision;

    private volatile Map<K, V> currentValues;

    public IncrementalMap(Rev firstRevision, Map<K, V> values, int maxSize, Equivalence<V> equivalence) {
        requireNonNull(values);
        this.equivalence = requireNonNull(equivalence);

        this.currentRevision = firstRevision;
        this.currentValues = new HashMap<>(values);

        this.increments = new LinkedHashLine<>(maxSize);
        this.increments.add(firstRevision, newFullMapIncrement(firstRevision, values));
    }

    public IncrementalMap(Rev firstRevision, int maxSize, Equivalence<V> equivalence) {
        this(firstRevision, Collections.<K, V>emptyMap(), maxSize, equivalence);
    }

    public boolean set(Rev revision, Map<K, V> values) {

        if (values == null) {
            return false;
        }

        final Map<K, V> created = new HashMap<>(values);
        final Map<K, V> updated = new HashMap<>();
        final Set<K> removed = new HashSet<>();

        for (Map.Entry<K, V> etr : currentValues.entrySet()) {
            final K key = etr.getKey();
            final V oldValue = etr.getValue();

            if (created.containsKey(key)) {
                final V newValue = created.remove(key);
                if (!equivalence.equivalent(oldValue, newValue)) {
                    updated.put(key, newValue);
                }
            }
            else {
                removed.add(key);
            }
        }

        updated.putAll(created);

        final MapIncrement<Rev, K, V> increment = newPartialMapIncrement(revision, updated, removed);
        if (!increments.add(revision, increment)) {
            return false;
        }

        this.currentRevision = revision;
        this.currentValues = new HashMap<>(values);
        return true;
    }

    public MapIncrement<Rev, K, V> full() {
        final Map<K, V> full = new HashMap<>(currentValues);
        return newFullMapIncrement(currentRevision, full);
    }

    public MapIncrement<Rev, K, V> from(Rev key) {
        final Iterator<MapIncrement<Rev, K, V>> itr = increments.values(key);
        return itr.hasNext() ? concatMapIncrements(itr) : full();
    }
}
