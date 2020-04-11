package fr.adbonnin.cz2128.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class JsonUtils {

    public static boolean updateObject(ObjectNode oldNode, ObjectNode newNode, JsonGenerator generator) throws IOException {
        requireNonNull(generator);

        if (newNode == null) {
            if (oldNode != null) {
                generator.writeTree(oldNode);
            }
            return false;
        }
        else {
            if (oldNode == null) {
                generator.writeTree(newNode);
            }
            else {
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
            }

            return true;
        }
    }

    public static LinkedHashMap<String, JsonNode> mapFieldsToLinkedHashMap(JsonNode node) {
        requireNonNull(node);

        final LinkedHashMap<String, JsonNode> map = new LinkedHashMap<>();
        final Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            final Map.Entry<String, JsonNode> field = fields.next();
            map.put(field.getKey(), field.getValue());
        }

        return map;
    }

    private JsonUtils() { /* Cannot be instantiated */ }
}