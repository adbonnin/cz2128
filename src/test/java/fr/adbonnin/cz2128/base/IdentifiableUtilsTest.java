package fr.adbonnin.cz2128.base;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.testng.Assert.*;

public class IdentifiableUtilsTest {

    @Test
    public void testToIdIterator() {

        try {
            IdentifiableUtils.toIdIterator(null);
            fail();
        }
        catch (NullPointerException e) {
            // should throw exception
        }

        final Iterator<Object> itr = IdentifiableUtils.toIdIterator(asList(
            new FooIdentifiable("A"),
            new FooIdentifiable("B")).iterator());

        assertTrue(itr.hasNext());
        assertEquals(itr.next(), "A");

        assertTrue(itr.hasNext());
        assertEquals(itr.next(), "B");

        assertFalse(itr.hasNext());
        try {
            itr.next();
            fail();
        }
        catch (NoSuchElementException e) {
            // should throw exception
        }
    }

    @Test
    public void testEqualsIdPredicate() {
        final Predicate<Identifiable> predicate = IdentifiableUtils.equalsIdPredicate("A");
        assertFalse(predicate.evaluate(null));
        assertTrue(predicate.evaluate(new FooIdentifiable("A")));
        assertFalse(predicate.evaluate(new FooIdentifiable("B")));
    }

    @Test
    public void testContainsIdsPredicate() {

        try {
            IdentifiableUtils.containsIdsPredicate(null);
            fail();
        }
        catch (NullPointerException e) {
            // should throw exception
        }

        final Predicate<Identifiable> predicate = IdentifiableUtils.containsIdsPredicate(singletonList("A").iterator());
        assertFalse(predicate.evaluate(null));
        assertFalse(predicate.evaluate(new FooIdentifiable("B")));
        assertTrue(predicate.evaluate(new FooIdentifiable("A")));
    }

    @Test
    public void testIndexByIds() throws Exception {

        try {
            IdentifiableUtils.indexByIds(null);
            fail();
        }
        catch (NullPointerException e) {
            // should throw exception
        }

        final FooIdentifiable idA = new FooIdentifiable("A");
        final FooIdentifiable idB = new FooIdentifiable("B");

        final Map<Object, FooIdentifiable> map = IdentifiableUtils.indexByIds(asList(idA, idB));
        assertEquals(map.size(), 2);
        assertEquals(map.get("A"), idA);
        assertEquals(map.get("B"), idB);
    }
}
