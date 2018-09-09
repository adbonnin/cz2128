package fr.adbonnin.cz2128.repository;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import fr.adbonnin.cz2128.base.Predicate;
import fr.adbonnin.cz2128.io.stream.StreamReader;
import fr.adbonnin.cz2128.io.stream.StreamReader.ReadFunction;
import fr.adbonnin.cz2128.io.stream.StreamWriter;
import fr.adbonnin.cz2128.io.stream.StreamWriter.WriteFunction;
import fr.adbonnin.cz2128.serializer.Serializer;
import fr.adbonnin.cz2128.serializer.ValueReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static fr.adbonnin.cz2128.io.IOUtils.closeQuietly;
import static java.util.Collections.singleton;
import static java.util.Objects.requireNonNull;

public class BaseRepository {

    private final StreamReader streamReader;

    private final StreamWriter streamWriter;

    private final JsonFactory jsonFactory;

    private final Serializer serializer;

    public BaseRepository(StreamReader streamReader, StreamWriter streamWriter, JsonFactory jsonFactory, Serializer serializer) {
        this.streamReader = requireNonNull(streamReader);
        this.streamWriter = requireNonNull(streamWriter);
        this.jsonFactory = requireNonNull(jsonFactory);
        this.serializer = requireNonNull(serializer);
    }

    public long count() throws IOException {
        return streamReader.read(countFunction);
    }

    public <T> long count(final ValueReader<T> reader, final Predicate<? super T> predicate) throws IOException {
        return streamReader.read(new ReadJsonFunction<Long>() {
            @Override
            public Long readJson(JsonParser parser) throws IOException {
                return serializer.count(parser, reader, predicate);
            }
        });
    }

    public <T> boolean delete(final ValueReader<T> reader, final Predicate<? super T> predicate) throws IOException {
        return streamWriter.write(new WriteJsonFunction<Boolean>() {
            @Override
            public Boolean writeJson(JsonParser parser, JsonGenerator generator) throws IOException {
                return serializer.delete(parser, reader, generator, predicate);
            }
        });
    }

    public <T> List<T> findAll(final ValueReader<T> reader, final Predicate<? super T> predicate) throws IOException {
        return streamReader.read(new ReadJsonFunction<List<T>>() {
            @Override
            public List<T> readJson(JsonParser parser) throws IOException {
                return serializer.findAll(parser, reader, predicate);
            }
        });
    }

    public <T> T findOne(final ValueReader<T> reader, final Predicate<? super T> predicate, final T defaultValue) throws IOException {
        return streamReader.read(new ReadJsonFunction<T>() {
            @Override
            public T readJson(JsonParser parser) throws IOException {
                return serializer.findOne(parser, reader, predicate, defaultValue);
            }
        });
    }

    public <T> boolean save(T element, final ValueReader<T> reader) throws IOException {
        return save(singleton(element), reader);
    }

    public <T> boolean save(final Iterable<T> elements, final ValueReader<T> reader) throws IOException {
        return streamWriter.write(new WriteJsonFunction<Boolean>() {
            @Override
            public Boolean writeJson(JsonParser parser, JsonGenerator generator) throws IOException {
                return serializer.save(elements, parser, reader, generator);
            }
        });
    }

    private abstract class ReadJsonFunction<T> implements ReadFunction<T> {

        public abstract T readJson(JsonParser parser) throws IOException;

        @Override
        public T read(InputStream input) throws IOException {
            JsonParser parser = null;
            try {
                parser = createParser(input);
                return readJson(parser);
            }
            finally {
                closeQuietly(parser);
            }
        }
    }

    private abstract class WriteJsonFunction<T> implements WriteFunction<T> {

        public abstract T writeJson(JsonParser parser, JsonGenerator generator) throws IOException;

        @Override
        public T write(InputStream input, OutputStream output) throws IOException {
            JsonParser parser = null;
            JsonGenerator generator = null;
            try {
                parser = createParser(input);
                generator = createGenerator(output);
                return writeJson(parser, generator);
            }
            finally {
                closeQuietly(generator);
                closeQuietly(parser);
            }
        }
    }

    private final ReadFunction<Long> countFunction = new ReadFunction<Long>() {
        @Override
        public Long read(InputStream input) throws IOException {
            final JsonParser parser = createParser(input);
            return serializer.count(parser);
        }
    };

    private JsonParser createParser(InputStream input) throws IOException {
        return jsonFactory.createParser(input);
    }

    private JsonGenerator createGenerator(OutputStream output) throws IOException {
        return jsonFactory.createGenerator(output);
    }
}
