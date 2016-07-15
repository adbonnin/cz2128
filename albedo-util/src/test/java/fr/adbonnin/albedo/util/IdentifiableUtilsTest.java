package fr.adbonnin.albedo.util;

import fr.adbonnin.albedo.util.Identifiable;
import fr.adbonnin.albedo.util.Predicate;
import fr.adbonnin.albedo.util.io.serializer.IdentifiableA;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static fr.adbonnin.albedo.util.IdentifiableUtils.containsIdsPredicate;
import static fr.adbonnin.albedo.util.IdentifiableUtils.equalsIdPredicate;
import static fr.adbonnin.albedo.util.IdentifiableUtils.toIdIterator;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class IdentifiableUtilsTest {

    @Test
    public void testToIdIterator() throws Exception {

        final List<IdentifiableA> identifiables = asList(
            new IdentifiableA("A"),
            new IdentifiableA("B")
        );

        final Iterator<Object> itr = toIdIterator(identifiables.iterator());

        assertTrue(itr.hasNext());
        assertEquals("A", itr.next());

        assertTrue(itr.hasNext());
        assertEquals("B", itr.next());

        assertFalse(itr.hasNext());
    }

    @Test
    public void testEqualsIdPredicate() throws Exception {
        final Predicate<Identifiable> predicate = equalsIdPredicate("A");
        assertTrue(predicate.evaluate(new IdentifiableA("A")));
        assertFalse(predicate.evaluate(new IdentifiableA("B")));
    }

    @Test
    public void testContainsIdsPredicate() throws Exception {
        final Predicate<Identifiable> predicate = containsIdsPredicate(asList("A", "B").iterator());
        assertTrue(predicate.evaluate(new IdentifiableA("A")));
        assertTrue(predicate.evaluate(new IdentifiableA("B")));
        assertFalse(predicate.evaluate(new IdentifiableA("C")));
    }
}
