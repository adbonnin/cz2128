package fr.adbonnin.cz2128.json.provider.wrapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.adbonnin.cz2128.JsonException;
import fr.adbonnin.cz2128.JsonProvider;
import fr.adbonnin.cz2128.base.Pair;
import fr.adbonnin.cz2128.json.JsonUtils;
import fr.adbonnin.cz2128.json.iterator.ObjectIterator;

import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class ObjectFieldJsonProviderWrapper implements JsonProvider {

    private final JsonProvider jsonProvider;

    private final String name;

    public ObjectFieldJsonProviderWrapper(String name, JsonProvider jsonProvider) {
        this.jsonProvider = requireNonNull(jsonProvider);
        this.name = requireNonNull(name);
    }

    @Override
    public <T> T withParser(ObjectMapper mapper, Function<JsonParser, ? extends T> function) {
        return jsonProvider.withParser(mapper, parser -> {

            final ObjectIterator itr = new ObjectIterator(parser);
            while (itr.hasNext()) {
                final Pair<String, JsonParser> next = itr.next();
                if (name.equals(next.getKey())) {
                    return function.apply(parser);
                }
            }

            try (JsonParser emptyParser = JsonUtils.newEmptyParser(mapper)) {
                return function.apply(emptyParser);
            }
            catch (IOException e) {
                throw new JsonException(e);
            }
        });
    }

    @Override
    public <T> T withGenerator(ObjectMapper mapper, BiFunction<JsonParser, JsonGenerator, ? extends T> function) {
        return jsonProvider.withGenerator(mapper, (parser, generator) -> {
            T result = null;

            try {
                generator.writeStartObject();
                boolean found = false;

                final ObjectIterator itr = new ObjectIterator(parser);
                while (itr.hasNext()) {
                    final String currentName = itr.next().getKey();
                    generator.writeFieldName(currentName);

                    if (name.equals(currentName)) {
                        found = true;
                        result = function.apply(parser, generator);
                    }
                    else {
                        generator.copyCurrentStructure(parser);
                    }
                }

                if (!found) {
                    generator.writeFieldName(name);

                    try (JsonParser emptyParser = JsonUtils.newEmptyParser(mapper)) {
                        result = function.apply(emptyParser, generator);
                    }
                }

                generator.writeEndObject();
            }
            catch (IOException e) {
                throw new JsonException(e);
            }

            return result;
        });
    }
}
