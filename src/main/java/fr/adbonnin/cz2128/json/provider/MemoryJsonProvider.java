package fr.adbonnin.cz2128.json.provider;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.adbonnin.cz2128.JsonException;
import fr.adbonnin.cz2128.JsonProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class MemoryJsonProvider implements JsonProvider {

    volatile private String content;

    public MemoryJsonProvider() {
        this("");
    }

    public MemoryJsonProvider(String content) {
        this.content = requireNonNull(content);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public <R> R withParser(ObjectMapper mapper, Function<JsonParser, ? extends R> function) {
        try (JsonParser parser = mapper.getFactory().createParser(content)) {
            return function.apply(parser);
        }
        catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public <R> R withGenerator(ObjectMapper mapper, BiFunction<JsonParser, JsonGenerator, ? extends R> function) {
        final R result;
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            try (JsonGenerator generator = mapper.getFactory().createGenerator(output)) {
                result = withParser(mapper, parser -> function.apply(parser, generator));
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
