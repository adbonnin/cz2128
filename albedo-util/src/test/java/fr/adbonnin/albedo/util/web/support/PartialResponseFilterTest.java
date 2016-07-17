package fr.adbonnin.albedo.util.web.support;

import fr.adbonnin.albedo.util.web.PartialFilter;
import org.junit.Test;

import static org.junit.Assert.*;

public class PartialResponseFilterTest {

    @Test
    public void testWildcard() {
        final PartialResponseFilter filter = PartialResponseFilter.buildWildcard();
        assertNull(filter.name());
        assertTrue(filter.match("test1"));
        assertTrue(filter.in("test1").match("test2"));
    }

    @Test
    public void testMatch() {
        final PartialResponseFilter filter = PartialResponseFilter.build("test1");
        assertNull(filter.name());
        assertTrue(filter.match("test1"));

        final PartialResponseFilter test1 = filter.in("test1");
        assertEquals("test1", test1.name());
        assertTrue(test1.match("test2"));
        assertTrue(test1.match("test3"));

        assertFalse(filter.match("test3"));
        assertFalse(filter.in("test3").match("test2"));
    }

    @Test
    public void testDeepMatch() {
        final PartialResponseFilter filter = PartialResponseFilter.build("test1(test2)");
        assertNull(filter.name());
        assertFalse(filter.match("test1"));

        final PartialFilter test1 = filter.in("test1");
        assertTrue(test1.match("test2"));
        assertFalse(test1.match("test3"));
    }
}
