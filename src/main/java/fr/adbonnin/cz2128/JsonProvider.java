package fr.adbonnin.cz2128;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.adbonnin.cz2128.json.provider.wrapper.ObjectFieldJsonProviderWrapper;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface JsonProvider {

    String getContent();

    void setContent(String content);

    <R> R withParser(ObjectMapper mapper, Function<JsonParser, ? extends R> function);

    <R> R withGenerator(ObjectMapper mapper, BiFunction<JsonParser, JsonGenerator, ? extends R> function);

    default JsonProvider at(String name) {
        return new ObjectFieldJsonProviderWrapper(name, this);
    }
}
