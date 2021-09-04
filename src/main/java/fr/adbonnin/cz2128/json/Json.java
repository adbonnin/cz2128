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
import fr.adbonnin.cz2128.json.repository.*;

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

        public NodeRepository node() {
            return new NodeRepository(provider);
        }

        public ValueRepository value() {
            return new ValueRepository(provider);
        }
    }

    public abstract class Repository extends Provider {

        public Repository(JsonProvider provider) {
            super(provider);
        }

        public abstract <U> SetRepository<U> setRepository(Class<U> type);

        public abstract <U> SetRepository<U> setRepository(TypeReference<U> type);

        public abstract <U> SetRepository<U> setRepository(ObjectReader reader);

        public abstract <U> MapRepository<U> mapRepository(Class<U> type);

        public abstract <U> MapRepository<U> mapRepository(TypeReference<U> type);

        public abstract <U> MapRepository<U> mapRepository(ObjectReader reader);

        public abstract <U> ElementRepository<U> elementRepository(Class<U> type);

        public abstract <U> ElementRepository<U> elementRepository(TypeReference<U> type);

        public abstract <U> ElementRepository<U> elementRepository(ObjectReader reader);
    }

    public class NodeRepository extends Repository {

        private final JsonProvider provider;

        public NodeRepository(JsonProvider provider) {
            super(provider);
            this.provider = provider;
        }

        @Override
        public <U> NodeSetRepository<U> setRepository(Class<U> type) {
            return new NodeSetRepository<>(type, provider, mapper);
        }

        @Override
        public <U> NodeSetRepository<U> setRepository(TypeReference<U> type) {
            return new NodeSetRepository<>(type, provider, mapper);
        }

        @Override
        public <U> NodeSetRepository<U> setRepository(ObjectReader reader) {
            return new NodeSetRepository<>(reader, provider, mapper);
        }

        @Override
        public <U> NodeMapRepository<U> mapRepository(Class<U> type) {
            return new NodeMapRepository<>(type, provider, mapper);
        }

        @Override
        public <U> NodeMapRepository<U> mapRepository(TypeReference<U> type) {
            return new NodeMapRepository<>(type, provider, mapper);
        }

        @Override
        public <U> NodeMapRepository<U> mapRepository(ObjectReader reader) {
            return new NodeMapRepository<>(reader, provider, mapper);
        }

        @Override
        public <U> ElementRepository<U> elementRepository(Class<U> type) {
            return new NodeElementRepository<>(type, provider, mapper);
        }

        @Override
        public <U> ElementRepository<U> elementRepository(TypeReference<U> type) {
            return new NodeElementRepository<>(type, provider, mapper);
        }

        @Override
        public <U> ElementRepository<U> elementRepository(ObjectReader reader) {
            return new NodeElementRepository<>(reader, provider, mapper);
        }
    }

    public class ValueRepository extends Repository {

        private final JsonProvider provider;

        public ValueRepository(JsonProvider provider) {
            super(provider);
            this.provider = provider;
        }

        @Override
        public <U> ValueSetRepository<U> setRepository(Class<U> type) {
            return new ValueSetRepository<>(type, provider, mapper);
        }

        @Override
        public <U> ValueSetRepository<U> setRepository(TypeReference<U> type) {
            return new ValueSetRepository<>(type, provider, mapper);
        }

        @Override
        public <U> ValueSetRepository<U> setRepository(ObjectReader reader) {
            return new ValueSetRepository<>(reader, provider, mapper);
        }

        @Override
        public <U> ValueMapRepository<U> mapRepository(Class<U> type) {
            return new ValueMapRepository<>(type, provider, mapper);
        }

        @Override
        public <U> ValueMapRepository<U> mapRepository(TypeReference<U> type) {
            return new ValueMapRepository<>(type, provider, mapper);
        }

        @Override
        public <U> ValueMapRepository<U> mapRepository(ObjectReader reader) {
            return new ValueMapRepository<>(reader, provider, mapper);
        }

        @Override
        public <U> ElementRepository<U> elementRepository(Class<U> type) {
            return new ValueElementRepository<>(type, provider, mapper);
        }

        @Override
        public <U> ElementRepository<U> elementRepository(TypeReference<U> type) {
            return new ValueElementRepository<>(type, provider, mapper);
        }

        @Override
        public <U> ElementRepository<U> elementRepository(ObjectReader reader) {
            return new ValueElementRepository<>(reader, provider, mapper);
        }
    }
}
