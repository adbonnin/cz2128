package fr.adbonnin.albedo.util.collect.increment;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MapIncrement<Rev, K, V> {

    private final Rev revision;

    private final boolean full;

    private final Map<K, V> updated;

    private final Set<K> removed;

    MapIncrement(Rev revision, boolean full, Map<K, V> updated, Set<K> removed) {
        this.revision = revision;
        this.full = full;
        this.updated = updated;
        this.removed = removed;
    }

    public boolean full() {
        return full;
    }

    public Rev revision() {
        return revision;
    }

    public Set<K> removed() {
        return removed;
    }

    public Map<K, V> updated() {
        return updated;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof MapIncrement)) {
            return false;
        }

        final MapIncrement other = (MapIncrement) obj;
        return full == other.full() &&
            Objects.equals(revision, other.revision()) &&
            Objects.equals(updated, other.updated()) &&
            Objects.equals(removed, other.removed());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(revision);
        result = 31 * result + (full ? 1 : 0);
        result = 31 * result + Objects.hashCode(updated);
        result = 31 * result + Objects.hashCode(removed);
        return result;
    }

    @Override
    public String toString() {
        return revision + "= full: " + full + "; updated: " + updated + "; removed: " + removed;
    }
}
