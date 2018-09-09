package fr.adbonnin.cz2128.io.stream;

import java.io.*;

import static java.util.Objects.requireNonNull;

public class FileStreamSwapper extends StreamSwapper {

    private final File file;

    private final File tempFile;

    public FileStreamSwapper(File file, File tempFile, long timeout) {
        super(timeout);
        this.file = requireNonNull(file);
        this.tempFile = requireNonNull(tempFile);
    }

    @Override
    protected InputStream createInputStream() throws IOException {
        return new BufferedInputStream(new FileInputStream(file));
    }

    @Override
    protected OutputStream createOutputStream() throws IOException {
        return new BufferedOutputStream(new FileOutputStream(tempFile));
    }

    @Override
    protected void swap(OutputStream closedOutput) throws IOException {
        if (!tempFile.renameTo(file)) {
            throw new IOException("files swap has failed");
        }
    }
}
