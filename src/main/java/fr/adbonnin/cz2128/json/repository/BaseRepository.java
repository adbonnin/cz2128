package fr.adbonnin.cz2128.json.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import fr.adbonnin.cz2128.json.JsonProvider;
import fr.adbonnin.cz2128.json.JsonRepository;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

public abstract class BaseRepository<T> implements JsonRepository<T> {

    private final JsonProvider provider;

    public BaseRepository(JsonProvider provider) {
        this.provider = requireNonNull(provider);
    }

    public JsonProvider getProvider() {
        return provider;
    }

    public <R> R withStream(Function<Stream<? extends T>, ? extends R> function) {
        return withIterator((iterator) -> {
            final Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
            final Stream<T> stream = StreamSupport.stream(spliterator, false);
            return function.apply(stream);
        });
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
