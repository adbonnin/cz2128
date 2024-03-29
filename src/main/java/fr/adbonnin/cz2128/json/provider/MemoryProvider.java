package fr.adbonnin.cz2128.json.provider;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import fr.adbonnin.cz2128.json.JsonException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class MemoryProvider implements ContentProvider {

    volatile private String content;

    private final JsonFactory factory;

    public MemoryProvider(JsonFactory factory) {
        this("", factory);
    }

    public MemoryProvider(String content, JsonFactory factory) {
        this.content = requireNonNull(content);
        this.factory = requireNonNull(factory);
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    public JsonFactory getFactory() {
        return factory;
    }

    @Override
    public <R> R withParser(Function<JsonParser, ? extends R> function) {
        try (JsonParser parser = factory.createParser(content)) {
            return function.apply(parser);
        }
        catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public <R> R withGenerator(BiFunction<JsonParser, JsonGenerator, ? extends R> function) {
        final R result;
        final StringWriter writer = new StringWriter();
        try {
            try (JsonGenerator generator = factory.createGenerator(writer)) {
                result = withParser(parser -> function.apply(parser, generator));
            }
            finally {
                writer.close();
            }
        }
        catch (IOException e) {
            throw new JsonException(e);
        }

        content = writer.toString();
        return result;
    }
}
