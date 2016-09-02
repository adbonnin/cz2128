package fr.adbonnin.albedo.util.io.serializer;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import fr.adbonnin.albedo.util.Identifiable;
import fr.adbonnin.albedo.util.Predicate;
import fr.adbonnin.albedo.util.collect.AbstractIterator;
import fr.adbonnin.albedo.util.io.CloseableIterator;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static com.google.gson.stream.JsonToken.*;
import static fr.adbonnin.albedo.util.IdentifiableUtils.indexByIds;
import static fr.adbonnin.albedo.util.io.IOUtils.closeQuietly;
import static java.util.Objects.requireNonNull;

public class GsonArraySerializer extends IterableArraySerializer {

    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .create();

    private static final JsonParser JSON_PARSER = new JsonParser();

    @Override
    public <E extends Identifiable> boolean delete(Predicate<E> predicate, Reader reader, Writer writer, Type typeOfT) throws IOException {
        boolean updated = false;

        CloseableIterator<JsonObject> itr = null;
        JsonWriter jsonWriter = null;
        try {
            jsonWriter = GSON.newJsonWriter(writer);
            jsonWriter.beginArray();

            itr = asJsonObjectIterator(reader);
            while (itr.hasNext()) {
                final JsonObject node = itr.next();
                final E element = GSON.fromJson(node, typeOfT);

                if (predicate.evaluate(element)) {
                    updated = true;
                }
                else {
                    GSON.toJson(node, jsonWriter);
                }
            }

            jsonWriter.endArray();
        }
        finally {
            closeQuietly(jsonWriter);
            closeQuietly(itr);
        }
        return updated;
    }

    @Override
    public <E extends Identifiable> boolean save(Iterable<E> elements, Reader reader, Writer writer, Type typeOfT) throws IOException {
        final Map<Object, E> elementsById = indexByIds(elements);
        boolean updated = false;

        CloseableIterator<JsonObject> itr = null;
        JsonWriter jsonWriter = null;
        try {
            jsonWriter = GSON.newJsonWriter(writer);
            jsonWriter.beginArray();

            // Update old elements
            itr = asJsonObjectIterator(reader);
            while (itr.hasNext()) {
                final JsonObject oldNode = itr.next();
                final E oldElement = GSON.fromJson(oldNode, typeOfT);
                final E newElement = elementsById.remove(oldElement.id());
                updated = saveObject(oldNode, newElement, jsonWriter, typeOfT) || updated;
            }

            // Create new elements
            for (E newElement : elementsById.values()) {
                updated = saveObject(null, newElement, jsonWriter, typeOfT) || updated;
            }

            jsonWriter.endArray();
        }
        finally {
            closeQuietly(jsonWriter);
            closeQuietly(itr);
        }
        return updated;
    }

    protected <E extends Identifiable> boolean saveObject(JsonObject oldNode, E newElement, JsonWriter jsonWriter, Type typeOfT) throws IOException {

        if (newElement == null) {
            if (oldNode != null) {
                GSON.toJson(oldNode, jsonWriter);
            }
            return false;
        }
        else {
            final JsonElement newJsonElement = GSON.toJsonTree(newElement, typeOfT);
            final JsonObject newNode = newJsonElement.getAsJsonObject();

            if (oldNode == null) {
                GSON.toJson(newNode, jsonWriter);
            }
            else {
                final Map<String, JsonElement> newEltsByFields = indexByFieldNames(newNode.entrySet());
                jsonWriter.beginObject();

                // Update old fields
                for (Map.Entry<String, JsonElement> oldField : oldNode.entrySet()) {
                    final String name = oldField.getKey();
                    final JsonElement oldElt = oldField.getValue();

                    JsonElement newElt = newEltsByFields.remove(name);
                    if (newElt == null) {
                        newElt = oldElt;
                    }

                    jsonWriter.name(name);
                    GSON.toJson(newElt, jsonWriter);
                }

                // Create new fields
                for (Map.Entry<String, JsonElement> newField : newEltsByFields.entrySet()) {
                    final String name = newField.getKey();
                    final JsonElement newElt = newField.getValue();

                    jsonWriter.name(name);
                    GSON.toJson(newElt, jsonWriter);
                }

                jsonWriter.endObject();
            }
            return true;
        }
    }

    protected Map<String, JsonElement> indexByFieldNames(Iterable<Map.Entry<String, JsonElement>> fields) {
        requireNonNull(fields);

        final Map<String, JsonElement> indexed = new HashMap<>();
        for (Map.Entry<String, JsonElement> field : fields) {
            indexed.put(field.getKey(), field.getValue());
        }

        return indexed;
    }

    protected CloseableIterator<JsonObject> asJsonObjectIterator(Reader reader) {
        final GsonArrayIterator itr = new GsonArrayIterator(reader);
        return new CloseableIterator<JsonObject>() {

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public JsonObject next() {
                final JsonElement element = JSON_PARSER.parse(itr.next());
                return element.getAsJsonObject();
            }

            @Override
            public void remove() {
                itr.hasNext();
            }

            @Override
            public void close() throws IOException {
                itr.close();
            }
        };
    }

    @Override
    protected <E> CloseableIterator<E> asElementIterator(Reader reader, final Type typeOfT) {
        final GsonArrayIterator itr = new GsonArrayIterator(reader);
        return new CloseableIterator<E>() {

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public E next() {
                return GSON.fromJson(itr.next(), typeOfT);
            }

            @Override
            public void remove() {
                itr.hasNext();
            }

            @Override
            public void close() throws IOException {
                itr.close();
            }
        };
    }

    @Override
    protected CloseableIterator<Void> asSkippedValueIterator(Reader reader) {
        final GsonArrayIterator itr = new GsonArrayIterator(reader);
        return new CloseableIterator<Void>() {

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public Void next() {
                try {
                    itr.next().skipValue();
                }
                catch (IOException e) {
                    throw new JsonSyntaxException(e);
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

    protected class GsonArrayIterator extends AbstractIterator<JsonReader> implements CloseableIterator<JsonReader> {

        private final JsonReader reader;

        private boolean first = true;

        public GsonArrayIterator(Reader reader) {
            this.reader = createJsonReader(reader);
        }

        private JsonReader createJsonReader(Reader reader) {
            final JsonReader jsonReader = GSON.newJsonReader(reader);
            jsonReader.setLenient(true);
            return jsonReader;
        }

        @Override
        protected JsonReader computeNext() {
            boolean isEmpty = first;
            try {
                JsonToken token = reader.peek();
                isEmpty = false;

                if (first) {
                    if (!BEGIN_ARRAY.equals(token)) {
                        throw new IllegalStateException("JsonReader must start with an array");
                    }
                    else {
                        reader.beginArray();
                        token = reader.peek();
                    }
                }

                if (END_ARRAY.equals(token)) {
                    return endOfData();
                }
                else if (!BEGIN_OBJECT.equals(token)) {
                    throw new IllegalStateException("Array must contains objects");
                }

                return reader;
            }
            catch (EOFException e) {
                if (isEmpty) {
                    return endOfData();
                }
                throw new JsonSyntaxException(e);
            }
            catch (IllegalStateException | IOException e) {
                throw new JsonSyntaxException(e);
            }
            finally {
                first = false;
            }
        }

        @Override
        public void close() throws IOException {
            reader.close();
        }
    }
}
