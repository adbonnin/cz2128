package fr.adbonnin.albedo.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ObjectUtilsTest {

    @Test
    public void testToString() throws Exception {
        assertNull(ObjectUtils.toString(null));
        assertEquals("test", ObjectUtils.toString("test"));
    }

    @Test
    public void testRequireNonEmptyStr() {
        assertEquals("test", ObjectUtils.requireNonEmpty("test"));
    }

    @Test(expected = NullPointerException.class)
    public void testRequireNonEmptyStrWithNull() {
        ObjectUtils.requireNonEmpty(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRequireNonEmptyStrWithEmpty() {
        ObjectUtils.requireNonEmpty("");
    }


}
