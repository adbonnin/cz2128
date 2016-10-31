package fr.adbonnin.cz2128.io;

import org.testng.annotations.Test;

import java.io.Closeable;
import java.io.IOException;

public class IOUtilsTest {

    @Test
    public void testCloseQuietly() {
        IOUtils.closeQuietly(new Closeable() {
            @Override
            public void close() throws IOException {
                throw new IOException();
            }
        });
    }
}
