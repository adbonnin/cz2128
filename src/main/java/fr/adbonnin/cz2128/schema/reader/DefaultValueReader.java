package fr.adbonnin.cz2128.schema.reader;

import com.fasterxml.jackson.databind.JsonNode;
import fr.adbonnin.cz2128.schema.Constants;
import fr.adbonnin.cz2128.schema.Schema;

public class DefaultValueReader implements PostReader {

    private static final DefaultValueReader INSTANCE = new DefaultValueReader();

    @Override
    public void postRead(Schema value, JsonNode node, Iterable<PostReader> postReader) {
        final JsonNode defaultValue = node.get(Constants.FIELD_DEFAULT_VALUE);
        if (defaultValue != null) {
            value.setDefaultValue(defaultValue);
        }
    }

    public static DefaultValueReader instance() {
        return INSTANCE;
    }
}

