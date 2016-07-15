package fr.adbonnin.albedo.util.io;

import java.io.Closeable;
import java.util.Iterator;

public interface CloseableIterator<E> extends Iterator<E>, Closeable {
}
