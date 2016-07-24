package fr.adbonnin.albedo.util;

import org.junit.Test;

import static fr.adbonnin.albedo.util.StringUtils.isEmpty;
import static org.junit.Assert.*;

public class StringUtilsTest {

    @Test
    public void testIsEmptyString() {
        assertTrue(isEmpty(""));
        assertTrue(isEmpty(null));
        assertFalse(isEmpty("test"));
    }

    @Test
    public void testIsEmptyCharSequence() {
        assertTrue(isEmpty((CharSequence)""));
        assertTrue(isEmpty((CharSequence)null));
        assertFalse(isEmpty((CharSequence)"test"));
    }
}
