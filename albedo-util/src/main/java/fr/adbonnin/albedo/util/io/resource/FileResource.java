package fr.adbonnin.albedo.util.io.resource;

import fr.adbonnin.albedo.util.io.CloseableIterator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Objects;

import static fr.adbonnin.albedo.util.collect.IteratorUtils.asIterator;

public class FileResource implements Resource {

    private final File file;

    public FileResource(File file) {
        this.file = Objects.requireNonNull(file);
    }

    public static FileResource toResource(URL url) {

        if (url == null || !"file".equals(url.getProtocol())) {
            return null;
        }

        final File file = new File(url.getFile());
        return new FileResource(file);
    }

    @Override
    public boolean isDirectory() throws IOException {
        return file.isDirectory();
    }

    @Override
    public InputStream openStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public CloseableIterator<Resource> list() throws IOException {
        final Iterator<String> children = asIterator(file.list());
        return new CloseableIterator<Resource>() {

            @Override
            public boolean hasNext() {
                return children.hasNext();
            }

            @Override
            public Resource next() {
                final String next = children.next();
                return new FileResource(new File(next));
            }

            @Override
            public void remove() {
                children.remove();
            }

            @Override
            public void close() throws IOException {}
        };
    }
}
