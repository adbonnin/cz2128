package fr.adbonnin.cz2128.schema.validator.reader;

import com.fasterxml.jackson.databind.JsonNode;
import fr.adbonnin.cz2128.schema.Constants;
import fr.adbonnin.cz2128.schema.Schema;
import fr.adbonnin.cz2128.schema.reader.PostReader;
import fr.adbonnin.cz2128.schema.validator.EqualsToValidator;

public class EqualsToValidatorReader implements PostReader {

    @Override
    public void postRead(Schema value, JsonNode node, Iterable<PostReader> postReader) {

        final JsonNode equalsToNode = node.get(Constants.FIELD_EQUALS_TO);
        if (equalsToNode == null) {
            return;
        }

        value.addValidator(new EqualsToValidator(equalsToNode));
    }
}

