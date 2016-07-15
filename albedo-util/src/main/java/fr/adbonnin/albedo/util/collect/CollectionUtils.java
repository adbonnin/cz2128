package fr.adbonnin.albedo.util.collect;

import java.util.*;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

public final class CollectionUtils {

    public static <T> boolean addAll(Collection<T> addTo, Iterator<? extends T> iterator) {
        requireNonNull(addTo);
        requireNonNull(iterator);

        boolean modified = false;
        while (iterator.hasNext()) {
            modified = addTo.add(iterator.next()) || modified;
        }

        return modified;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> ArrayList<T> newArrayList(Iterator<? extends T> iterator) {
        final ArrayList<T> list = new ArrayList<>();
        addAll(list, iterator);
        return list;
    }

    public static <T> HashSet<T> newHashSet(Iterator<? extends T> iterator) {
        final HashSet<T> set = new HashSet<>();
        addAll(set, iterator);
        return set;
    }

    public static <K, V> Map<K, V> asUnmodifiableMap(Map<? extends K, ? extends V> map) {
        return map == null || map.isEmpty() ? Collections.<K, V>emptyMap() : unmodifiableMap(map);
    }

    public static <T> Set<T> asUnmodifiableSet(Set<T> set) {
        return set == null || set.isEmpty() ? Collections.<T>emptySet() : unmodifiableSet(set);
    }

    public static <T> Iterable<T> asEmptyIterable(Iterable<T> iterable) {
        return iterable == null ? Collections.<T>emptyList() : iterable;
    }

    public static <T> List<T> asUnmodifiableList(final T... elements) {
        return elements == null ? Collections.<T>emptyList() : unmodifiableList(Arrays.asList(elements));
    }

    private CollectionUtils() { /* Cannot be instantiated */ }
}
