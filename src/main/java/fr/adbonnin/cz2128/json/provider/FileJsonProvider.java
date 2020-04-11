package fr.adbonnin.cz2128.json.provider;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.adbonnin.cz2128.JsonProvider;
import fr.adbonnin.cz2128.JsonException;
import fr.adbonnin.cz2128.base.FileUtils;

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

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    public static final String DEFAULT_TEMPORARY_FILE_SUFFIX = ".tmp";

    public static final JsonEncoding DEFAULT_ENCODING = JsonEncoding.UTF8;

    private final Path file;

    private final Path tempFile;

    private final JsonEncoding encoding;

    public FileJsonProvider(Path file, Path tempFile, JsonEncoding encoding) {
        this.file = requireNonNull(file);
        this.tempFile = requireNonNull(tempFile);
        this.encoding = requireNonNull(encoding);
    }

    public FileJsonProvider(Path file) {
        this(file, buildDefaultTempFile(file), DEFAULT_ENCODING);
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

    public String getContent() throws IOException {
        final byte[] bytes = Files.readAllBytes(file);
        return new String(bytes, getJavaEncoding());
    }

    public void setContent(String content) throws IOException {
        final byte[] bytes = content.getBytes(getJavaEncoding());
        Files.write(file, bytes, StandardOpenOption.WRITE);
    }

    @Override
    public <T> T withParser(ObjectMapper mapper, Function<JsonParser, ? extends T> function) {
        try {
            if (!Files.exists(file)) {
                final JsonParser parser = mapper.getFactory().createParser(EMPTY_BYTE_ARRAY);
                return function.apply(parser);
            }
            else {
                try (InputStream input = Files.newInputStream(file, StandardOpenOption.READ);
                     JsonParser parser = mapper.getFactory().createParser(input)) {
                    return function.apply(parser);
                }
            }
        }
        catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public <T> T withGenerator(ObjectMapper mapper, BiFunction<JsonParser, JsonGenerator, ? extends T> function) {
        final T result;

        try {
            try (OutputStream tempOutput = Files.newOutputStream(tempFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                 JsonGenerator tempGenerator = mapper.getFactory().createGenerator(tempOutput, encoding)) {
                result = withParser(mapper, parser -> function.apply(parser, tempGenerator));
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
