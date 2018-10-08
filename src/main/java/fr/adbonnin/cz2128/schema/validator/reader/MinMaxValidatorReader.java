package fr.adbonnin.cz2128.schema.validator.reader;

import com.fasterxml.jackson.databind.JsonNode;
import fr.adbonnin.cz2128.schema.Schema;
import fr.adbonnin.cz2128.schema.Validator;
import fr.adbonnin.cz2128.schema.reader.PostReader;
import fr.adbonnin.cz2128.schema.validator.MinDecimalValidator;
import fr.adbonnin.cz2128.schema.validator.MinValidator;

import java.math.BigDecimal;

public abstract class MinMaxValidatorReader implements PostReader {

    private final String fieldName;

    private final String inclusiveFieldName;

    public MinMaxValidatorReader(String fieldName, String inclusiveFieldName) {
        this.fieldName = fieldName;
        this.inclusiveFieldName = inclusiveFieldName;
    }

    @Override
    public void postRead(Schema value, JsonNode node, Iterable<PostReader> postReaders) {

        final JsonNode minNode = node.get(fieldName);
        if (minNode == null) {
            return;
        }

        final boolean inclusive = readInclusive(node);
        final Validator validator = readMinValidator(node, inclusive);
        value.addValidator(validator);
    }

    private boolean readInclusive(JsonNode node) {
        return false; // TODO
    }

    private Validator readMinValidator(JsonNode field, boolean inclusive) {
        if (field.isFloatingPointNumber()) {
            final BigDecimal min = field.decimalValue();
            return new MinDecimalValidator(min, inclusive);
        }
        else if (field.isTextual()) {
            final BigDecimal min = new BigDecimal(field.textValue());
            return new MinDecimalValidator(min, inclusive);
        }
        else {
            final long min = field.longValue();
            return new MinValidator(min, inclusive);
        }
    }
}

