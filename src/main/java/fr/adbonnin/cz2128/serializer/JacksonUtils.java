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
                final ObjectNode newNodeCopy = newNode.deepCopy();
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
                final Iterator<Map.Entry<String, JsonNode>> newFields = newNodeCopy.fields();
                while (newFields.hasNext()) {
                    final Map.Entry<String, JsonNode> newField = newFields.next();
                    generator.writeFieldName(newField.getKey());
                    generator.writeTree(newField.getValue());
                }

                generator.writeEndObject();
            }

            return true;
        }
    }

    private JacksonUtils() { /* Cannot be instantiated */ }
}
