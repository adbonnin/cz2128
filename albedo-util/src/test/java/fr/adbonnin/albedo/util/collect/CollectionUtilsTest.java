package fr.adbonnin.albedo.util.collect;

import org.junit.Test;

import java.util.*;

import static fr.adbonnin.albedo.util.collect.IteratorUtils.asIterator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CollectionUtilsTest {

    @Test
    public void testIsEmpty() {
        assertTrue(CollectionUtils.isEmpty(null));
        assertTrue(CollectionUtils.isEmpty(new ArrayList<>()));
        assertFalse(CollectionUtils.isEmpty(Collections.singleton(1)));
    }

    @Test
    public void testAddAll() {
        final ArrayList<Object> list = new ArrayList<>();
        CollectionUtils.addAll(list, asIterator("test1", "test2"));
        assertEquals(2, list.size());
        assertEquals("test1", list.get(0));
        assertEquals("test2", list.get(1));
    }
}
