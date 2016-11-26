package fr.adbonnin.cz2128.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
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

    public static boolean readToField(JsonParser parser, String field) throws IOException {

        JsonToken token = skipStructStart(parser);
        if (token == null) {
            return false;
        }

        while (true) {

            if (token.id() != JsonTokenId.ID_FIELD_NAME) {
                return false;
            }
            else if (field.equals(token.name())) {
                return true;
            }

            parser.skipChildren();

            token = parser.nextToken();
            if (token == null) {
                return false;
            }
        }
    }

    public static void writeToField(JsonParser parser, JsonGenerator generator, String field) throws IOException {

        JsonToken token = skipStructStart(parser);
        if (token == null) {
            return;
        }

        while (true) {

            if (token.id() != JsonTokenId.ID_FIELD_NAME) {
                return;
            }
            else if (field.equals(token.name())) {
                return;
            }

            generator.copyCurrentStructure(parser);
        }
    }

    public static void copyCurrent(JsonParser parser, JsonGenerator generator) throws IOException {
        while (true) {
            final JsonToken token = parser.currentToken();
            if (token == null || token.isStructEnd()) {
                return;
            }

            generator.copyCurrentStructure(parser);
        }
    }

    public static JsonToken skipStructStart(JsonParser parser) throws IOException {

        JsonToken token = parser.currentToken();
        if (token == null) {
            return null;
        }
        else if (token.isStructStart()) {
            parser.nextToken();
        }

        return token;
    }

    private JacksonUtils() { /* Cannot be instantiated */ }
}
