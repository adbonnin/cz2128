package fr.adbonnin.albedo.util;

public final class StringUtils {

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    private StringUtils() { /* Cannot be instantiated */ }
}
