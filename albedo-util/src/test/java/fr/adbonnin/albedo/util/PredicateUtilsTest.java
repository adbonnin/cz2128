package fr.adbonnin.albedo.util;

import org.junit.Test;

import static fr.adbonnin.albedo.util.PredicateUtils.alwaysFalse;
import static fr.adbonnin.albedo.util.PredicateUtils.alwaysTrue;
import static org.junit.Assert.*;

public class PredicateUtilsTest {

    @Test
    public void testAlwaysTrue() throws Exception {
        assertTrue(alwaysTrue().evaluate(null));
    }

    @Test
    public void testAlwaysFalse() throws Exception {
        assertFalse(alwaysFalse().evaluate(null));
    }
}
