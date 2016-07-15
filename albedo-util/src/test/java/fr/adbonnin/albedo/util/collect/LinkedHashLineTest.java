package fr.adbonnin.albedo.util.collect;

import org.junit.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.*;

public class LinkedHashLineTest {

    private final SimpleEntry<String, Integer> a = new SimpleEntry<>("a", 1);
    private final SimpleEntry<String, Integer> b = new SimpleEntry<>("b", 2);
    private final SimpleEntry<String, Integer> c = new SimpleEntry<>("c", 3);

    @Test
    public void test() {

        final LinkedHashLine<String, Integer> line = new LinkedHashLine<>(2);
        assertEquals(2, line.maxSize());

        // Test add a->1
        assertTrue(line.add("a", 1));
        assertEquals(1, line.size());

        // Test entries from unknown
        final Iterator<Map.Entry<String, Integer>> fromUnknown1 = line.entries("unknown");
        assertFalse(fromUnknown1.hasNext());

        // Test entries from a
        final Iterator<Map.Entry<String, Integer>> fromA1 = line.entries("a");
        assertTrue(fromA1.hasNext());

        final Map.Entry<String, Integer> nextA1 = fromA1.next();
        assertTrue(a.equals(nextA1));
        assertTrue(nextA1.equals(a));
        assertEquals(a.hashCode(), nextA1.hashCode());
        assertFalse(fromA1.hasNext());

        // Test entries from tail
        final Iterator<Map.Entry<String, Integer>> fromStart1 = line.entries();
        assertTrue(fromStart1.hasNext());
        assertEquals(a, fromStart1.next());
        assertFalse(fromStart1.hasNext());



        // (*1) Test entries after add
        final Iterator<Map.Entry<String, Integer>> fromStart2 = line.entries();

        // Test add b->2
        assertTrue(line.add("b", 2));
        assertEquals(2, line.size());

        // (*1) ...
        assertTrue(fromStart2.hasNext());
        assertEquals(a, fromStart2.next()); // next is defined after consuming element
        assertTrue(fromStart2.hasNext());
        assertEquals(b, fromStart2.next());
        assertFalse(fromStart2.hasNext());

        // Test entries from a
        final Iterator<Map.Entry<String, Integer>> fromA2 = line.entries("a");
        assertTrue(fromA2.hasNext());
        assertEquals(a, fromA2.next());
        assertTrue(fromA2.hasNext());
        assertEquals(b, fromA2.next());
        assertFalse(fromA2.hasNext());

        // Test values from start
        final Iterator<Integer> fromA6 = line.values();
        assertTrue(fromA6.hasNext());
        assertEquals(new Integer(1), fromA6.next());
        assertTrue(fromA6.hasNext());
        assertEquals(new Integer(2), fromA6.next());
        assertFalse(fromA6.hasNext());

        // Test values from a
        final Iterator<Integer> fromA5 = line.values("a");
        assertTrue(fromA5.hasNext());
        assertEquals(new Integer(1), fromA5.next());
        assertTrue(fromA5.hasNext());
        assertEquals(new Integer(2), fromA5.next());
        assertFalse(fromA5.hasNext());



        // (*2) Test entries after removing
        final Iterator<Map.Entry<String, Integer>> fromA3 = line.entries("a");

        // Test add c->3
        assertTrue(line.add("c", 3));
        assertEquals(2, line.size());

        // (*2) ...
        assertTrue(fromA3.hasNext());
        assertTrue(fromA3.hasNext());
        assertEquals(fromA3.next(), a);
        assertTrue(fromA3.hasNext());
        assertEquals(fromA3.next(), b);
        assertTrue(fromA3.hasNext());
        assertEquals(fromA3.next(), c);
        assertFalse(fromA3.hasNext());

        final Iterator<Map.Entry<String, Integer>> fromA4 = line.entries("a");
        assertFalse(fromA4.hasNext());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeMaxSize() {
        new LinkedHashLine(-1);
    }

    @Test
    public void testZeroMaxSize() {
        final LinkedHashLine<String, Integer> line = new LinkedHashLine<>(0);
        assertEquals(0, line.maxSize());

        // Test add a->1
        assertTrue(line.add("a", 1));
        assertEquals(0, line.size());

        final Iterator<Map.Entry<String, Integer>> fromA1 = line.entries("a");
        assertFalse(fromA1.hasNext());
    }

    @Test
    public void testOneMaxSize() {
        final LinkedHashLine<String, Integer> line = new LinkedHashLine<>(1);
        assertEquals(1, line.maxSize());

        // Test add a->1
        assertTrue(line.add("a", 1));
        assertEquals(1, line.size());

        // Test entries from a
        final Iterator<Map.Entry<String, Integer>> fromA1 = line.entries("a");
        assertTrue(fromA1.hasNext());
        assertEquals(a, fromA1.next());
        assertFalse(fromA1.hasNext());

        // (*1) Test entries after add
        final Iterator<Map.Entry<String, Integer>> fromStart2 = line.entries();

        // Test add b->2
        assertTrue(line.add("b", 2));
        assertEquals(1, line.size());

        // (*1) ...
        assertTrue(fromStart2.hasNext());
        assertEquals(a, fromStart2.next()); // next is defined after consuming element
        assertTrue(fromStart2.hasNext());
        assertEquals(b, fromStart2.next());
        assertFalse(fromStart2.hasNext());
    }
}
