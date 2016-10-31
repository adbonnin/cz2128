package fr.adbonnin.cz2128.collect;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.*;

public class CollectionUtilsTest {

    @Test
    public void testAddAll() {

        try {
            CollectionUtils.addAll(null, Collections.emptyList().iterator());
            fail();
        }
        catch (NullPointerException e) {
            // should throw exception
        }

        try {
            CollectionUtils.addAll(Collections.emptyList(), null);
            fail();
        }
        catch (NullPointerException e) {
            // should throw exception
        }

        final List<String> list = new ArrayList<>();
        final List<String> excepted = Arrays.asList("A", "B");

        assertTrue(CollectionUtils.addAll(list, excepted.iterator()));
        assertEquals(list, excepted);
    }
}
