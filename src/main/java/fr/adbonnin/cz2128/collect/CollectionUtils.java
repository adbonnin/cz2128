package fr.adbonnin.cz2128.collect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import static java.util.Objects.requireNonNull;

public final class CollectionUtils {

    public static <T> ArrayList<T> newArrayList(Iterator<? extends T> iterator) {
        final ArrayList<T> list = new ArrayList<>();
        CollectionUtils.addAll(list, iterator);
        return list;
    }

    public static <T> HashSet<T> newHashSet(Iterator<? extends T> iterator) {
        final HashSet<T> set = new HashSet<>();
        CollectionUtils.addAll(set, iterator);
        return set;
    }

    public static <T> boolean addAll(Collection<T> addTo, Iterator<? extends T> iterator) {
        requireNonNull(addTo);

        boolean modified = false;
        while (iterator.hasNext()) {
            modified |= addTo.add(iterator.next());
        }

        return modified;
    }

    private CollectionUtils() { /* Cannot be instantiated */ }
}
