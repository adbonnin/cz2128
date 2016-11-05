package fr.adbonnin.cz2128.io.stream;

import fr.adbonnin.cz2128.io.stream.StreamProcessor.ReadLongFunction;
import fr.adbonnin.cz2128.io.stream.StreamProcessor.WriteBooleanFunction;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class BytesProcessorTest {

    private static final Charset DEFAULT_ENCODING = Charset.defaultCharset();

    private BytesStreamProcessor processor;

    @BeforeMethod
    public void setUp() throws Exception {
        processor = new BytesStreamProcessor(-1);
    }

    @Test
    public void testEmpty() {
        assertEquals(processor.getBytes().length, 0);
    }

    @Test
    public void testReturns() throws IOException {
        assertEquals(processor.read(new ReadLongFunction() {
            @Override
            public long read(InputStream input) throws IOException {
                return 5;
            }
        }), 5);

        assertTrue(processor.write(new WriteBooleanFunction() {
            @Override
            public boolean write(InputStream input, OutputStream output) throws IOException {
                return true;
            }
        }));
    }

    @Test
    public void test() throws Exception {

        // Read empty and write "test1"
        processor.write(new WriteBooleanFunction() {
            @Override
            public boolean write(InputStream input, OutputStream output) throws IOException {
                assertTrue(IOUtils.toString(input, DEFAULT_ENCODING).isEmpty());
                IOUtils.write("test1", output, DEFAULT_ENCODING);
                return false;
            }
        });

        // Bytes contains "test1"
        assertEquals(processor.getBytes(), "test1".getBytes());

        // Read "test1"
        processor.read(new ReadLongFunction() {
            @Override
            public long read(InputStream input) throws IOException {
                assertEquals(IOUtils.toString(input, DEFAULT_ENCODING), "test1");
                return 0;
            }
        });

        // Read "test1" and write "test2"
        processor.write(new WriteBooleanFunction() {
            @Override
            public boolean write(InputStream input, OutputStream output) throws IOException {
                assertEquals(IOUtils.toString(input, DEFAULT_ENCODING), "test1");
                IOUtils.write("test2", output, DEFAULT_ENCODING);
                return false;
            }
        });

        // Bytes contains "test2"
        assertEquals(processor.getBytes(), "test2".getBytes());
    }
}
