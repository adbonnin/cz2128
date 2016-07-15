package fr.adbonnin.albedo.util.collect;

import org.junit.Test;

import java.util.*;

import static fr.adbonnin.albedo.util.collect.IterableUtils.equal;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IterableUtilsTest {

    @Test
    public void testEqual() {
        final Iterable<Object> iterable = TestIterableUtils.unsupportedOperator();
        assertTrue(equal(null, null));
        assertFalse(equal(iterable, null));
        assertFalse(equal(null, iterable));
        assertTrue(equal(iterable, iterable));

        final List<Integer> list1 = asList(1, 2, 3);
        final List<Integer> list2 = asList(1, 2, 3);
        final List<Integer> list3 = asList(1, 2);

        assertTrue(equal(list1, list2));
        assertFalse(equal(list2, list3));
        assertFalse(equal(list3, list2));
    }

    @Test
    public void testConcat() {
        Iterable<String> iterable = IterableUtils.concat(asList(singletonList("test1"), singletonList("test2")));
        Iterator<String> itr = iterable.iterator();

        assertTrue(itr.hasNext());
        assertEquals("test1", itr.next());
        assertTrue(itr.hasNext());
        assertEquals("test2", itr.next());
        assertFalse(itr.hasNext());
    }
}
