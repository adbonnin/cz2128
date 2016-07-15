package fr.adbonnin.albedo.util;

import java.util.*;

import static fr.adbonnin.albedo.util.collect.CollectionUtils.newHashSet;
import static java.util.Objects.requireNonNull;

public final class IdentifiableUtils {

    public static Iterator<Object> toIdIterator(final Iterator<? extends Identifiable> identifiables) {
        requireNonNull(identifiables);
        return new Iterator<Object>() {

            @Override
            public boolean hasNext() {
                return identifiables.hasNext();
            }

            @Override
            public Object next() {
                final Identifiable next = identifiables.next();
                return requireNonNull(next).id();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static <T extends Identifiable> Predicate<T> equalsIdPredicate(final Object id) {
        return new Predicate<T>() {

            @Override
            public boolean evaluate(T input) {
                return input != null && Objects.equals(id, input.id());
            }
        };
    }

    public static <T extends Identifiable> Predicate<T> equalsIdPredicate(T entity) {
        return equalsIdPredicate(entity.id());
    }

    public static <T extends Identifiable> Predicate<T> containsIdsPredicate(Iterator<?> ids) {
        final Set<Object> idSet = newHashSet(ids);
        return new Predicate<T>() {

            @Override
            public boolean evaluate(T input) {
                return input != null && idSet.contains(input.id());
            }
        };
    }

    public static <T extends Identifiable> Map<Object, T> indexByIds(Iterable<T> entities) {
        requireNonNull(entities);

        final Map<Object, T> result = new HashMap<>();
        for (T entity : entities) {
            result.put(entity.id(), requireNonNull(entity));
        }

        return result;
    }

    private IdentifiableUtils() { /* Cannot be instantiated */ }
}
