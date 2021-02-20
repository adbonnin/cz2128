package fr.adbonnin.cz2128.json.provider.wrapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
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

    private final JsonProvider provider;

    private final String name;

    public ObjectFieldJsonProviderWrapper(String name, JsonProvider provider) {
        this.provider = requireNonNull(provider);
        this.name = requireNonNull(name);
    }

    @Override
    public <R> R withParser(Function<JsonParser, ? extends R> function) {
        return provider.withParser(parser -> {
            try {
                final ObjectIterator itr = new ObjectIterator(parser);
                while (itr.hasNext()) {
                    final Pair<String, JsonParser> next = itr.next();
                    if (name.equals(next.getKey())) {
                        return function.apply(parser);
                    }
                    else {
                        next.getValue().skipChildren();
                    }
                }

                try (JsonParser emptyParser = JsonUtils.newEmptyParser()) {
                    return function.apply(emptyParser);
                }
            }
            catch (IOException e) {
                throw new JsonException(e);
            }
        });
    }

    @Override
    public <R> R withGenerator(BiFunction<JsonParser, JsonGenerator, ? extends R> function) {
        return provider.withGenerator((parser, generator) -> {
            R result = null;

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

                    try (JsonParser emptyParser = JsonUtils.newEmptyParser()) {
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
