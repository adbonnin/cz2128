package fr.adbonnin.cz2128.io.stream;

import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class StreamSwapperTest {

    public static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;
    public static final long DEFAULT_TIMEOUT = 2000;

    @Test
    public void testRead() {
        final StreamSwapper processor = new StreamSwapper(100) {

            @Override
            protected InputStream createInputStream() throws IOException {
                return null;
            }

            @Override
            protected OutputStream createOutputStream() throws IOException {
                return null;
            }

            @Override
            protected void swap(OutputStream closedOutput) throws IOException {

            }
        };
    }

    /*@Test
    public void testReturns() throws IOException {
        assertEquals(swa.read(new StreamReader.ReadFunction<Long>() {
            @Override
            public Long read(InputStream input) throws IOException {
                return 5L;
            }
        }), new Long(5));

        assertTrue(processor.write(new StreamWriter.WriteFunction<Boolean>() {
            @Override
            public Boolean write(InputStream input, OutputStream output) throws IOException {
                return true;
            }
        }));
    }*/
}
