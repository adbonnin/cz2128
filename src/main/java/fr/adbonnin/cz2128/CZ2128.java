package fr.adbonnin.cz2128;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.adbonnin.cz2128.json.provider.FileJsonProvider;
import fr.adbonnin.cz2128.json.provider.MemoryJsonProvider;

import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class CZ2128 {

    private final ObjectMapper mapper;

    public CZ2128(ObjectMapper mapper) {
        this.mapper = requireNonNull(mapper);
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public JsonProviderBuilder fileProvider(Path file) {
        return new JsonProviderBuilder(new FileJsonProvider(file, mapper.getFactory()));
    }

    public JsonProviderBuilder fileProvider(Path file, JsonEncoding encoding) {
        return new JsonProviderBuilder(new FileJsonProvider(file, encoding, mapper.getFactory()));
    }

    public JsonProviderBuilder fileProvider(Path file, Path tempFile, JsonEncoding encoding) {
        return new JsonProviderBuilder(new FileJsonProvider(file, tempFile, encoding, mapper.getFactory()));
    }

    public JsonProviderBuilder memoryProvider() {
        return new JsonProviderBuilder(new MemoryJsonProvider(mapper.getFactory()));
    }

    public JsonProviderBuilder memoryProvider(String content) {
        return new JsonProviderBuilder(new MemoryJsonProvider(content, mapper.getFactory()));
    }

    public JsonProviderBuilder builder(JsonProvider provider) {
        return new JsonProviderBuilder(provider);
    }

    public class JsonProviderBuilder implements JsonProvider {

        private final JsonProvider provider;

        public JsonProviderBuilder(JsonProvider provider) {
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
        public JsonProviderBuilder at(String name) {
            return new JsonProviderBuilder(provider.at(name));
        }

        @Override
        public JsonProviderBuilder at(int index) {
            return new JsonProviderBuilder(provider.at(index));
        }

        @Override
        public JsonProviderBuilder concurrent(long lockTimeout) {
            return new JsonProviderBuilder(provider.concurrent(lockTimeout));
        }

        public <T> JsonSetRepository<T> setRepository(Class<T> type, JsonUpdateStrategy updateStrategy) {
            return new JsonSetRepository<>(type, mapper, provider, updateStrategy);
        }

        public <T> JsonSetRepository<T> setRepository(TypeReference<T> type, JsonUpdateStrategy updateStrategy) {
            return new JsonSetRepository<>(type, mapper, provider, updateStrategy);
        }

        public <T> JsonSetRepository<T> setRepository(ObjectReader reader, JsonUpdateStrategy updateStrategy) {
            return new JsonSetRepository<>(reader, mapper, provider, updateStrategy);
        }

        public <T> JsonMapRepository<T> mapRepository(Class<T> type, JsonUpdateStrategy updateStrategy) {
            return new JsonMapRepository<>(type, mapper, provider, updateStrategy);
        }

        public <T> JsonMapRepository<T> mapRepository(TypeReference<T> type, JsonUpdateStrategy updateStrategy) {
            return new JsonMapRepository<>(type, mapper, provider, updateStrategy);
        }

        public <T> JsonMapRepository<T> mapRepository(ObjectReader reader, JsonUpdateStrategy updateStrategy) {
            return new JsonMapRepository<>(reader, mapper, provider, updateStrategy);
        }
    }
}
