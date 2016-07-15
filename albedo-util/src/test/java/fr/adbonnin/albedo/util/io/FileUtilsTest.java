package fr.adbonnin.albedo.util.io;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FileUtilsTest {

    @Test
    public void cleanFilename() throws Exception {

        // test endsWith space or period
        assertEquals("a", FileUtils.cleanFilename("a  "));
        assertEquals("a", FileUtils.cleanFilename("a.."));

        // test reserved chars
        StringBuilder sb = new StringBuilder("a");
        for (char c : FileUtils.FILENAME_RESERVED_CHARACTERS) {
            sb.append(c);
        }

        assertEquals("a", FileUtils.cleanFilename(sb.toString()));

        // test less than or equals 31
        sb = new StringBuilder("a");
        for (int i = 0; i <= 31; i++) {
            sb.append((char) i);
        }

        assertEquals("a", FileUtils.cleanFilename(sb.toString()));
    }
}
