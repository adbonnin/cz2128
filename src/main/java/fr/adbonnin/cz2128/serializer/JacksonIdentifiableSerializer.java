package fr.adbonnin.cz2128.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.adbonnin.cz2128.base.Identifiable;
import fr.adbonnin.cz2128.base.IdentifiableUtils;
import fr.adbonnin.cz2128.base.Predicate;
import fr.adbonnin.cz2128.collect.AbstractIterator;
import fr.adbonnin.cz2128.exception.JacksonIOException;
import fr.adbonnin.cz2128.io.CloseableIterator;

import java.io.IOException;
import java.util.Map;

import static com.fasterxml.jackson.core.JsonToken.*;
import static fr.adbonnin.cz2128.io.IOUtils.closeQuietly;
import static java.util.Objects.requireNonNull;

public class JacksonIdentifiableSerializer extends IterableIdentifiableSerializer {

    private final ObjectMapper mapper;

    public JacksonIdentifiableSerializer(ObjectMapper mapper) {
        this.mapper = requireNonNull(mapper);
    }

    @Override
    public <E extends Identifiable> boolean delete(Predicate<E> predicate, JsonParser parser, JsonGenerator generator, JavaType typeOfT) throws IOException {
        boolean updated = false;

        CloseableIterator<ObjectNode> itr = null;
        try {
            generator.writeStartArray();

            itr = asObjectNodeIterator(parser);
            while (itr.hasNext()) {
                final ObjectNode node = itr.next();
                final E element = mapper.convertValue(node, typeOfT);

                if (predicate.evaluate(element)) {
                    updated = true;
                }
                else {
                    generator.writeTree(node);
                }
            }

            generator.writeEndArray();
        }
        finally {
            closeQuietly(itr);
        }

        return updated;
    }

    @Override
    public <E extends Identifiable> boolean save(Iterable<E> elements, JsonParser parser, JsonGenerator generator, JavaType typeOfT) throws IOException {
        final Map<Object, E> elementsById = IdentifiableUtils.indexByIds(elements);
        boolean updated = false;

        CloseableIterator<ObjectNode> itr = null;
        try {
            generator.writeStartArray();

            // Update old elements
            itr = asObjectNodeIterator(parser);
            while (itr.hasNext()) {
                final ObjectNode oldNode = itr.next();
                final E oldElement = mapper.convertValue(oldNode, typeOfT);
                final E newElement = elementsById.remove(oldElement.id());
                final ObjectNode newNode = mapper.convertValue(newElement, typeOfT);
                updated = JacksonUtils.updateObject(oldNode, newNode, generator) || updated;
            }

            // Create new elements
            for (E newElement : elementsById.values()) {
                final ObjectNode newNode = mapper.convertValue(newElement, typeOfT);
                updated = JacksonUtils.updateObject(null, newNode, generator) || updated;
            }

            generator.writeEndArray();
        }
        finally {
            closeQuietly(generator);
            closeQuietly(itr);
        }

        return updated;
    }

    @Override
    protected CloseableIterator<Void> asSkippedElementIterator(JsonParser parser) throws IOException {
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
                }
                catch (IOException e) {
                    throw new JacksonIOException(e);
                }
                return null;
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

    @Override
    protected <T> CloseableIterator<T> asElementIterator(JsonParser parser, final JavaType typeOfT) throws IOException {
        final JacksonArrayIterator itr = new JacksonArrayIterator(parser);
        return new CloseableIterator<T>() {

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @SuppressWarnings("unchecked")
            @Override
            public T next() {
                return (T) mapper.convertValue(itr.next(), typeOfT);
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
}
