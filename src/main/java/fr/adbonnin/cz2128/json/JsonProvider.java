package fr.adbonnin.cz2128.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import fr.adbonnin.cz2128.json.provider.ArrayIndexJsonProvider;
import fr.adbonnin.cz2128.json.provider.ConcurrentJsonProvider;
import fr.adbonnin.cz2128.json.provider.ObjectFieldJsonProvider;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface JsonProvider {

    <R> R withParser(Function<JsonParser, ? extends R> function);

    <R> R withGenerator(BiFunction<JsonParser, JsonGenerator, ? extends R> function);

    default JsonNode readJsonNode() {
        return JsonUtils.readNode(this);
    }

    default void writeJsonNode(JsonNode node) {
        JsonUtils.writeNode(node, this);
    }

    default JsonProvider at(String name) {
        return new ObjectFieldJsonProvider(name, this);
    }

    default JsonProvider at(int index) {
        return new ArrayIndexJsonProvider(index, this);
    }

    default JsonProvider concurrent(long lockTimeout) {
        return new ConcurrentJsonProvider(this, lockTimeout);
    }
}
