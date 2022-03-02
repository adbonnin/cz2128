package fr.adbonnin.cz2128.io;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;

public class FileUtils {

    /**
     * Creates any necessary but nonexistent parent directories of the specified path. Note that if
     * this operation fails, it may have succeeded in creating some (but not all) of the necessary
     * parent directories. The parent directory is created with the given {@code attrs}.
     *
     * @throws IOException if an I/O error occurs, or if any necessary but nonexistent parent
     *     directories of the specified file could not be created.
     */
    public static void createParentDirectories(Path path, FileAttribute<?>... attrs)
        throws IOException {
        // Interestingly, unlike File.getCanonicalFile(), Path/Files provides no way of getting the
        // canonical (absolute, normalized, symlinks resolved, etc.) form of a path to a nonexistent
        // file. getCanonicalFile() can at least get the canonical form of the part of the path which
        // actually exists and then append the normalized remainder of the path to that.
        Path normalizedAbsolutePath = path.toAbsolutePath().normalize();
        Path parent = normalizedAbsolutePath.getParent();
        if (parent == null) {
            // The given directory is a filesystem root. All zero of its ancestors exist. This doesn't
            // mean that the root itself exists -- consider x:\ on a Windows machine without such a
            // drive -- or even that the caller can create it, but this method makes no such guarantees
            // even for non-root files.
            return;
        }

        // Check if the parent is a directory first because createDirectories will fail if the parent
        // exists and is a symlink to a directory... we'd like for this to succeed in that case.
        // (I'm kind of surprised that createDirectories would fail in that case; doesn't seem like
        // what you'd want to happen.)
        if (!Files.isDirectory(parent)) {
            Files.createDirectories(parent, attrs);
            if (!Files.isDirectory(parent)) {
                throw new IOException("Unable to create parent directories of " + path);
            }
        }
    }

    public static void deleteIfExistsQuietly(Path path) {
        if (path != null) {
            try {
                Files.deleteIfExists(path);
            }
            catch (IOException ignored) {
                // nothing to do
            }
        }
    }

    public static boolean deleteRecursively(Path path) throws IOException {

        if (Files.notExists(path)) {
            return false;
        }

        Files.walkFileTree(path, DELETE_RECURSIVELY_VISITOR);
        return true;
    }

    private static final FileVisitor<Path> DELETE_RECURSIVELY_VISITOR = new SimpleFileVisitor<Path>() {

        @Override
        public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {

            if (attrs.isSymbolicLink() || attrs.isOther()) {
                Files.delete(dir);
                return FileVisitResult.SKIP_SUBTREE;
            }

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

            if (exc != null) {
                throw exc;
            }

            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    };

    private FileUtils() { /* Cannot be instantiated */ }
}
