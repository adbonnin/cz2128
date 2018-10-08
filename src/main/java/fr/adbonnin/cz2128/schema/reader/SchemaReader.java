package fr.adbonnin.cz2128.schema.reader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.adbonnin.cz2128.schema.*;
import fr.adbonnin.cz2128.schema.validator.reader.EqualsToValidatorReader;
import fr.adbonnin.cz2128.schema.validator.reader.RequiredValidatorReader;
import fr.adbonnin.cz2128.schema.validator.reader.SizeValidatorReader;

import java.io.IOException;
import java.util.*;

public class SchemaReader {

    private static final SchemaTypeReader[] DEFAULT_SCHEMA_TYPE_READERS = {
        new BaseSchemaTypeReader(Constants.TYPE_BOOLEAN, XtraSchema.booleanType()),
        new BaseSchemaTypeReader(Constants.TYPE_NUMBER, XtraSchema.numberType()),
        new BaseSchemaTypeReader(Constants.TYPE_STRING, XtraSchema.stringType()),
        new DateSchemaTypeReader()
    };

    private static final PostReader[] DEFAULT_POST_READERS = {
        DefaultValueReader.instance(),
        new RequiredValidatorReader(),
        new EqualsToValidatorReader(),
        new SizeValidatorReader(),
        //new MinMaxValidatorReader()
    };

    private final ObjectMapper mapper;

    private final List<SchemaTypeReader> schemaTypeReaders = new ArrayList<>();

    private final List<PostReader> postReaders = new ArrayList<>();

    public SchemaReader(JsonFactory factory) {
        this.mapper = new ObjectMapper(factory);
        this.schemaTypeReaders.addAll(Arrays.asList(DEFAULT_SCHEMA_TYPE_READERS));
        this.postReaders.addAll(Arrays.asList(DEFAULT_POST_READERS));
    }

    public Schema readSchema(JsonParser parser) throws IOException {
        final JsonNode node = mapper.readTree(parser);
        return readSchema(node);
    }

    public void addSchemaTypeReader(SchemaTypeReader schemaTypeReader) {
        this.schemaTypeReaders.add(schemaTypeReader);
    }

    public void addPostReader(PostReader postReader) {
        this.postReaders.add(postReader);
    }

    public Schema readSchema(JsonNode node) throws IOException {

        final Schema type = readType(node);
        if (type != null) {
            return type;
        }

        if (node.isObject()) {
            final JsonNode typeNode = node.get(Constants.FIELD_TYPE);
            final Schema objectType = readType(typeNode);
            if (objectType != null) {
                postProcess(objectType, node);
                return objectType;
            }

            return readObjectField(node);
        }

        throw new IllegalArgumentException("Parsed field is not well formed ; node: " + node);
    }

    private Schema readType(JsonNode node) throws IOException {

        if (node == null) {
            return null;
        }

        if (node.isTextual()) {
            final SchemaType schemaType = toSchemaType(node);
            return newFieldSchema(schemaType);
        }
        else if (node.isArray()) {
            return readArrayField(node);
        }
        else {
            return null;
        }
    }

    private SchemaType toSchemaType(JsonNode node) {
        final String type = node.textValue();
        for (SchemaTypeReader schemaTypeReader : schemaTypeReaders) {
            if (schemaTypeReader.canHandle(type)) {
                return schemaTypeReader.readSchemaType(node);
            }
        }
        throw new IllegalArgumentException("Schema type can't be found ; type: " + type);
    }

    /**
     *
     * @param value
     * @param node (ObjectNode)
     */
    private void postProcess(Schema value, JsonNode node) {
        for (PostReader postReader : postReaders) {
            postReader.postRead(value, node, postReaders);
        }
    }

    /**
     *
     * @param node (ArrayNode)
     * @return
     * @throws IOException
     */
    private Schema readArrayField(JsonNode node) throws IOException {

        if (node.size() != 1) {
            throw new IllegalArgumentException("Array node must contains only one value ; node: " + node);
        }

        final JsonNode firstNode = node.get(0);
        final Schema arrayOf = readSchema(firstNode);
        return newArraySchema(arrayOf);
    }

    /**
     *
     * @param node (ObjectNode)
     * @return
     * @throws IOException
     */
    private Schema readObjectField(JsonNode node) throws IOException {
        final Schema object = newObjectSchema();

        final Iterator<Map.Entry<String, JsonNode>> jsonFields = node.fields();
        while (jsonFields.hasNext()) {
            final Map.Entry<String, JsonNode> jsonField = jsonFields.next();
            final Schema field = readSchema(jsonField.getValue());
            object.addField(jsonField.getKey(), field);
        }

        return object;
    }

    protected Schema newFieldSchema(SchemaType schemaType) {
        return new FieldSchema(schemaType);
    }

    protected Schema newArraySchema(Schema arrayOf) {
        return new ArraySchema(arrayOf);
    }

    protected Schema newObjectSchema() {
        return new ObjectSchema();
    }
}

