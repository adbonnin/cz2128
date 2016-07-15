package fr.adbonnin.albedo.util;

import static fr.adbonnin.albedo.util.EquivalenceUtils.ObjectEquivalence.ARE_EQUAL;
import static fr.adbonnin.albedo.util.EquivalenceUtils.ObjectEquivalence.SAME_REFERENCE;

public final class EquivalenceUtils {

    @SuppressWarnings("unchecked")
    public static <T> Equivalence<T> haveSameReference() {
        return (Equivalence<T>) SAME_REFERENCE;
    }

    @SuppressWarnings("unchecked")
    public static <T> Equivalence<T> areEqual() {
        return (Equivalence<T>) ARE_EQUAL;
    }

    enum ObjectEquivalence implements Equivalence<Object> {
        /** @see EquivalenceUtils#haveSameReference()  */
        SAME_REFERENCE {
            @Override
            public boolean equivalent(Object a, Object b) {
                return a == b;
            }
        },
        ARE_EQUAL {
            @Override
            public boolean equivalent(Object a, Object b) {
                return a == b || (a != null && a.equals(b));
            }
        }
    }

    private EquivalenceUtils() { /* Cannot be instantiated */ }
}
