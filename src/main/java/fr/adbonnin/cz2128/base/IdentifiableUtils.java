package fr.adbonnin.cz2128.base;

import java.util.*;

import static fr.adbonnin.cz2128.collect.CollectionUtils.newHashSet;
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
                return identifiables.next().id();
            }

            @Override
            public void remove() {
                identifiables.remove();
            }
        };
    }

    public static <E extends Identifiable> Predicate<E> equalsIdPredicate(final Object id) {
        return new Predicate<E>() {

            @Override
            public boolean evaluate(E value) {
                return value != null && Objects.equals(id, value.id());
            }
        };
    }

    public static <E extends Identifiable> Predicate<E> containsIdsPredicate(Iterator<?> ids) {
        final Set<Object> idSet = newHashSet(ids);
        return new Predicate<E>() {

            @Override
            public boolean evaluate(E value) {
                return value != null && idSet.contains(value.id());
            }
        };
    }

    public static <E extends Identifiable> Map<Object, E> indexByIds(Iterable<E> entities) {

        final Map<Object, E> result = new LinkedHashMap<>();
        for (E entity : entities) {
            result.put(entity.id(), entity);
        }

        return result;
    }

    private IdentifiableUtils() { /* Cannot be instantiated */}
}
