package fr.adbonnin.cz2128.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    public static void deleteIfExistsQuietly(Path path) {
        if (path != null) {
            try {
                Files.deleteIfExists(path);
            }
            catch (IOException e) {
                // nothing to do
            }
        }
    }

    private FileUtils() { /* Cannot be instantiated */ }
}
