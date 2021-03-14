package fr.adbonnin.cz2128.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.adbonnin.cz2128.json.provider.FileProvider;
import fr.adbonnin.cz2128.json.provider.MemoryProvider;
import fr.adbonnin.cz2128.json.repository.MapRepository;
import fr.adbonnin.cz2128.json.repository.SetRepository;

import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class Json {

    private final ObjectMapper mapper;

    public Json(ObjectMapper mapper) {
        this.mapper = requireNonNull(mapper);
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public Provider fileProvider(Path file) {
        return new Provider(new FileProvider(file, mapper.getFactory()));
    }

    public Provider fileProvider(Path file, JsonEncoding encoding) {
        return new Provider(new FileProvider(file, encoding, mapper.getFactory()));
    }

    public Provider fileProvider(Path file, Path tempFile, JsonEncoding encoding) {
        return new Provider(new FileProvider(file, tempFile, encoding, mapper.getFactory()));
    }

    public Provider memoryProvider() {
        return new Provider(new MemoryProvider(mapper.getFactory()));
    }

    public Provider memoryProvider(String content) {
        return new Provider(new MemoryProvider(content, mapper.getFactory()));
    }

    public Provider builder(JsonProvider provider) {
        return new Provider(provider);
    }

    public class Provider implements JsonProvider {

        private final JsonProvider provider;

        public Provider(JsonProvider provider) {
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

        @Override
        public JsonNode readJsonNode() {
            return provider.readJsonNode();
        }

        @Override
        public void writeJsonNode(JsonNode node) {
            provider.writeJsonNode(node);
        }

        @Override
        public Provider at(String name) {
            return new Provider(provider.at(name));
        }

        @Override
        public Provider at(int index) {
            return new Provider(provider.at(index));
        }

        @Override
        public Provider concurrent(long lockTimeout) {
            return new Provider(provider.concurrent(lockTimeout));
        }

        public <T> SetRepository<T> setRepository(Class<T> type, JsonUpdateStrategy updateStrategy) {
            return new SetRepository<>(type, mapper, provider, updateStrategy);
        }

        public <T> SetRepository<T> setRepository(TypeReference<T> type, JsonUpdateStrategy updateStrategy) {
            return new SetRepository<>(type, mapper, provider, updateStrategy);
        }

        public <T> SetRepository<T> setRepository(ObjectReader reader, JsonUpdateStrategy updateStrategy) {
            return new SetRepository<>(reader, mapper, provider, updateStrategy);
        }

        public <T> MapRepository<T> mapRepository(Class<T> type, JsonUpdateStrategy updateStrategy) {
            return new MapRepository<>(type, mapper, provider, updateStrategy);
        }

        public <T> MapRepository<T> mapRepository(TypeReference<T> type, JsonUpdateStrategy updateStrategy) {
            return new MapRepository<>(type, mapper, provider, updateStrategy);
        }

        public <T> MapRepository<T> mapRepository(ObjectReader reader, JsonUpdateStrategy updateStrategy) {
            return new MapRepository<>(reader, mapper, provider, updateStrategy);
        }
    }
}
