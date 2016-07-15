package fr.adbonnin.albedo.util.collect;

import fr.adbonnin.albedo.util.Predicate;
import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static fr.adbonnin.albedo.util.collect.IteratorUtils.*;
import static org.junit.Assert.*;

public class IteratorUtilsTest {

    private static final Predicate<String> TEST1_PREDICATE = new Predicate<String>() {

        @Override
        public boolean evaluate(String value) {
            return "test1".equals(value);
        }
    };

    /**
     * @see IterableUtilsTest#testEqual()
     */
    @Test
    public void testEqual() throws Exception {
        final Iterator<Object> itr = TestIteratorUtils.unsupportedOperation();
        assertTrue(equal(null, null));
        assertFalse(equal(itr, null));
        assertFalse(equal(null, itr));
        assertTrue(equal(itr, itr));
    }

    @Test
    public void testEmptyIterator() {
        final Iterator<Integer> itr = emptyIterator();
        assertFalse(itr.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testNextEmptyIterator() {
        emptyIterator().next();
    }

    @Test
    public void testAsIterator() {
        final Iterator<String> itr = asIterator("test");
        assertTrue(itr.hasNext());
        assertEquals("test", itr.next());
        assertFalse(itr.hasNext());
    }

    @Test
    public void testArrayAsIterator() {
        final Iterator<String> itr = asIterator("test1", "test2");
        assertTrue(itr.hasNext());
        assertEquals("test1", itr.next());

        assertTrue(itr.hasNext());
        assertEquals("test2", itr.next());

        assertFalse(itr.hasNext());
    }

    @Test
    public void testSize() {
        assertEquals(0, count(IteratorUtils.emptyIterator()));

        final Iterator<String> itr = asIterator("test1", "test2");
        assertEquals(2, count(itr));
        assertFalse(itr.hasNext());
    }

    @Test
    public void testFilter() {

        // Test before
        Iterator<String> itr = filter(asIterator("test1", "test2"), TEST1_PREDICATE);
        assertTrue(itr.hasNext());
        assertEquals("test1", itr.next());
        assertFalse(itr.hasNext());

        // Test after
        itr = filter(asIterator("test2", "test1"), TEST1_PREDICATE);
        assertTrue(itr.hasNext());
        assertEquals("test1", itr.next());
        assertFalse(itr.hasNext());

        // Test between
        itr = filter(asIterator("test2", "test1", "test2"), TEST1_PREDICATE);
        assertTrue(itr.hasNext());
        assertEquals("test1", itr.next());
        assertFalse(itr.hasNext());
    }

    @Test
    public void testNext() {
        final Iterator<String> itr = asIterator("test1");
        assertEquals("test1", next(itr, "test2"));
        assertEquals("test2", next(itr, "test2"));
    }

    @Test
    public void testFind() {

        // Test found
        assertEquals("test1", find(asIterator("test1", "test2"), TEST1_PREDICATE, "test3"));

        // Test not found
        assertEquals("test3", find(asIterator("unknown", "test2"), TEST1_PREDICATE, "test3"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testConcat() {

        // Test alone
        Iterator<String> itr = concat(asIterator(asIterator("test1")));
        assertTrue(itr.hasNext());
        assertEquals("test1", itr.next());
        assertFalse(itr.hasNext());

        // Test only empty
        itr = concat(asIterator(
            IteratorUtils.<String>emptyIterator(),
            IteratorUtils.<String>emptyIterator(),
            IteratorUtils.<String>emptyIterator()));
        assertFalse(itr.hasNext());

        // Test multiple
        itr = concat(asIterator(asIterator("test1"), asIterator("test2")));
        assertTrue(itr.hasNext());
        assertEquals("test1", itr.next());
        assertTrue(itr.hasNext());
        assertEquals("test2", itr.next());
        assertFalse(itr.hasNext());
    }
}
