package fr.adbonnin.cz2128.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.adbonnin.cz2128.base.Predicate;
import fr.adbonnin.cz2128.io.stream.StreamProcessor;
import fr.adbonnin.cz2128.io.stream.StreamProcessor.ReadFunction;
import fr.adbonnin.cz2128.io.stream.StreamProcessor.WriteFunction;
import fr.adbonnin.cz2128.serializer.Serializer;
import fr.adbonnin.cz2128.serializer.ValueReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singleton;
import static java.util.Objects.requireNonNull;

public class BaseRepository {

    private final StreamProcessor processor;

    private final ObjectMapper mapper;

    private final Serializer serializer;

    public BaseRepository(StreamProcessor processor, ObjectMapper mapper, Serializer serializer) {
        this.mapper = requireNonNull(mapper);
        this.processor = requireNonNull(processor);
        this.serializer = requireNonNull(serializer);
    }

    public long count() throws IOException {
        return processor.read(countFunction);
    }

    public <T> long count(final ValueReader<T> reader, final Predicate<? super T> predicate) throws IOException {
        return processor.read(new ReadFunction<Long>() {
            @Override
            public Long read(InputStream input) throws IOException {
                return serializer.count(createParser(input), reader, predicate);
            }
        });
    }

    public <T> boolean delete(final ValueReader<T> reader, final Predicate<? super T> predicate) throws IOException {
        return processor.write(new WriteFunction<Boolean>() {
            @Override
            public Boolean write(InputStream input, OutputStream output) throws IOException {
                return serializer.delete(createParser(input), reader, createGenerator(output), predicate);
            }
        });
    }

    public <T> List<T> findAll(final ValueReader<T> reader, final Predicate<? super T> predicate) throws IOException {
        return processor.read(new ReadFunction<List<T>>() {
            @Override
            public List<T> read(InputStream input) throws IOException {
                return serializer.findAll(createParser(input), reader, predicate);
            }
        });
    }

    public <T> T findOne(final ValueReader<T> reader, final Predicate<? super T> predicate, final T defaultValue) throws IOException {
        return processor.read(new ReadFunction<T>() {
            @Override
            public T read(InputStream input) throws IOException {
                return serializer.findOne(createParser(input), reader, predicate, defaultValue);
            }
        });
    }

    public <T> boolean save(T element, final ValueReader<T> reader) throws IOException {
        return save(singleton(element), reader);
    }

    public <T> boolean save(final Iterable<T> elements, final ValueReader<T> reader) throws IOException {
        return processor.write(new WriteFunction<Boolean>() {
            @Override
            public Boolean write(InputStream input, OutputStream output) throws IOException {
                return serializer.save(elements, createParser(input), reader, createGenerator(output));
            }
        });
    }

    protected JsonParser createParser(InputStream input) throws IOException {
        return mapper.getFactory().createParser(input);
    }

    protected JsonGenerator createGenerator(OutputStream output) throws IOException {
        return mapper.getFactory().createGenerator(output);
    }

    private final ReadFunction<Long> countFunction = new ReadFunction<Long>() {
        @Override
        public Long read(InputStream input) throws IOException {
            return serializer.count(createParser(input));
        }
    };
}
