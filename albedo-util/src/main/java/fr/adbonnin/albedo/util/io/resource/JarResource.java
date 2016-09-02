package fr.adbonnin.albedo.util.io.resource;

import fr.adbonnin.albedo.util.ObjectUtils;
import fr.adbonnin.albedo.util.collect.AbstractIterator;
import fr.adbonnin.albedo.util.io.CloseableIterator;
import fr.adbonnin.albedo.util.io.FileUtils;
import fr.adbonnin.albedo.util.io.IOUtils;
import fr.adbonnin.albedo.util.io.InputStreamWrapper;
import fr.adbonnin.albedo.util.io.resource.support.EntryNotFoundException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import static java.util.Objects.requireNonNull;

public class JarResource implements Resource {

    private final File file;
    private final String name;

    public JarResource(File file, String name) {
        this.file = requireNonNull(file);
        this.name = ObjectUtils.requireNonEmpty(name);
    }

    public static JarResource toResource(URL url) {

        if (url == null || !"jar".equals(url.getProtocol())) {
            return null;
        }

        final String urlFile = url.getFile();

        final int separatorIndex = urlFile.lastIndexOf("!/");
        if (separatorIndex == -1) {
            throw new IllegalArgumentException("Separator \"!/\" not found; file: " + urlFile);
        }

        final String fileUrl = urlFile.substring(0, separatorIndex);
        final String file;
        try {
            file = new URL(fileUrl).getFile();
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        final String name = urlFile.substring(separatorIndex + 2);
        return new JarResource(new File(file), name);
    }

    @Override
    public boolean isDirectory() throws IOException {
        final JarFile jar = new JarFile(file);
        try {
            final ZipEntry entry = jar.getEntry(name);
            if (entry == null) {
                throw new EntryNotFoundException("Entry not found; " +
                    "name: " + name + "; " +
                    "file: " + FileUtils.tryCanonicalPath(file));
            }

            return entry.isDirectory();
        }
        finally {
            IOUtils.closeQuietly(jar);
        }
    }

    @Override
    public InputStream openStream() throws IOException {
        final JarFile jar = new JarFile(file);

        final ZipEntry entry = jar.getEntry(name);
        if (entry == null) {
            IOUtils.closeQuietly(jar);
            throw new EntryNotFoundException("Entry not found; " +
                "name: " + name + "; " +
                "file: " + FileUtils.tryCanonicalPath(file));
        }

        final InputStream input;
        try {
            input = jar.getInputStream(entry);
        }
        catch (IOException e) {
            IOUtils.closeQuietly(jar);
            throw e;
        }

        return new InputStreamWrapper(input) {

            @Override
            public void close() throws IOException {
                try {
                    super.close();
                }
                finally {
                    IOUtils.closeQuietly(jar);
                }
            }
        };
    }

    @Override
    public CloseableIterator<Resource> list() throws IOException {
        final JarFile jarFile = new JarFile(file);
        return new JarChildrenIterator(jarFile);
    }

    private class JarChildrenIterator extends AbstractIterator<Resource> implements CloseableIterator<Resource> {

        private final JarFile jar;

        private final Enumeration<JarEntry> entries;

        public JarChildrenIterator(JarFile jar) {
            this.jar = jar;
            this.entries = jar.entries();
        }

        @Override
        protected Resource computeNext() {

            while (entries.hasMoreElements()) {
                final String childName = entries.nextElement().getName();
                if (childName.startsWith(name)) {
                    return new JarResource(file, childName);
                }
            }

            return endOfData();
        }

        @Override
        public void close() throws IOException {
            jar.close();
        }
    }
}
