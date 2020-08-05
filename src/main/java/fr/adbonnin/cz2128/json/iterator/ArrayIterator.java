package fr.adbonnin.cz2128.json.iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import static com.fasterxml.jackson.core.JsonToken.END_ARRAY;
import static com.fasterxml.jackson.core.JsonToken.START_ARRAY;

public class ArrayIterator extends ContainerIterator<JsonParser> {

    public ArrayIterator(JsonParser parser) {
        super(parser);
    }

    @Override
    protected void checkStartToken(JsonToken token) {
        if (!START_ARRAY.equals(token)) {
            throw new IllegalStateException("Parser must start with an array token");
        }
    }

    @Override
    protected boolean isEndToken(JsonToken token) {
        return END_ARRAY.equals(token);
    }

    @Override
    protected JsonParser nextValue(JsonToken token) {
        return getParser();
    }
}
