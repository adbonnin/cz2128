package fr.adbonnin.cz2128.io.stream;

import java.io.*;

import static fr.adbonnin.cz2128.base.ArrayUtils.EMPTY_BYTE_ARRAY;

public class ByteArrayStreamSwapper extends StreamSwapper {

    private byte[] bytes;

    public ByteArrayStreamSwapper(long timeout) {
        this(EMPTY_BYTE_ARRAY, timeout);
    }

    public ByteArrayStreamSwapper(String str, long timeout) {
        this(str.getBytes(), timeout);
    }

    public ByteArrayStreamSwapper(byte[] bytes, long lockTimeout) {
        super(lockTimeout);
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    protected InputStream createInputStream() {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    protected OutputStream createOutputStream() {
        return new ByteArrayOutputStream();
    }

    @Override
    protected void swap(OutputStream closedOutput) {
        bytes = ((ByteArrayOutputStream) closedOutput).toByteArray();
    }

    @Override
    public String toString() {
        return new String(bytes);
    }
}
