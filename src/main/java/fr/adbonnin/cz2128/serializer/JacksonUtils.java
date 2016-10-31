package fr.adbonnin.cz2128.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public final class JacksonUtils {

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
                final Map<String, JsonNode> newEltsByFields = indexByFields(newNode);
                generator.writeStartObject();

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

                    generator.writeFieldName(name);
                    generator.writeTree(newElt);
                }

                // Create new fields
                for (Map.Entry<String, JsonNode> newField : newEltsByFields.entrySet()) {
                    final String name = newField.getKey();
                    final JsonNode newElt = newField.getValue();

                    generator.writeFieldName(name);
                    generator.writeTree(newElt);
                }

                generator.writeEndObject();
            }

            return true;
        }
    }

    public static Map<String, JsonNode> indexByFields(ObjectNode object) {
        final Iterator<Map.Entry<String, JsonNode>> fields = object.fields();
        final Map<String, JsonNode> indexed = new HashMap<>();

        while (fields.hasNext()) {
            final Map.Entry<String, JsonNode> field = fields.next();
            indexed.put(field.getKey(), field.getValue());
        }

        return indexed;
    }

    private JacksonUtils() { /* Cannot be instantiated */ }
}
