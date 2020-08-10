package fr.adbonnin.cz2128.json.iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import fr.adbonnin.cz2128.base.Pair;

import java.io.IOException;

import static com.fasterxml.jackson.core.JsonToken.*;

public class ObjectIterator extends ContainerIterator<Pair<String, JsonParser>> {

    public ObjectIterator(JsonParser parser) {
        super(parser);
    }

    @Override
    protected void checkStartToken(JsonToken token) {
        if (!START_OBJECT.equals(token)) {
            throw new IllegalStateException("Parser must start with an object token; " +
                "token: " + token);
        }
    }

    @Override
    protected boolean isEndToken(JsonToken token) {
        return END_OBJECT.equals(token);
    }

    @Override
    protected Pair<String, JsonParser> nextValue(JsonToken token) throws IOException {

        if (!FIELD_NAME.equals(token)) {
            throw new IllegalArgumentException("Object must contains field name before values; " +
                "token: " + token);
        }

        final JsonParser parser = getParser();

        final String name = parser.getCurrentName();
        parser.nextToken();
        return Pair.of(name, parser);
    }
}
