package fr.adbonnin.cz2128.collect;

import java.io.Closeable;
import java.util.Iterator;

public interface CloseableIterator<E> extends Closeable, Iterator<E> {
}
