package fr.adbonnin.albedo.util.io;

import fr.adbonnin.albedo.util.io.IOUtils;
import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class IOUtilsTest {

    @Test
    public void testCloseQuietly() throws Exception {
        IOUtils.closeQuietly(null);

        final TestCloseable closeable = new TestCloseable();
        IOUtils.closeQuietly(closeable);
        assertEquals(1, closeable.callCount);
    }

    public static class TestCloseable implements Closeable {

        public int callCount;

        @Override
        public void close() throws IOException {
            ++callCount;
            throw new IOException();
        }
    }
}
