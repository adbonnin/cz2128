package fr.adbonnin.cz2128.io.stream;

import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SwapStreamProcessorTest {

    @Test
    public void testRead() {
        final SwapStreamProcessor processor = new SwapStreamProcessor(100) {

            @Override
            protected InputStream createInputStream() throws IOException {
                return null;
            }

            @Override
            protected OutputStream createToBeSwappedOutputStream() throws IOException {
                return null;
            }

            @Override
            protected void swap(OutputStream tempOutput) throws IOException {

            }
        };
    }
}
