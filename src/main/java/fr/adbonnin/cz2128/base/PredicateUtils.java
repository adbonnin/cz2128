package fr.adbonnin.cz2128.base;

public final class PredicateUtils {

    public static <T> Predicate<T> alwaysTrue() {
        return ObjectPredicate.ALWAYS_TRUE.withNarrowedType();
    }

    public static <T> Predicate<T> alwaysFalse() {
        return ObjectPredicate.ALWAYS_FALSE.withNarrowedType();
    }

    private enum ObjectPredicate implements Predicate<Object> {
        /** @see PredicateUtils#alwaysTrue() */
        ALWAYS_TRUE {
            @Override
            public boolean evaluate(Object value) {
                return true;
            }
        },
        /** @see PredicateUtils#alwaysFalse() */
        ALWAYS_FALSE {
            @Override
            public boolean evaluate(Object value) {
                return false;
            }
        };

        @SuppressWarnings("unchecked")
        <T> Predicate<T>  withNarrowedType() {
            return (Predicate<T>) this;
        }
    }

    private PredicateUtils() { /* Cannot be instantiated */ }
}
