package fr.adbonnin.albedo.util.collect.increment;

import fr.adbonnin.albedo.util.collect.LinkedHashLine;

import java.util.Iterator;

import static fr.adbonnin.albedo.util.collect.increment.IncrementUtils.concatIterableIncrements;
import static fr.adbonnin.albedo.util.collect.increment.IncrementUtils.newEmptyIterableIncrement;
import static fr.adbonnin.albedo.util.collect.increment.IncrementUtils.newIterableIncrement;

public class IncrementalIterable<Rev, T> {

    private final LinkedHashLine<Rev, IterableIncrement<Rev, T>> increments;

    private volatile Rev currentRevision;

    public IncrementalIterable(Rev firstRevision, int maxSize) {
        this.currentRevision = firstRevision;
        this.increments = new LinkedHashLine<>(maxSize);
        this.increments.add(firstRevision, IncrementUtils.<Rev, T>newEmptyIterableIncrement(firstRevision));
    }

    public boolean add(Rev revision, Iterable<T> values) {

        if (values == null) {
            return false;
        }

        if (!increments.add(revision, newIterableIncrement(revision, values))) {
            return false;
        }

        this.currentRevision = revision;
        return true;
    }

    public IterableIncrement<Rev, T> last() {
        return newEmptyIterableIncrement(currentRevision);
    }

    public IterableIncrement<Rev, T> from(Rev revision) {
        final Iterator<IterableIncrement<Rev, T>> itr = increments.values(revision);
        return itr.hasNext() ? concatIterableIncrements(itr) : last();
    }
}
