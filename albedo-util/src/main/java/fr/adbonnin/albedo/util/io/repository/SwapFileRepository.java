package fr.adbonnin.albedo.util.io.repository;

import fr.adbonnin.albedo.util.io.serializer.ArraySerializer;

import java.io.File;
import java.lang.reflect.Type;

import static java.util.Objects.requireNonNull;

public class SwapFileRepository {

    private final File file;

    private final Type type;

    private final ArraySerializer serializer;

    public SwapFileRepository(File file, Type type, ArraySerializer serializer) {
        this.file = requireNonNull(file);
        this.type = requireNonNull(type);
        this.serializer = requireNonNull(serializer);
    }
}
