package fr.adbonnin.cz2128.io.stream;

import java.io.*;

import static fr.adbonnin.cz2128.base.ArrayUtils.EMPTY_BYTE_ARRAY;

public class BytesStreamProcessor extends SwapStreamProcessor {

    private byte[] bytes;

    public BytesStreamProcessor(long timeout) {
        this(EMPTY_BYTE_ARRAY, timeout);
    }

    public BytesStreamProcessor(String str, long timeout) {
        this(str.getBytes(), timeout);
    }

    public BytesStreamProcessor(byte[] bytes, long timeout) {
        super(timeout);
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    protected InputStream createInputStream() throws IOException {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    protected OutputStream createOutputStream() throws IOException {
        return new ByteArrayOutputStream();
    }

    @Override
    protected void swap(OutputStream output) throws IOException {
        bytes = ((ByteArrayOutputStream) output).toByteArray();
    }
}
