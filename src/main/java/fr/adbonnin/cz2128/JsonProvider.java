package fr.adbonnin.cz2128;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface JsonProvider {

    <R> R withParser(ObjectMapper mapper, Function<JsonParser, ? extends R> function);

    <R> R withGenerator(ObjectMapper mapper, BiFunction<JsonParser, JsonGenerator, ? extends R> function);
}
