package fr.adbonnin.cz2128.json.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import fr.adbonnin.cz2128.json.JsonProvider;
import fr.adbonnin.cz2128.json.JsonRepository;

import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public abstract class BaseRepository<T> implements JsonRepository<T> {

    private final JsonProvider provider;

    public BaseRepository(JsonProvider provider) {
        this.provider = requireNonNull(provider);
    }

    public JsonProvider getProvider() {
        return provider;
    }

    @Override
    public <R> R withParser(Function<JsonParser, ? extends R> function) {
        return provider.withParser(function);
    }

    @Override
    public <R> R withGenerator(BiFunction<JsonParser, JsonGenerator, ? extends R> function) {
        return provider.withGenerator(function);
    }
}
