package fr.adbonnin.cz2128.collect;

import fr.adbonnin.cz2128.base.Predicate;
import fr.adbonnin.cz2128.base.PredicateUtils;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static org.testng.Assert.*;

public class IteratorUtilsTest {

    @Test
    public void testCount() {

        try {
            IteratorUtils.count(null);
            fail();
        }
        catch (NullPointerException e) {
            // should throw exception
        }

        assertEquals(IteratorUtils.count(asList("A", "B").iterator()), 2);
    }

    @Test
    public void testFilter() {

        try {
            IteratorUtils.filter(null, PredicateUtils.alwaysTrue());
        }
        catch (NullPointerException e) {
            // should throw exception
        }

        try {
            IteratorUtils.filter(emptyList().iterator(), null);
        }
        catch (NullPointerException e) {
            // should throw exception
        }

        final Iterator<String> filtered = IteratorUtils.filter(asList("A", "B", "C").iterator(), new Predicate<String>() {
            @Override
            public boolean evaluate(String value) {
                return !"B".equals(value);
            }
        });

        assertTrue(filtered.hasNext());
        assertEquals(filtered.next(), "A");

        assertTrue(filtered.hasNext());
        assertEquals(filtered.next(), "C");

        assertFalse(filtered.hasNext());
        try {
            filtered.next();
            fail();
        }
        catch (NoSuchElementException e) {
            // should throw exception
        }
    }

    @Test
    public void testNext() {

        try {
            IteratorUtils.next(null, null);
            fail();
        }
        catch (NullPointerException e) {
            // should throw exception
        }

        assertEquals(IteratorUtils.next(singleton("A").iterator(), "B"), "A");
        assertEquals(IteratorUtils.next(emptyList().iterator(), "B"), "B");
    }
}
