package fr.adbonnin.cz2128.json.provider;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import fr.adbonnin.cz2128.io.FileUtils;
import fr.adbonnin.cz2128.json.JsonException;
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

public class FileProvider implements ContentProvider {

    public static final String DEFAULT_TEMPORARY_FILE_SUFFIX = ".tmp";

    private final Path file;

    private final Function<Path, Path> tempFileProvider;

    private final JsonEncoding encoding;

    private final JsonFactory factory;

    public FileProvider(Path file, JsonFactory factory) {
        this(file, JsonUtils.DEFAULT_ENCODING, factory);
    }

    public FileProvider(Path file, JsonEncoding encoding, JsonFactory factory) {
        this(file, FileProvider::buildDefaultTempFile, encoding, factory);
    }

    public FileProvider(Path file, Path tempFile, JsonEncoding encoding, JsonFactory factory) {
        this(file, f -> tempFile, encoding, factory);
    }

    public FileProvider(Path file, Function<Path, Path> tempFileProvider, JsonEncoding encoding, JsonFactory factory) {
        this.file = requireNonNull(file);
        this.tempFileProvider = requireNonNull(tempFileProvider);
        this.encoding = requireNonNull(encoding);
        this.factory = requireNonNull(factory);
    }

    public Path getFile() {
        return file;
    }

    public Function<Path, Path> getTempFileProvider() {
        return tempFileProvider;
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

    @Override
    public String getContent() throws IOException {

        if (!Files.exists(file)) {
            return "";
        }

        final byte[] bytes = Files.readAllBytes(file);
        return new String(bytes, getJavaEncoding());
    }

    @Override
    public void setContent(String content) throws IOException {
        final byte[] bytes = content.getBytes(getJavaEncoding());
        FileUtils.createParentDirectories(file);
        Files.write(file, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
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
        final Path tempFile = tempFileProvider.apply(file);

        try {
            FileUtils.createParentDirectories(tempFile);
            try (OutputStream tempOutput = Files.newOutputStream(tempFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                 JsonGenerator tempGenerator = factory.createGenerator(tempOutput, encoding)) {
                result = withParser(parser -> function.apply(parser, tempGenerator));
            }

            FileUtils.createParentDirectories(file);
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
