package fr.adbonnin.albedo.util.io;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

import static fr.adbonnin.albedo.util.io.FileUtils.cleanFilename;
import static fr.adbonnin.albedo.util.io.FileUtils.touch;
import static java.nio.file.Files.createDirectory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileUtilsTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void testCleanFilename() throws Exception {

        // test endsWith space or period
        assertEquals("a", cleanFilename("a  "));
        assertEquals("a", cleanFilename("a.."));

        // test reserved chars
        StringBuilder sb = new StringBuilder("a");
        for (char c : FileUtils.FILENAME_RESERVED_CHARACTERS) {
            sb.append(c);
        }

        assertEquals("a", cleanFilename(sb.toString()));

        // test less than or equals 31
        sb = new StringBuilder("a");
        for (int i = 0; i <= 31; i++) {
            sb.append((char) i);
        }

        assertEquals("a", cleanFilename(sb.toString()));
    }

    @Test
    public void testTouch() throws Exception {
        final Path rootPath = testFolder.getRoot().toPath();

        // File not exists
        final Path file = rootPath.resolve("file");
        assertFalse(Files.exists(file));

        touch(file);
        assertTrue(Files.exists(file));

        // File already exists
        FileTime lastModified = Files.getLastModifiedTime(file);

        Thread.sleep(100);
        touch(file);
        assertEquals(-1, lastModified.compareTo(Files.getLastModifiedTime(file)));

        // Dir already exists
        final Path dir = rootPath.resolve("dir");
        createDirectory(dir);
        assertTrue(Files.isDirectory(dir));
        lastModified = Files.getLastModifiedTime(dir);

        Thread.sleep(100);
        touch(dir);
        assertEquals(-1, lastModified.compareTo(Files.getLastModifiedTime(dir)));
    }
}
