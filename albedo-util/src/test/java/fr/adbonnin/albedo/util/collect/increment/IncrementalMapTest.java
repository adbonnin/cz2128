package fr.adbonnin.albedo.util.collect.increment;

import fr.adbonnin.albedo.util.EquivalenceUtils;
import org.junit.Test;

import java.util.*;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static org.junit.Assert.*;

public class IncrementalMapTest {

    @Test
    public void test() {
        final Map<String, Integer> r1 = new HashMap<>();
        r1.put("a", 1);

        final IncrementalMap<String, String, Integer> inc = new IncrementalMap<>("r1", r1, 3, EquivalenceUtils.<Integer>haveSameReference());

        // Test full
        final MapIncrement<String, String, Integer> fromFull1 = inc.full();
        assertEquals("r1", fromFull1.revision());
        assertTrue(fromFull1.full());
        assertEquals(r1, fromFull1.updated());
        assertEquals(emptySet(), fromFull1.removed());

        // Test from
        final MapIncrement<String, String, Integer> fromR1_1 = inc.from("r1");
        assertEquals("r1", fromR1_1.revision());
        assertFalse(fromR1_1.full());
        assertEquals(emptyMap(), fromR1_1.updated());
        assertEquals(emptySet(), fromR1_1.removed());


        // Test add existing revision
        assertFalse(inc.set("r1", new HashMap<String, Integer>()));


        // Test add revision with create
        final Map<String, Integer> r2 = new HashMap<>();
        r2.put("a", 1);
        r2.put("b", 2);
        inc.set("r2", r2);

        // Test full
        final MapIncrement<String, String, Integer> fromFull2 = inc.full();
        assertEquals("r2", fromFull2.revision());
        assertTrue(fromFull2.full());
        assertEquals(r2, fromFull2.updated());
        assertEquals(emptySet(), fromFull2.removed());

        // Test from r1
        final Map<String, Integer> r1_2 = new HashMap<>();
        r1_2.put("b", 2);

        final MapIncrement<String, String, Integer> fromR1_2 = inc.from("r1");
        assertEquals("r2", fromR1_2.revision());
        assertFalse(fromR1_2.full());
        assertEquals(r1_2, fromR1_2.updated());
        assertEquals(emptySet(), fromR1_2.removed());


        // Test remove element
        final Map<String, Integer> r3 = new HashMap<>();
        r3.put("a", 1);
        inc.set("r3", r3);

        // Test full
        final MapIncrement<String, String, Integer> fromFull3 = inc.full();
        assertEquals("r3", fromFull3.revision());
        assertTrue(fromFull3.full());
        assertEquals(r3, fromFull3.updated());
        assertEquals(emptySet(), fromFull3.removed());

        // Test from r1
        final Set<String> r1_3 = new HashSet<>();
        r1_3.add("b");

        final MapIncrement<String, String, Integer> fromR2_1 = inc.from("r2");
        assertEquals("r3", fromR2_1.revision());
        assertFalse(fromR2_1.full());
        assertEquals(emptyMap(), fromR2_1.updated());
        assertEquals(r1_3, fromR2_1.removed());
    }
}
