package fr.adbonnin.cz2128.schema.reader;

import com.fasterxml.jackson.databind.JsonNode;
import fr.adbonnin.cz2128.schema.SchemaType;

import static java.util.Objects.requireNonNull;

public class BaseSchemaTypeReader implements SchemaTypeReader {

    private final String name;

    private final SchemaType type;

    public BaseSchemaTypeReader(String name, SchemaType type) {
        this.name = requireNonNull(name);
        this.type = requireNonNull(type);
    }

    @Override
    public boolean canHandle(String type) {
        return name.equals(type);
    }

    @Override
    public SchemaType readSchemaType(JsonNode node) {
        return type;
    }
}
