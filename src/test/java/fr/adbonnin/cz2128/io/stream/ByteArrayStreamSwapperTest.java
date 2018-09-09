package fr.adbonnin.cz2128.io.stream;

import fr.adbonnin.cz2128.io.stream.StreamReader.ReadFunction;
import fr.adbonnin.cz2128.io.stream.StreamWriter.WriteFunction;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static fr.adbonnin.cz2128.io.stream.StreamSwapperTest.DEFAULT_ENCODING;
import static fr.adbonnin.cz2128.io.stream.StreamSwapperTest.DEFAULT_TIMEOUT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ByteArrayStreamSwapperTest {

    @Test
    public void testEmptyConstructor() throws Exception {
        // given
        final AtomicInteger counter = new AtomicInteger();
        final ByteArrayStreamSwapper swapper = new ByteArrayStreamSwapper(DEFAULT_TIMEOUT);

        // when / then
        assertEquals(swapper.getBytes().length, 0);

        // when
        swapper.read(new ReadFunction<Void>() {
            @Override
            public Void read(InputStream input) throws IOException {
                final byte[] buffer = new byte[1];

                // then
                assertEquals(0, IOUtils.read(input, buffer));

                assertEquals(0, counter.getAndIncrement());
                return null;
            }
        });

        assertEquals(1, counter.get());
    }

    @Test
    public void testStrConstructor() throws Exception {
        // given
        final AtomicInteger counter = new AtomicInteger();
        final ByteArrayStreamSwapper swapper = new ByteArrayStreamSwapper("foo", DEFAULT_TIMEOUT);

        // when / then
        assertEquals("foo".getBytes(), swapper.getBytes());

        // when
        swapper.read(new ReadFunction<Void>() {
            @Override
            public Void read(InputStream input) throws IOException {
                final byte[] buff = new byte[4];

                // then
                assertEquals(3, IOUtils.read(input, buff));
                assertEquals("foo".getBytes(), Arrays.copyOf(buff, 3));

                assertEquals(0, counter.getAndIncrement());
                return null;
            }
        });

        assertEquals(1, counter.get());
    }

    @Test
    public void test() throws Exception {
        // given
        final AtomicInteger counter = new AtomicInteger();
        final ByteArrayStreamSwapper processor = new ByteArrayStreamSwapper(DEFAULT_TIMEOUT);

        // when
        processor.write(new WriteFunction<Void>() {
            @Override
            public Void write(InputStream input, OutputStream output) throws IOException {

                // then read empty
                assertTrue(IOUtils.toString(input, DEFAULT_ENCODING).isEmpty());

                // when write "foo"
                IOUtils.write("foo", output, DEFAULT_ENCODING);

                assertEquals(0, counter.getAndIncrement());
                return null;
            }
        });

        assertEquals(1, counter.get());

        // then read "foo" from field
        assertEquals(processor.getBytes(), "foo".getBytes());

        processor.read(new ReadFunction<Void>() {
            @Override
            public Void read(InputStream input) throws IOException {

                // then read "foo" from read
                assertEquals(IOUtils.toString(input, DEFAULT_ENCODING), "foo");

                assertEquals(1, counter.getAndIncrement());
                return null;
            }
        });

        assertEquals(2, counter.get());

        processor.write(new WriteFunction<Void>() {
            @Override
            public Void write(InputStream input, OutputStream output) throws IOException {

                // then read "foo" from write
                assertEquals(IOUtils.toString(input, DEFAULT_ENCODING), "foo");

                // when write "bar"
                IOUtils.write("bar", output, DEFAULT_ENCODING);

                assertEquals(2, counter.getAndIncrement());
                return null;
            }
        });

        assertEquals(3, counter.get());

        // then read "bar"
        assertEquals(processor.getBytes(), "bar".getBytes());
    }
}
