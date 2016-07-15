package fr.adbonnin.albedo.util.io.resource;

import fr.adbonnin.albedo.util.io.CloseableIterator;

import java.io.IOException;
import java.io.InputStream;

public interface Resource {

    boolean isDirectory() throws IOException;

    InputStream openStream() throws IOException;

    CloseableIterator<Resource> list() throws IOException;
}
