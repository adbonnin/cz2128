package fr.adbonnin.albedo.util.collect.increment;

import org.junit.Test;

import static fr.adbonnin.albedo.util.collect.IterableUtils.equal;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

public class IncrementalIterableTest {

    @Test
    public void test() throws Exception {
        final IncrementalIterable<String, Integer> inc = new IncrementalIterable<>("a", 3);
        final IterableIncrement<String, Integer> fromA1 = inc.from("a");
        assertEquals("a", fromA1.revision());
        assertTrue(equal(emptyList(), fromA1.updated()));

        // Test add existing revision
        assertFalse(inc.add("a", singletonList(1)));

        // Test add revision
        inc.add("b", singletonList(1));
        inc.add("c", asList(2, 3));

        final IterableIncrement<String, Integer> fromA2 = inc.from("a");
        assertEquals("c", fromA2.revision());
        assertTrue(equal(asList(1, 2, 3), fromA2.updated()));

        final IterableIncrement<String, Integer> fromB1 = inc.from("b");
        assertEquals("c", fromB1.revision());
        assertTrue(equal(asList(2, 3), fromB1.updated()));

        final IterableIncrement<String, Integer> fromC1 = inc.from("c");
        assertEquals("c", fromC1.revision());
        assertTrue(equal(emptyList(), fromC1.updated()));

        // Test add max revision
        inc.add("d", asList(3, 4));

        final IterableIncrement<String, Integer> fromA3 = inc.from("a");
        assertEquals("d", fromA3.revision());
        assertTrue(equal(emptyList(), fromA3.updated()));

        final IterableIncrement<String, Integer> fromB2 = inc.from("b");
        assertEquals("d", fromB2.revision());
        assertTrue(equal(asList(2, 3, 3, 4), fromB2.updated()));

        final IterableIncrement<String, Integer> fromC2 = inc.from("c");
        assertEquals("d", fromC2.revision());
        assertTrue(equal(asList(3, 4), fromC2.updated()));

        final IterableIncrement<String, Integer> fromD1 = inc.from("d");
        assertEquals("d", fromD1.revision());
        assertTrue(equal(emptyList(), fromD1.updated()));

        // Test from unknown
        final IterableIncrement<String, Integer> fromUnknown = inc.from("unknown");
        assertEquals("d", fromUnknown.revision());
        assertTrue(equal(emptyList(), fromUnknown.updated()));
    }
}
