package fr.adbonnin.cz2128.schema.validator.reader;

import com.fasterxml.jackson.databind.JsonNode;
import fr.adbonnin.cz2128.schema.Constants;
import fr.adbonnin.cz2128.schema.Schema;
import fr.adbonnin.cz2128.schema.reader.PostReader;
import fr.adbonnin.cz2128.schema.validator.RequiredValidator;

public class RequiredValidatorReader implements PostReader {

    @Override
    public void postRead(Schema value, JsonNode node, Iterable<PostReader> postReader) {

        final JsonNode required = node.get(Constants.FIELD_REQUIRED);
        if (required == null) {
            return;
        }

        if (!required.asBoolean(false)) {
            return;
        }

        value.addValidator(RequiredValidator.INSTANCE);
    }
}

