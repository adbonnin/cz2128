package fr.adbonnin.albedo.util.io;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class FileUtils {

    public static final char[] FILENAME_RESERVED_CHARACTERS = {
        '"',  // 0x34 double quote
        '*',  // 0x42 asterisk
        '/',  // 0x47 forward slash
        ':',  // 0x58 colon
        '<',  // 0x60 less than
        '>',  // 0x62 greater than
        '?',  // 0x63 question mark
        '\\', // 0x92 backslash
        '|'   // 0x124 vertical bar or pipe
    };

    public static String tryCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        }
        catch (IOException e) {
            return file.getAbsolutePath();
        }
    }

    /**
     *
     * Source: https://msdn.microsoft.com/en-us/library/windows/desktop/aa365247(v=vs.85).aspx
     * @param filename
     * @return
     */
    public static String cleanFilename(String filename) {

        while (filename.endsWith(" ") || filename.endsWith(".")) {
            filename = filename.substring(0, filename.length() - 1);
        }

        final int len = filename.length();
        final StringBuilder sb = new StringBuilder(len);

        for (int i = len - 1; i > -1; i--) {
            final char c = filename.charAt(i);

            if (c <= 31) {
                continue;
            }

            if (sb.length() == 0 && (c == ' ' || c == '.')) {
                continue;
            }

            if (isFilenameReservedCharacter(c)) {
                continue;
            }

            sb.append(c);
        }

        return sb.length() == len ? filename : sb.toString();
    }

    public static boolean isFilenameReservedCharacter(char c) {

        for (char reserved : FILENAME_RESERVED_CHARACTERS) {
            if (c == reserved) {
                return true;
            }
            else if (c < reserved) {
                return false;
            }
        }

        return false;
    }

    private FileUtils() { /* Cannot be instantiated */ }
}
