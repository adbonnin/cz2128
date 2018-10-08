package fr.adbonnin.cz2128.schema.validator;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import fr.adbonnin.cz2128.schema.Validator;

public class RequiredValidator implements Validator {

    public static final RequiredValidator INSTANCE = new RequiredValidator();

    @Override
    public boolean evaluate(JsonNode value) {
        return value != null && value.asToken() != JsonToken.VALUE_NULL;
    }
}

