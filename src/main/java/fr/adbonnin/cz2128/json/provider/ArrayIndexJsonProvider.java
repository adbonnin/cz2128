package fr.adbonnin.cz2128.json.provider;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import fr.adbonnin.cz2128.JsonException;
import fr.adbonnin.cz2128.JsonProvider;
import fr.adbonnin.cz2128.json.JsonUtils;
import fr.adbonnin.cz2128.json.iterator.ArrayIterator;

import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ArrayIndexJsonProvider implements JsonProvider {

    private final int index;

    private final JsonProvider provider;

    public ArrayIndexJsonProvider(int index, JsonProvider provider) {
        this.index = index;
        this.provider = provider;
    }

    @Override
    public <R> R withParser(Function<JsonParser, ? extends R> function) {
        return provider.withParser(parser -> {
            try {
                final ArrayIterator itr = new ArrayIterator(parser);
                int currentIndex = 0;

                while (itr.hasNext()) {
                    final JsonParser next = itr.next();

                    if (currentIndex == index) {
                        return function.apply(parser);
                    }
                    else {
                        next.skipChildren();
                    }

                    if (currentIndex > index) {
                        break;
                    }

                    ++currentIndex;
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
                generator.writeStartArray();
                boolean found = false;

                final ArrayIterator itr = new ArrayIterator(parser);
                int currentIndex = 0;

                while (itr.hasNext()) {
                    itr.next();

                    if (index == currentIndex) {
                        found = true;
                        result = function.apply(parser, generator);
                    }
                    else {
                        generator.copyCurrentStructure(parser);
                    }

                    ++currentIndex;
                }

                if (!found) {
                    while (currentIndex < index) {
                        generator.writeNull();
                        ++currentIndex;
                    }

                    try (JsonParser emptyParser = JsonUtils.newEmptyParser()) {
                        result = function.apply(emptyParser, generator);
                    }
                }

                generator.writeEndArray();
            }
            catch (IOException e) {
                throw new JsonException(e);
            }

            return result;
        });
    }
}
