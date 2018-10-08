package fr.adbonnin.cz2128.schema;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

public final class TestUtils {

    public static JsonFactory buildFactory() throws IOException {
        return new JsonFactory()
            .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
            .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    private TestUtils() { /* Cannot be instantiated */ }
}

