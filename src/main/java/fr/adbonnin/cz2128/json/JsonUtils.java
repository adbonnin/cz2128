package fr.adbonnin.cz2128.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.adbonnin.cz2128.JsonException;
import fr.adbonnin.cz2128.JsonProvider;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JsonUtils {

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    public static JsonUpdateStrategy partialUpdate() {
        return JSON_UPDATE_STRATEGIES.PARTIAL_UPDATE;
    }

    public static JsonUpdateStrategy replaceUpdate() {
        return JSON_UPDATE_STRATEGIES.REPLACE_UPDATE;
    }

    public static JsonParser newEmptyParser(ObjectMapper mapper) throws IOException {
        return mapper.getFactory().createParser(EMPTY_BYTE_ARRAY);
    }

    public static LinkedHashMap<String, JsonNode> mapFieldsToLinkedHashMap(JsonNode node) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(node.fields(), 0), false)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public static JsonNode readJsonNode(JsonProvider provider, ObjectMapper mapper) {
        return provider.withParser(mapper, JSON_NODE_PARSER);
    }

    public static void writeJsonNode(JsonNode node, JsonProvider provider, ObjectMapper mapper) {
        provider.withGenerator(mapper, (parser, generator) -> {
            try {
                generator.writeTree(node);
                return null;
            }
            catch (IOException e) {
                throw new JsonException(e);
            }
        });
    }

    private static final Function<JsonParser, JsonNode> JSON_NODE_PARSER = parser -> {
        try {
            return parser.readValueAsTree();
        }
        catch (IOException e) {
            throw new JsonException(e);
        }
    };

    private enum JSON_UPDATE_STRATEGIES implements JsonUpdateStrategy {
        PARTIAL_UPDATE {
            @Override
            public boolean update(JsonNode oldNode, JsonNode newNode, JsonGenerator generator) throws IOException {

                if (oldNode == null) {
                    if (newNode == null) {
                        return false;
                    }
                    else {
                        generator.writeTree(newNode);
                        return true;
                    }
                }

                if (newNode == null || newNode.isEmpty()) {
                    generator.writeTree(oldNode);
                    return false;
                }

                if (oldNode.isObject() && newNode.isObject()) {
                    return updateObject((ObjectNode) oldNode, (ObjectNode) newNode, generator);
                }
                else if (oldNode.isArray() && newNode.isArray()) {
                    return updateArray((ArrayNode) oldNode, (ArrayNode) newNode, generator);
                }
                else {
                    generator.writeTree(newNode);
                    return !newNode.equals(oldNode);
                }
            }

            private boolean updateObject(ObjectNode oldNode, ObjectNode newNode, JsonGenerator generator) throws IOException {

                final Map<String, JsonNode> newNodeCopy = mapFieldsToLinkedHashMap(newNode);
                generator.writeStartObject();

                // Update old fields
                final Iterator<Map.Entry<String, JsonNode>> oldFields = oldNode.fields();
                while (oldFields.hasNext()) {
                    final Map.Entry<String, JsonNode> oldField = oldFields.next();
                    final String name = oldField.getKey();

                    JsonNode newElt = newNodeCopy.remove(name);
                    if (newElt == null) {
                        newElt = oldField.getValue();
                    }

                    generator.writeFieldName(name);
                    generator.writeTree(newElt);
                }

                // Create new fields
                for (Map.Entry<String, JsonNode> newField : newNodeCopy.entrySet()) {
                    generator.writeFieldName(newField.getKey());
                    generator.writeTree(newField.getValue());
                }

                generator.writeEndObject();
                return true;
            }

            private boolean updateArray(ArrayNode oldNode, ArrayNode newNode, JsonGenerator generator) throws IOException {

                if (oldNode.size() <= newNode.size()) {
                    generator.writeTree(newNode);
                    return true;
                }

                generator.writeStartArray();

                final Iterator<JsonNode> newElts = newNode.iterator();
                boolean newEltsHasNext = true;

                for (JsonNode oldElt : oldNode) {
                    newEltsHasNext &= newElts.hasNext();
                    generator.writeTree(newEltsHasNext ? newElts.next() : oldElt);
                }

                generator.writeEndArray();
                return true;
            }
        },

        REPLACE_UPDATE {
            @Override
            public boolean update(JsonNode oldNode, JsonNode newNode, JsonGenerator generator) throws IOException {

                if (newNode == null) {
                    if (oldNode != null) {
                        generator.writeTree(oldNode);
                    }
                    return false;
                }

                generator.writeTree(newNode);
                return !newNode.equals(oldNode);
            }
        }
    }

    private JsonUtils() { /* Cannot be instantiated */ }
}
