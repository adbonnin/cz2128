package fr.adbonnin.cz2128.schema.reader;

import com.fasterxml.jackson.databind.JsonNode;
import fr.adbonnin.cz2128.schema.Constants;
import fr.adbonnin.cz2128.schema.XtraSchema;
import fr.adbonnin.cz2128.schema.SchemaType;

import static fr.adbonnin.cz2128.schema.XtraSchema.dateType;

public class DateSchemaTypeReader implements SchemaTypeReader {

    @Override
    public boolean canHandle(String type) {
        return Constants.TYPE_DATE.equals(type);
    }

    @Override
    public SchemaType readSchemaType(JsonNode node) {

        if (!node.isObject()) {
            return XtraSchema.dateType();
        }

        final JsonNode datePattern = node.get(Constants.FIELD_DATE_PATTERN);
        return datePattern != null && datePattern.isTextual() ? XtraSchema.dateType(datePattern.asText()) : XtraSchema.dateType();
    }
}

