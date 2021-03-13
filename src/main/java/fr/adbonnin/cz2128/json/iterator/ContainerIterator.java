package fr.adbonnin.cz2128.json.iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import fr.adbonnin.cz2128.collect.AbstractIterator;
import fr.adbonnin.cz2128.json.JsonException;

import java.io.IOException;

import static com.fasterxml.jackson.core.JsonToken.VALUE_NULL;
import static java.util.Objects.requireNonNull;

public abstract class ContainerIterator<T> extends AbstractIterator<T> {

    private final JsonParser parser;

    private boolean first = true;

    protected abstract void checkStartToken(JsonToken token);

    protected abstract boolean isEndToken(JsonToken token);

    protected abstract T nextValue(JsonToken token) throws IOException;

    public ContainerIterator(JsonParser parser) {
        this.parser = requireNonNull(parser);
    }

    public JsonParser getParser() {
        return parser;
    }

    @Override
    protected T computeNext() {
        try {
            JsonToken token;
            if (first) {
                first = false;

                token = parser.hasCurrentToken()
                    ? parser.getCurrentToken()
                    : parser.nextToken();

                if (token == null || VALUE_NULL.equals(token)) {
                    return endOfData();
                }

                checkStartToken(token);
            }

            token = parser.nextToken();

            if (isEndToken(token)) {
                return endOfData();
            }

            return nextValue(token);
        }
        catch (IOException e) {
            throw new JsonException(e);
        }
    }
}
