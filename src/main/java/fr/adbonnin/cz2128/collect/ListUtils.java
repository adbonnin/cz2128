package fr.adbonnin.cz2128.collect;

import java.util.ArrayList;
import java.util.Iterator;

public class ListUtils {

    public static <E> ArrayList<E> newArrayList(Iterator<? extends E> iterator) {
        final ArrayList<E> list = new ArrayList<>();
        IteratorUtils.addAll(list, iterator);
        return list;
    }

    private ListUtils() { /* Cannot be instantiated */ }
}
