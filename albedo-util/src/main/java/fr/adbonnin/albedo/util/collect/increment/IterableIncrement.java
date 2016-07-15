package fr.adbonnin.albedo.util.collect.increment;

import fr.adbonnin.albedo.util.collect.IterableUtils;

import java.util.Objects;

public class IterableIncrement<Rev, T> {

    private final Rev revision;

    private final Iterable<T> updated;

    public IterableIncrement(Rev revision, Iterable<T> updated) {
        this.revision = revision;
        this.updated = updated;
    }

    public Rev revision() {
        return revision;
    }

    public Iterable<T> updated() {
        return updated;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof IterableIncrement)) {
            return false;
        }

        final IterableIncrement other = (IterableIncrement) obj;
        return Objects.equals(revision, other.revision()) &&
               IterableUtils.equal(updated, other.updated());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(revision) ^ Objects.hashCode(updated);
    }

    @Override
    public String toString() {
        return revision + "=" + updated;
    }
}
