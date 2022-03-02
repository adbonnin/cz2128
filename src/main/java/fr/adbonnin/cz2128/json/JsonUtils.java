package fr.adbonnin.cz2128.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.adbonnin.cz2128.io.StreamUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JsonUtils {

    public static final JsonEncoding DEFAULT_ENCODING = JsonEncoding.UTF8;

    private static final JsonFactory DEFAULT_EMPTY_JSON_PARSER_FACTORY = new JsonFactory(new JsonMapper());

    public static JsonParser newEmptyParser() throws IOException {
        return DEFAULT_EMPTY_JSON_PARSER_FACTORY.createParser(StreamUtils.nullInputStream());
    }

    public static LinkedHashMap<String, JsonNode> mapFieldsToLinkedHashMap(JsonNode node) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(node.fields(), 0), false)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public static JsonNode readNode(JsonProvider provider) {
        return provider.withParser(JSON_NODE_READER);
    }

    public static void writeNode(JsonNode node, JsonProvider provider) {
        provider.withGenerator((parser, generator) -> {
            try {
                generator.writeTree(node);
                return null;
            }
            catch (IOException e) {
                throw new JsonException(e);
            }
        });
    }

    public static boolean partialUpdate(JsonNode oldNode, JsonNode newNode, JsonGenerator generator) throws IOException {

        if (oldNode == null && newNode == null) {
            return false;
        }
        else if (oldNode == null) {
            generator.writeTree(newNode);
            return true;
        }
        else if (newNode == null) {
            generator.writeTree(oldNode);
            return false;
        }

        if (oldNode.isObject() && newNode.isObject()) {
            return partialUpdateObject((ObjectNode) oldNode, (ObjectNode) newNode, generator);
        }
        else if (oldNode.isArray() && newNode.isArray()) {
            return partialUpdateArray((ArrayNode) oldNode, (ArrayNode) newNode, generator);
        }
        else {
            generator.writeTree(newNode);
            return !newNode.equals(oldNode);
        }
    }

    public static boolean partialUpdateObject(ObjectNode oldNode, ObjectNode newNode, JsonGenerator generator) throws IOException {

        if (oldNode == null && newNode == null) {
            return false;
        }
        else if (oldNode == null) {
            generator.writeTree(newNode);
            return true;
        }
        else if (newNode == null || newNode.isEmpty()) {
            generator.writeTree(oldNode);
            return false;
        }

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

    public static boolean partialUpdateArray(ArrayNode oldNode, ArrayNode newNode, JsonGenerator generator) throws IOException {

        if (oldNode == null && newNode == null) {
            return false;
        }
        else if (oldNode == null) {
            generator.writeTree(newNode);
            return true;
        }
        else if (newNode == null || newNode.isEmpty()) {
            generator.writeTree(oldNode);
            return false;
        }
        else if (oldNode.size() <= newNode.size()) {
            generator.writeTree(newNode);
            return true;
        }

        generator.writeStartArray();

        final Iterator<JsonNode> newElements = newNode.iterator();
        boolean hasNewElements = true;

        for (JsonNode oldElement : oldNode) {
            hasNewElements &= newElements.hasNext();
            generator.writeTree(hasNewElements ? newElements.next() : oldElement);
        }

        generator.writeEndArray();
        return true;
    }

    private static final Function<JsonParser, JsonNode> JSON_NODE_READER = parser -> {
        try {
            return parser.readValueAsTree();
        }
        catch (IOException e) {
            throw new JsonException(e);
        }
    };

    private JsonUtils() { /* Cannot be instantiated */ }
}
