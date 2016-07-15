package fr.adbonnin.albedo.util.io.serializer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.type.TypeFactory;
import fr.adbonnin.albedo.util.Identifiable;
import fr.adbonnin.albedo.util.Predicate;
import fr.adbonnin.albedo.util.collect.AbstractIterator;
import fr.adbonnin.albedo.util.io.CloseableIterator;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.fasterxml.jackson.core.JsonToken.*;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static fr.adbonnin.albedo.util.IdentifiableUtils.indexByIds;
import static fr.adbonnin.albedo.util.io.IOUtils.closeQuietly;
import static java.util.Objects.requireNonNull;

public class JacksonEntitySerializer extends IterableEntitySerializer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final TypeFactory TYPE_FACTORY = OBJECT_MAPPER.getTypeFactory();

    private static final JsonFactory JSON_FACTORY = OBJECT_MAPPER.getFactory();

    @Override
    public <T extends Identifiable> boolean delete(Predicate<T> predicate, Reader reader, Writer writer, Type typeOfT) throws IOException {
        final ObjectReader objectReader = buildObjectReader(typeOfT);
        boolean updated = false;

        CloseableIterator<JsonNode> itr = null;
        JsonGenerator jsonGenerator = null;
        try {
            jsonGenerator = JSON_FACTORY.createGenerator(writer);
            jsonGenerator.writeStartArray();

            itr = asJsonNodeIterator(reader);
            while (itr.hasNext()) {
                final JsonNode node = itr.next();
                final T entity = objectReader.readValue(node);

                if (predicate.evaluate(entity)) {
                    updated = true;
                }
                else {
                    jsonGenerator.writeTree(node);
                }
            }

            jsonGenerator.writeEndArray();
        }
        finally {
            closeQuietly(jsonGenerator);
            closeQuietly(itr);
        }

        return updated;
    }

    @Override
    public <T extends Identifiable> boolean save(Iterable<T> entities, Reader reader, Writer writer, Type typeOfT) throws IOException {
        final Map<Object, T> entitiesById = indexByIds(entities);
        final ObjectReader objectReader = buildObjectReader(typeOfT);
        boolean updated = false;

        CloseableIterator<JsonNode> itr = null;
        JsonGenerator jsonGenerator = null;
        try {
            jsonGenerator = JSON_FACTORY.createGenerator(writer);
            jsonGenerator.writeStartArray();

            // Update old entities
            itr = asJsonNodeIterator(reader);
            while (itr.hasNext()) {
                final JsonNode oldNode = itr.next();
                final T oldEntity = objectReader.readValue(oldNode);
                final T newEntity = entitiesById.remove(oldEntity.id());
                updated = saveObject(oldNode, newEntity, jsonGenerator) || updated;
            }

            // Create new entities
            for (T newEntity : entitiesById.values()) {
                updated = saveObject(null, newEntity, jsonGenerator) || updated;
            }

            jsonGenerator.writeEndArray();
        }
        finally {
            closeQuietly(jsonGenerator);
            closeQuietly(itr);
        }

        return updated;
    }

    protected <T extends Identifiable> boolean saveObject(JsonNode oldNode, T newEntity, JsonGenerator jsonGenerator) throws IOException {

        if (newEntity == null) {
            if (oldNode != null) {
                jsonGenerator.writeTree(oldNode);
            }
            return false;
        }
        else {
            final JsonNode newNode = OBJECT_MAPPER.valueToTree(newEntity);

            if (oldNode == null) {
                jsonGenerator.writeTree(newNode);
            }
            else {
                final Map<String, JsonNode> newEltsByFields = indexByFieldNames(newNode.fields());
                jsonGenerator.writeStartObject();

                // Update old fields
                final Iterator<Map.Entry<String, JsonNode>> oldFields = oldNode.fields();
                while (oldFields.hasNext()) {
                    final Map.Entry<String, JsonNode> oldField = oldFields.next();
                    final String name = oldField.getKey();
                    final JsonNode oldElt = oldField.getValue();

                    JsonNode newElt = newEltsByFields.remove(name);
                    if (newElt == null) {
                        newElt = oldElt;
                    }

                    jsonGenerator.writeFieldName(name);
                    jsonGenerator.writeTree(newElt);
                }

                // Create new fields
                for (Map.Entry<String, JsonNode> newField : newEltsByFields.entrySet()) {
                    final String name = newField.getKey();
                    final JsonNode newElt = newField.getValue();

                    jsonGenerator.writeFieldName(name);
                    jsonGenerator.writeTree(newElt);
                }

                jsonGenerator.writeEndObject();
            }

            return true;
        }
    }

    protected Map<String, JsonNode> indexByFieldNames(Iterator<Map.Entry<String, JsonNode>> fields) {
        requireNonNull(fields);

        final Map<String, JsonNode> indexed = new HashMap<>();
        while (fields.hasNext()) {
            final Map.Entry<String, JsonNode> field = fields.next();
            indexed.put(field.getKey(), field.getValue());
        }

        return indexed;
    }

    protected CloseableIterator<JsonNode> asJsonNodeIterator(Reader reader) throws IOException {
        final JacksonArrayIterator itr = new JacksonArrayIterator(reader);
        return new CloseableIterator<JsonNode>() {

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public JsonNode next() {
                try {
                    return OBJECT_MAPPER.readTree(itr.next());
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

    @Override
    protected CloseableIterator<Void> asSkippedValueIterator(Reader reader) throws IOException {
        final JacksonArrayIterator itr = new JacksonArrayIterator(reader);
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
    protected <T> CloseableIterator<T> asEntityIterator(Reader reader, final Type typeOfT) throws IOException {
        final JacksonArrayIterator itr = new JacksonArrayIterator(reader);
        final ObjectReader objectReader = buildObjectReader(typeOfT);
        return new CloseableIterator<T>() {

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @SuppressWarnings("unchecked")
            @Override
            public T next() {
                try {
                    return (T) objectReader.readValue(itr.next());
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

    private ObjectReader buildObjectReader(Type type) {
        final JavaType javaType = TYPE_FACTORY.constructType(type);
        return OBJECT_MAPPER.readerFor(javaType);
    }

    protected class JacksonArrayIterator extends AbstractIterator<JsonParser> implements CloseableIterator<JsonParser> {

        private final JsonParser parser;

        private boolean first = true;

        public JacksonArrayIterator(Reader reader) throws IOException {
            this.parser = JSON_FACTORY.createParser(reader);
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
