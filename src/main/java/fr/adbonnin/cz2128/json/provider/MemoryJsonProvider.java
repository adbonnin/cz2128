package fr.adbonnin.cz2128.json.provider;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import fr.adbonnin.cz2128.JsonException;
import fr.adbonnin.cz2128.JsonProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class MemoryJsonProvider implements JsonProvider {

    volatile private String content;

    private final JsonFactory jsonFactory;

    public MemoryJsonProvider(JsonFactory jsonFactory) {
        this("", jsonFactory);
    }

    public MemoryJsonProvider(String content, JsonFactory jsonFactory) {
        this.content = requireNonNull(content);
        this.jsonFactory = requireNonNull(jsonFactory);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public JsonFactory getJsonFactory() {
        return jsonFactory;
    }

    @Override
    public <R> R withParser(Function<JsonParser, ? extends R> function) {
        try (JsonParser parser = jsonFactory.createParser(content)) {
            return function.apply(parser);
        }
        catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public <R> R withGenerator(BiFunction<JsonParser, JsonGenerator, ? extends R> function) {
        final R result;
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            try (JsonGenerator generator = jsonFactory.createGenerator(output)) {
                result = withParser(parser -> function.apply(parser, generator));
            }
            finally {
                output.close();
            }
        }
        catch (IOException e) {
            throw new JsonException(e);
        }

        content = output.toString();
        return result;
    }
}
