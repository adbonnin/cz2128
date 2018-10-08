package fr.adbonnin.cz2128.schema.reader;

import com.fasterxml.jackson.databind.JsonNode;
import fr.adbonnin.cz2128.schema.SchemaType;

public interface SchemaTypeReader {

    boolean canHandle(String type);

    SchemaType readSchemaType(JsonNode node);
}

