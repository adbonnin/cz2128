package fr.adbonnin.cz2128.schema.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static fr.adbonnin.xtra.base.XtraObjects.requireNonNull;
import static fr.adbonnin.xtra.base.XtraObjects.requireNonNullCopy;

public class ObjectUtilsTest {

    @Test
    public void testRequireNonNull() throws Exception {

        // when / then
        requireNonNull("foo", "bar");

        // when
        try {
            requireNonNull("foo", null);

            // then
            Assert.fail();
        }
        catch (NullPointerException e) {
            // should throw an exception
        }
    }

    @Test
    public void testRequireNonNullCopy() throws Exception {

        // when
        final List<String> copy = requireNonNullCopy("foo", "bar");

        // then
        assertEquals(2, copy.size());
        assertEquals("foo", copy.get(0));
        assertEquals("bar", copy.get(1));

        // when
        try {
            requireNonNullCopy("foo", null);

            // then
            Assert.fail();
        }
        catch (NullPointerException e) {
            // should throw an exception
        }
    }
}
