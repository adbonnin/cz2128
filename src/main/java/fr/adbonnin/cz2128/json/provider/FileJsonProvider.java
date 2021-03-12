package fr.adbonnin.cz2128.json.provider;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import fr.adbonnin.cz2128.json.JsonException;
import fr.adbonnin.cz2128.json.JsonProvider;
import fr.adbonnin.cz2128.base.FileUtils;
import fr.adbonnin.cz2128.json.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class FileJsonProvider implements JsonProvider {

    public static final String DEFAULT_TEMPORARY_FILE_SUFFIX = ".tmp";

    private final Path file;

    private final Path tempFile;

    private final JsonEncoding encoding;

    private final JsonFactory factory;

    public FileJsonProvider(Path file, JsonFactory factory) {
        this(file, JsonUtils.DEFAULT_ENCODING, factory);
    }

    public FileJsonProvider(Path file, JsonEncoding encoding, JsonFactory factory) {
        this(file, buildDefaultTempFile(file), encoding, factory);
    }

    public FileJsonProvider(Path file, Path tempFile, JsonEncoding encoding, JsonFactory factory) {
        this.file = requireNonNull(file);
        this.tempFile = requireNonNull(tempFile);
        this.encoding = requireNonNull(encoding);
        this.factory = requireNonNull(factory);
    }

    public Path getFile() {
        return file;
    }

    public Path getTempFile() {
        return tempFile;
    }

    public JsonEncoding getEncoding() {
        return encoding;
    }

    public String getJavaEncoding() {
        return encoding.getJavaName();
    }

    public JsonFactory getFactory() {
        return factory;
    }

    public String getContent() {
        try {
            final byte[] bytes = Files.readAllBytes(file);
            return new String(bytes, getJavaEncoding());
        }
        catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public void setContent(String content) {
        try {
            final byte[] bytes = content.getBytes(getJavaEncoding());
            Files.write(file, bytes, StandardOpenOption.WRITE);
        }
        catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public <T> T withParser(Function<JsonParser, ? extends T> function) {

        if (!Files.exists(file)) {
            try (JsonParser emptyParser = JsonUtils.newEmptyParser()) {
                return function.apply(emptyParser);
            }
            catch (IOException e) {
                throw new JsonException(e);
            }
        }

        try (InputStream input = Files.newInputStream(file, StandardOpenOption.READ);
             JsonParser parser = factory.createParser(input)) {
            return function.apply(parser);
        }
        catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public <R> R withGenerator(BiFunction<JsonParser, JsonGenerator, ? extends R> function) {
        final R result;

        try {
            try (OutputStream tempOutput = Files.newOutputStream(tempFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                 JsonGenerator tempGenerator = factory.createGenerator(tempOutput, encoding)) {
                result = withParser(parser -> function.apply(parser, tempGenerator));
            }

            Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            throw new JsonException(e);
        }
        finally {
            FileUtils.deleteIfExistsQuietly(tempFile);
        }

        return result;
    }

    public static Path buildDefaultTempFile(Path file) {
        return file.resolveSibling(file.getFileName() + DEFAULT_TEMPORARY_FILE_SUFFIX);
    }
}
