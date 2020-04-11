package fr.adbonnin.cz2128.json.array;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import fr.adbonnin.cz2128.JsonException;
import fr.adbonnin.cz2128.collect.AbstractIterator;
import fr.adbonnin.cz2128.collect.CloseableIterator;

import java.io.IOException;

import static com.fasterxml.jackson.core.JsonToken.*;
import static java.util.Objects.requireNonNull;

public class ArrayIterator extends AbstractIterator<JsonParser> implements CloseableIterator<JsonParser> {

    private final JsonParser parser;

    private boolean first = true;

    public ArrayIterator(JsonParser parser) {
        this.parser = requireNonNull(parser);
    }

    @Override
    protected JsonParser computeNext() {
        try {
            JsonToken token = parser.nextToken();

            if (first) {
                if (token == null) {
                    return endOfData();
                }
                else if (!START_ARRAY.equals(token)) {
                    throw new IllegalStateException("Parser must start with an array token");
                }
                else {
                    token = parser.nextToken();
                }
            }

            if (END_ARRAY.equals(token)) {
                return endOfData();
            }

            return parser;
        }
        catch (IOException e) {
            throw new JsonException(e);
        }
        finally {
            first = false;
        }
    }

    @Override
    public void close() {
        try {
            parser.close();
        }
        catch (IOException e) {
            throw new JsonException(e);
        }
    }
}