package fr.adbonnin.cz2128.base;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PredicateUtilsTest {

    @Test
    public void testAlwaysTrue() {
        Assert.assertTrue(PredicateUtils.alwaysTrue().evaluate(null));
    }

    @Test
    public void testAlwaysFalse() {
        Assert.assertFalse(PredicateUtils.alwaysFalse().evaluate(null));
    }
}
