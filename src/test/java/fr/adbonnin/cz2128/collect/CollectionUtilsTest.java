package fr.adbonnin.cz2128.collect;

import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.*;

public class CollectionUtilsTest {

    @Test
    public void testAddAllWithNullParameters() {

        try {
            // when
            CollectionUtils.addAll(null, Collections.emptyIterator());

            //then
            fail();
        }
        catch (NullPointerException e) {
            // should throw exception
        }

        try {
            // when
            CollectionUtils.addAll(Collections.emptyList(), null);

            // then
            fail();
        }
        catch (NullPointerException e) {
            // should throw exception
        }
    }

    @Test
    public void testAddAll() {
        // given
        final List<String> list = new ArrayList<>();
        final List<String> excepted = Arrays.asList("A", "B");

        // when
        final boolean result = CollectionUtils.addAll(list, excepted.iterator());

        // then
        assertTrue(result);
        assertEquals(list, excepted);
    }

    @Test
    public void testMapAllToHashMapWithNullParameters() {

        try {
            // when
            CollectionUtils.mapAllToHashMap(null);

            // then
            fail();
        }
        catch (NullPointerException e) {
            // should throw exception
        }
    }

    @Test
    public void testMapAllToHashMap() {
        // given
        final List<String> list = Arrays.asList("A", "B");

        final Map<String, String> excepted = new HashMap<>();
        excepted.put("A", "A");
        excepted.put("B", "B");

        // when
        final Map<String, String> result = CollectionUtils.mapAllToHashMap(list);

        // then
        assertEquals(result, excepted);
    }
}
