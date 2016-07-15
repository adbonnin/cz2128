package fr.adbonnin.albedo.util;

import org.junit.Test;

import static fr.adbonnin.albedo.util.EquivalenceUtils.areEqual;
import static fr.adbonnin.albedo.util.EquivalenceUtils.haveSameReference;
import static org.junit.Assert.*;

public class EquivalenceUtilsTest {

    public static class TestA {

        private final int value;

        public TestA(int value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {

            if (obj == this) {
                return true;
            }

            if (!(obj instanceof TestA)) {
                return false;
            }

            final TestA other = (TestA) obj;
            return value == other.value;
        }
    }

    @Test
    public void testHaveSameReference() throws Exception {
        assertTrue(haveSameReference().equivalent(1, 1));
        assertFalse(haveSameReference().equivalent(0, 1));
    }

    @Test
    public void testAreEqual() throws Exception {
        TestA a0 = new TestA(0);
        TestA a1 = new TestA(1);
        TestA a1p = new TestA(1);

        // Test same ref
        assertTrue(areEqual().equivalent(a0, a0));
        assertTrue(areEqual().equivalent(null, null));

        // Test are equal
        assertTrue(areEqual().equivalent(a1, a1p));

        // Test null left
        assertFalse(areEqual().equivalent(null, a0));

        // Test null right
        assertFalse(areEqual().equivalent(a0, null));
    }
}
