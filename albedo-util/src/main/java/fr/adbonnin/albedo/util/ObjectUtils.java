package fr.adbonnin.albedo.util;

import static java.util.Objects.requireNonNull;

public final class ObjectUtils {

    public static String toString(Object object) {
        return object == null ? null : object.toString();
    }

    public static String requireNonEmpty(String str) {
        requireNonNull(str);

        if (str.isEmpty()) {
            throw new IllegalArgumentException("str must not be empty");
        }

        return str;
    }

    private ObjectUtils() { /* Cannot be instantiated */ }
}
