package fr.adbonnin.cz2128.schema.validator.reader;

import com.fasterxml.jackson.databind.JsonNode;
import fr.adbonnin.cz2128.schema.Constants;
import fr.adbonnin.cz2128.schema.Schema;
import fr.adbonnin.cz2128.schema.reader.PostReader;
import fr.adbonnin.cz2128.schema.validator.SizeValidator;

public class SizeValidatorReader implements PostReader {

    @Override
    public void postRead(Schema value, JsonNode node, Iterable<PostReader> postReaders) {
        boolean addValidator = false;
        int min = 0;
        int max = Integer.MAX_VALUE;

        final JsonNode sizeNode = node.get(Constants.FIELD_SIZE);
        if (sizeNode != null) {
            final int size = sizeNode.asInt();
            min = size;
            max = size;
            addValidator = true;
        }
        else {
            final JsonNode minNode = node.get(Constants.FIELD_MIN_SIZE);
            if (minNode != null) {
                min = minNode.asInt();
                addValidator = true;
            }

            final JsonNode maxNode = node.get(Constants.FIELD_MAX_SIZE);
            if (maxNode != null) {
                max = maxNode.asInt();
                addValidator = true;
            }
        }

        if (addValidator) {
            value.addValidator(new SizeValidator(min, max));
        }
    }
}

