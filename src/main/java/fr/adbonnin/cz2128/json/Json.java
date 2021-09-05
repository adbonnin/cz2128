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

    public ProviderFactory fileProvider(Path file) {
        return new ProviderFactory(new FileProvider(file, mapper.getFactory()));
    }

    public ProviderFactory fileProvider(Path file, JsonEncoding encoding) {
        return new ProviderFactory(new FileProvider(file, encoding, mapper.getFactory()));
    }

    public ProviderFactory fileProvider(Path file, Path tempFile, JsonEncoding encoding) {
        return new ProviderFactory(new FileProvider(file, tempFile, encoding, mapper.getFactory()));
    }

    public ProviderFactory fileProvider(Path file, Function<Path, Path> tempFileProvider, JsonEncoding encoding) {
        return new ProviderFactory(new FileProvider(file, tempFileProvider, encoding, mapper.getFactory()));
    }

    public ProviderFactory memoryProvider() {
        return new ProviderFactory(new MemoryProvider(mapper.getFactory()));
    }

    public ProviderFactory memoryProvider(String content) {
        return new ProviderFactory(new MemoryProvider(content, mapper.getFactory()));
    }

    public ProviderFactory provider(JsonProvider provider) {
        return new ProviderFactory(provider);
    }

    public class ProviderFactory implements JsonProvider {

        private final JsonProvider provider;

        public ProviderFactory(JsonProvider provider) {
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
        public ProviderFactory at(String name) {
            return new ProviderFactory(provider.at(name));
        }

        @Override
        public ProviderFactory at(int index) {
            return new ProviderFactory(provider.at(index));
        }

        @Override
        public ProviderFactory concurrent(long lockTimeout) {
            return new ProviderFactory(provider.concurrent(lockTimeout));
        }

        public NodeRepositoryFactory node() {
            return new NodeRepositoryFactory(provider);
        }

        public ValueRepositoryFactory value() {
            return new ValueRepositoryFactory(provider);
        }
    }

    public abstract class RepositoryFactory extends ProviderFactory {

        public RepositoryFactory(JsonProvider provider) {
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

    public class NodeRepositoryFactory extends RepositoryFactory {

        private final JsonProvider provider;

        public NodeRepositoryFactory(JsonProvider provider) {
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

    public class ValueRepositoryFactory extends RepositoryFactory {

        private final JsonProvider provider;

        public ValueRepositoryFactory(JsonProvider provider) {
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
