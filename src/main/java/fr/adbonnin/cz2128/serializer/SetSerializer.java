package fr.adbonnin.cz2128.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.adbonnin.cz2128.base.Predicate;
import fr.adbonnin.cz2128.collect.AbstractIterator;
import fr.adbonnin.cz2128.collect.IteratorUtils;
import fr.adbonnin.cz2128.io.CloseableIterator;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.core.JsonToken.*;
import static fr.adbonnin.cz2128.collect.CollectionUtils.newArrayList;
import static fr.adbonnin.cz2128.io.IOUtils.closeQuietly;
import static java.util.Objects.requireNonNull;

public class SetSerializer implements Serializer {

    private final ObjectMapper mapper;

    public SetSerializer(ObjectMapper mapper) {
        this.mapper = requireNonNull(mapper);
    }

    @Override
    public long count(JsonParser parser) throws IOException {
        CloseableIterator<Void> itr = null;
        try {
            itr = asSkippedElementIterator(parser);
            return IteratorUtils.count(itr);
        }
        catch (JacksonIOException e) {
            throw e.getCause();
        }
        finally {
            closeQuietly(itr);
        }
    }

    @Override
    public <T> long count(JsonParser parser, ValueReader<T> reader, Predicate<? super T> predicate) throws IOException {
        CloseableIterator<T> itr = null;
        try {
            itr = asElementIterator(parser, reader);
            return IteratorUtils.count(IteratorUtils.filter(itr, predicate));
        }
        catch (JacksonIOException e) {
            throw e.getCause();
        }
        finally {
            closeQuietly(itr);
        }
    }

    @Override
    public <T> boolean delete(JsonParser parser, ValueReader<T> reader, JsonGenerator generator, Predicate<? super T> predicate) throws IOException {
        boolean updated = false;
        CloseableIterator<ObjectNode> itr = null;
        try {
            generator.writeStartArray();

            itr = asObjectNodeIterator(parser);
            while (itr.hasNext()) {
                final ObjectNode node = itr.next();
                final T element = reader.read(node);

                if (predicate.evaluate(element)) {
                    updated = true;
                }
                else {
                    generator.writeTree(node);
                }
            }

            generator.writeEndArray();
        }
        catch (JacksonIOException e) {
            throw e.getCause();
        }
        finally {
            closeQuietly(itr);
        }

        return updated;
    }

    @Override
    public <T> List<T> findAll(JsonParser parser, ValueReader<T> reader, Predicate<? super T> predicate) throws IOException {
        CloseableIterator<T> itr = null;
        try {
            itr = asElementIterator(parser, reader);
            return newArrayList(IteratorUtils.filter(itr, predicate));
        }
        catch (JacksonIOException e) {
            throw e.getCause();
        }
        finally {
            closeQuietly(itr);
        }
    }

    @Override
    public <T> T findOne(JsonParser parser, ValueReader<T> reader, Predicate<? super T> predicate, T defaultValue) throws IOException {
        CloseableIterator<T> itr = null;
        try {
            itr = asElementIterator(parser, reader);
            return IteratorUtils.find(itr, predicate, defaultValue);
        }
        catch (JacksonIOException e) {
            throw e.getCause();
        }
        finally {
            closeQuietly(itr);
        }
    }

    @Override
    public <T> boolean save(Iterable<T> elements, JsonParser parser, ValueReader<T> reader, JsonGenerator generator) throws IOException {
        final Map<T, T> newElements = mapElements(elements);
        boolean updated = false;
        CloseableIterator<ObjectNode> itr = null;
        try {
            generator.writeStartArray();

            // Update old elements
            itr = asObjectNodeIterator(parser);
            while (itr.hasNext()) {
                final ObjectNode oldNode = itr.next();
                final T oldElement = reader.read(oldNode);

                final T newElement = newElements.remove(oldElement);
                final ObjectNode newNode = mapper.valueToTree(newElement);
                updated = JacksonUtils.updateObject(oldNode, newNode, generator) || updated;
            }

            // Create new elements
            for (T newElement : newElements.values()) {
                final ObjectNode newNode = mapper.valueToTree(newElement);
                updated = JacksonUtils.updateObject(null, newNode, generator) || updated;
            }

            generator.writeEndArray();
        }
        catch (JacksonIOException e) {
            throw e.getCause();
        }
        finally {
            closeQuietly(generator);
            closeQuietly(itr);
        }

        return updated;
    }

    private static <T> Map<T, T> mapElements(Iterable<? extends T> elements) {
        requireNonNull(elements);

        final Map<T, T> map = new HashMap<>();
        for (T element : elements) {
            map.put(element, element);
        }

        return map;
    }

    private CloseableIterator<Void> asSkippedElementIterator(JsonParser parser) throws IOException {
        final JacksonArrayIterator itr = new JacksonArrayIterator(parser);
        return new CloseableIterator<Void>() {

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public Void next() {
                try {
                    itr.next().skipChildren();
                    return null;
                }
                catch (IOException e) {
                    throw new JacksonIOException(e);
                }
            }

            @Override
            public void remove() {
                itr.remove();
            }

            @Override
            public void close() throws IOException {
                itr.close();
            }
        };
    }

    private <T> CloseableIterator<T> asElementIterator(JsonParser parser, final ValueReader<T> reader) throws IOException {
        final CloseableIterator<ObjectNode> itr = asObjectNodeIterator(parser);
        return new CloseableIterator<T>() {

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public T next() {
                try {
                    return reader.read(itr.next());
                }
                catch (IOException e) {
                    throw new JacksonIOException(e);
                }
            }

            @Override
            public void remove() {
                itr.remove();
            }

            @Override
            public void close() throws IOException {
                itr.close();
            }
        };
    }

    private CloseableIterator<ObjectNode> asObjectNodeIterator(JsonParser parser) throws IOException {
        final JacksonArrayIterator itr = new JacksonArrayIterator(parser);
        return new CloseableIterator<ObjectNode>() {

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public ObjectNode next() {
                try {
                    return mapper.readTree(itr.next());
                }
                catch (IOException e) {
                    throw new JacksonIOException(e);
                }
            }

            @Override
            public void remove() {
                itr.remove();
            }

            @Override
            public void close() throws IOException {
                itr.close();
            }
        };
    }

    private class JacksonArrayIterator extends AbstractIterator<JsonParser> implements CloseableIterator<JsonParser> {

        private final JsonParser parser;

        private boolean first = true;

        private JacksonArrayIterator(JsonParser parser) throws IOException {
            this.parser = requireNonNull(parser);
        }

        @Override
        protected JsonParser computeNext() {
            try {
                JsonToken token = parser.nextToken();

                if (first) {
                    if (token == null) {
                        return endOfData();
                    }
                    else if (!START_ARRAY.equals(token)) {
                        throw new IllegalStateException("JsonReader must start with an array");
                    }
                    else {
                        token = parser.nextToken();
                    }
                }

                if (END_ARRAY.equals(token)) {
                    return endOfData();
                }
                else if (!START_OBJECT.equals(token)) {
                    throw new IllegalStateException("Array must contains objects");
                }

                return parser;
            }
            catch (IOException e) {
                throw new JacksonIOException(e);
            }
            finally {
                first = false;
            }
        }

        @Override
        public void close() throws IOException {
            parser.close();
        }
    }

    private static class JacksonIOException extends RuntimeException {

        private final IOException cause;

        public JacksonIOException(IOException cause) {
            super(cause);
            this.cause = cause;
        }

        @Override
        public IOException getCause() {
            return cause;
        }
    }
}
