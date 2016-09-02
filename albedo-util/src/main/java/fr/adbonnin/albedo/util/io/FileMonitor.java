package fr.adbonnin.albedo.util.io;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileMonitor implements Runnable {

    private final Map<WatchKey, FileWatcher> keys = new HashMap<>();

    private final WatchService service;

    public FileMonitor() throws IOException {
        this.service = FileSystems.getDefault().newWatchService();
    }

    public FileMonitorHandler handler() {
        throw new UnsupportedOperationException();
    }

    public FileMonitor handler(FileMonitorHandler handler) {
        throw new UnsupportedOperationException();
    }

    public void watch(Path path, FileMonitorHandler handler, boolean recursive) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void fireCreate(FileWatcher watcher, Path path) {
        watcher.handler().handleCreate(watcher.baseDir(), path);
    }

    protected void fireDelete(FileWatcher watcher, Path path) {
        watcher.handler().handleDelete(watcher.baseDir(), path);
    }

    protected void fireModify(FileWatcher watcher, Path path) {
        watcher.handler().handleModify(watcher.baseDir(), path);
    }

    protected void fireOverflow(FileWatcher watcher) {
        watcher.handler().handleOverflow(watcher.baseDir());
    }

    protected void fireWatchException(FileWatcher watcher, Path path, Throwable exception) {
        watcher.handler().handleWatchException(watcher.baseDir(), path, exception);
    }

    @Override
    public void run() {
        for (;;) {

            WatchKey key;
            try {
                key = service.take();
            }
            catch (InterruptedException e) {
                continue;
            }

            FileWatcher watcher = keys.get(key);
            if (watcher == null) {
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                final WatchEvent.Kind<?> kind = event.kind();

                if (kind == OVERFLOW) {
                    fireOverflow(watcher);
                    continue;
                }

                final WatchEvent<Path> pathKind = cast(event);
                final Path name = pathKind.context();
                final Path child = watcher.baseDir().resolve(name);

                if (kind == ENTRY_CREATE) {
                    fireCreate(watcher, child);

                    if (watcher.recursive()) {
                        // register all
                    }
                }
                else if (kind == ENTRY_DELETE) {
                    fireDelete(watcher, child);
                }
                else if (kind == ENTRY_MODIFY) {
                    fireModify(watcher, child);
                }
            }

            if (key.reset()) {
                keys.remove(key); // ameliorer
            }
        }
    }

    public void stop() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    public interface FileMonitorHandler {

        void handleCreate(Path baseDir, Path path);

        void handleDelete(Path baseDir, Path path);

        void handleModify(Path baseDir, Path path);

        void handleOverflow(Path baseDir);

        void handleWatchException(Path baseDir, Path path, Throwable exception);
    }

    public class FileWatcher {

        public Path baseDir() {
            throw new UnsupportedOperationException();
        }

        public boolean recursive() { throw new UnsupportedOperationException(); }

        public void cancel() {
            throw new UnsupportedOperationException();
        }

        public FileMonitorHandler handler() {
            throw new UnsupportedOperationException();
        }
    }
}
