package fr.adbonnin.albedo.util;

public final class PredicateUtils {

    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> alwaysTrue() {
        return (Predicate<T>) ObjectPredicate.ALWAYS_TRUE;
    }

    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> alwaysFalse() {
        return (Predicate<T>) ObjectPredicate.ALWAYS_FALSE;
    }

    enum ObjectPredicate implements Predicate<Object> {
        /** @see PredicateUtils#alwaysTrue() */
        ALWAYS_TRUE {
            @Override
            public boolean evaluate(Object value) {
                return true;
            }
        },
        /** @see PredicateUtils#alwaysFalse() () */
        ALWAYS_FALSE {
            @Override
            public boolean evaluate(Object value) {
                return false;
            }
        }
    }

    private PredicateUtils() { /* Cannot be instantiated */ }
}
