package fr.adbonnin.cz2128.collect;

import java.util.*;

import static java.util.Objects.requireNonNull;

public final class CollectionUtils {

    public static <T> ArrayList<T> newArrayList(Iterator<? extends T> iterator) {
        final ArrayList<T> list = new ArrayList<>();
        CollectionUtils.addAll(list, iterator);
        return list;
    }

    public static <T> boolean addAll(Collection<T> addTo, Iterator<? extends T> iterator) {
        requireNonNull(addTo);

        boolean modified = false;
        while (iterator.hasNext()) {
            modified |= addTo.add(iterator.next());
        }

        return modified;
    }

    public static <T> Map<T, T> mapAllToHashMap(Iterable<? extends T> elements) {
        final Map<T, T> map = new HashMap<>();
        for (T element : elements) {
            map.put(element, element);
        }

        return map;
    }

    private CollectionUtils() { /* Cannot be instantiated */ }
}
